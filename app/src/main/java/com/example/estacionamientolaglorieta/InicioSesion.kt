package com.example.estacionamientolaglorieta

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.NoSuchProviderException
import javax.mail.Session
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class InicioSesion : AppCompatActivity() {
    val db = Firebase.firestore
    val storage = FirebaseStorage.getInstance()
    lateinit var imgView_logo: ImageView
    lateinit var text_recuperar_contrasenia: TextView
    lateinit var text_registro: TextView
    lateinit var btn_enviar_contrasenia: Button
    lateinit var btn_entrar: Button
    lateinit var et_email: EditText
    lateinit var et_contraseña: EditText
    lateinit var et_email_recuperar: EditText
    lateinit var progressBar: ProgressBar
    lateinit var linear_progress: LinearLayout
    var terminar_progressBar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Removing strict mode
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_inicio_sesion)

        instanciarComponentes()
        cargarLogo()
    }

    private fun instanciarComponentes(){
        imgView_logo = findViewById(R.id.imgView_logo)
        progressBar = findViewById(R.id.progressBar_login)
        linear_progress = findViewById(R.id.linear_login_progress)
        et_email = findViewById(R.id.et_login_email)
        et_contraseña = findViewById(R.id.et_login_password)
        text_recuperar_contrasenia = findViewById(R.id.tV_login_remember_password)
        text_recuperar_contrasenia.setOnClickListener { mostrarAlertRecuperacion() }
        text_registro = findViewById(R.id.tV_login_register)
        text_registro.setOnClickListener{navegarPantallaRegistro()}
        btn_entrar = findViewById<Button>(R.id.btn_login)
        btn_entrar.setOnClickListener{validarDatosInicio()}
        Thread(Runnable {
            while(!terminar_progressBar){
                progressBar.progress += 5
                Thread.sleep(50)
            }
        }).start()
    }

    private fun cargarLogo(){
        db.collection("configuraciones").whereEqualTo("nombre", "Logo").get()
            .addOnSuccessListener { resultados ->
                if(resultados.size() == 1){
                    var logo_nombre = resultados.documents[0].get("valor").toString()
                    var storageReference = storage.getReferenceFromUrl("gs://estacionamiento-la-glorieta.appspot.com/"+logo_nombre)
                    val localFile = File.createTempFile("images", "")
                    storageReference.getFile(localFile)
                        .addOnSuccessListener { resultado ->
                            val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
                            imgView_logo.setImageBitmap(bitMap)
                            terminar_progressBar = true
                            linear_progress.visibility = View.GONE
                        }
                        .addOnFailureListener{ error ->
                            Snackbar.make(btn_entrar, error.toString(), Snackbar.LENGTH_SHORT).show()
                        }
                }else{
                    Snackbar.make(btn_entrar, "Error al cargar logo - Archivo no encontrado", Snackbar.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{ error ->
                Snackbar.make(btn_entrar, error.toString(), Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun mostrarAlertRecuperacion(){
        val alerta = AlertDialog.Builder(this)
        var customLayout = layoutInflater.inflate(R.layout.alert_layout_recuperar_contrasenia, null)
        et_email_recuperar = customLayout.findViewById(R.id.et_recuperaContra_correo)
        btn_enviar_contrasenia = customLayout.findViewById<Button>(R.id.btn_recuperaContra_enviar)
        alerta.setView(customLayout)
        val alerta_dialog = alerta.create()
        alerta_dialog.show()
        btn_enviar_contrasenia.setOnClickListener { recuperarContrasenia(customLayout, alerta_dialog) }
    }

    private fun recuperarContrasenia(view: View, alerta_dialog: AlertDialog){
        val correo = et_email_recuperar.text.toString()
        db.collection("usuarios").whereEqualTo("tipo", "Cliente").whereEqualTo("correo", correo).get()
                .addOnSuccessListener { resultado ->
                    if(resultado.size() == 1){
                        val contrasenia = resultado.documents[0].get("clavedeacceso").toString()
                        enviarCorreoRecuperacion(correo, contrasenia, alerta_dialog)
                    }else{
                        Snackbar.make(view, "Correo incorrecto o formato no válido.", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{ error ->
                    Snackbar.make(view, error.toString(), Snackbar.LENGTH_SHORT).show()
                }
    }

    fun enviarCorreoRecuperacion(correo: String, contrasenia: String, alerta_dialog: AlertDialog) {
        // Propiedades
        val properties = System.getProperties()
        properties["mail.smtp.host"] = "smtp.gmail.com"
        properties["mail.smtp.socketFactory.port"] = "465"
        properties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.port"] = "587"

        //Configuramos la sesión
        val session = Session.getDefaultInstance(properties, null)

        // Configuramos los valores de nuestro mensaje
        val mensaje = MimeMessage(session)
        try {
            mensaje.addRecipient(Message.RecipientType.TO, InternetAddress(correo))
            mensaje.subject = "Recuperación de contraseña - Estacionamiento La Glorieta"
            mensaje.setContent("""<h1 style='color:#3498db;'>Recuperar contraseña</h1>
    <p>Se ha solicitado la recuperación de contraseña para esta cuenta en App Estacionamiento 'La Glorieta'.
         <br>Tu contraseña es:
         <br><b>$contrasenia</b>
    </p>""", "text/html")
            // Configuramos como sera el envio del correo
            val transport = session.getTransport("smtp")
            transport.connect("smtp.gmail.com", "jesusrodriguezmx10@gmail.com", "chivas10")
            transport.sendMessage(mensaje, mensaje.allRecipients)
            transport.close()
            alerta_dialog.dismiss()
            Toast.makeText(this, "Se ha enviado un mensaje a tu correo electrónico", Toast.LENGTH_LONG).show()

        } catch (e: AddressException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun validarDatosInicio(){
        var email: String = et_email.text.toString()
        var contraseña: String = et_contraseña.text.toString()
        if(!email.trim().equals("") || !contraseña.trim().equals("")){
            db.collection("usuarios").whereEqualTo("tipo","Cliente")
                .whereEqualTo("correo", email).whereEqualTo("clavedeacceso", contraseña).get()
                .addOnSuccessListener { resultados ->
                    if(resultados.size() == 1) {
                        var datos = resultados.documents[0]
                        guardarPreferencias(datos.id, email, contraseña)
                        navegarPantallaPrincipal()
                    }else{
                        Snackbar.make(btn_entrar, "No se pudo iniciar sesión - Correo o contraseña incorrectos", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{ error ->
                    Snackbar.make(btn_entrar, error.toString(), Snackbar.LENGTH_SHORT).show()
                }
        }else{
            Snackbar.make(btn_entrar, "Ingrese su correo y contraseña", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun guardarPreferencias(id: String, email: String, contraseña: String){
        AppPreferences.claveUsuario = id
        AppPreferences.usuarioActual = email
        AppPreferences.contraseñaActual = contraseña
        AppPreferences.esIdentificado = true
    }

    private fun navegarPantallaPrincipal(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navegarPantallaRegistro(){
        val intent = Intent(this, RegistrarCliente::class.java)
        startActivity(intent)
    }

}