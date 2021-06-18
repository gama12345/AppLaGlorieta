package com.example.estacionamientolaglorieta

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class CuentaUsuario : AppCompatActivity() {
    private lateinit var et_nombre: EditText
    private lateinit var et_apellidos: EditText
    private lateinit var et_telefono: EditText
    private lateinit var et_calle: EditText
    private lateinit var et_colonia: EditText
    private lateinit var et_codigo_postal: EditText
    private lateinit var et_localidad: EditText
    private lateinit var et_correo: EditText
    private lateinit var et_contrasenia: EditText
    private lateinit var btn_guardar_cambios: Button
    lateinit var progressBar: ProgressBar
    lateinit var linear_progress: LinearLayout
    var terminar_progressBar = false
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta_usuario)
        db = FirebaseFirestore.getInstance()

        configurarBarraHerramientas()
        instanciarComponentes()
        cargarInformacion()
    }

    private fun configurarBarraHerramientas(){
        setSupportActionBar(findViewById(R.id.barra_herramientas))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val actionBar = supportActionBar
        actionBar?.setTitle("Cuenta")
    }

    private fun instanciarComponentes(){
        et_nombre = findViewById(R.id.et_cuenta_nombre)
        et_apellidos = findViewById(R.id.et_cuenta_apellidos)
        et_telefono = findViewById(R.id.et_cuenta_telefono)
        et_calle = findViewById(R.id.et_cuenta_calle)
        et_colonia = findViewById(R.id.et_cuenta_colonia)
        et_codigo_postal = findViewById(R.id.et_cuenta_codigo_postal)
        et_localidad = findViewById(R.id.et_cuenta_localidad)
        et_correo = findViewById(R.id.et_cuenta_correo)
        et_contrasenia = findViewById(R.id.et_cuenta_contraseña)
        btn_guardar_cambios = findViewById(R.id.btn_cuenta_guardar)
        btn_guardar_cambios.setOnClickListener { guardarCambios() }
        progressBar = findViewById(R.id.progressBar_registrar_vehiculo)
        linear_progress = findViewById(R.id.linear_registrar_vehiculo_progress)
        activarEfectoProgressBar()
    }

    private fun cargarInformacion(){
        val correoUsuario = AppPreferences.usuarioActual
        db.collection("usuarios").whereEqualTo("tipo", "Cliente").whereEqualTo("correo", correoUsuario).get()
                .addOnSuccessListener { resultado ->
                    if(resultado.size() == 1){
                        et_nombre.setText(resultado.documents[0].get("nombre").toString())
                        et_apellidos.setText(resultado.documents[0].get("apellidos").toString())
                        et_telefono.setText(resultado.documents[0].get("telefono").toString())
                        et_calle.setText(resultado.documents[0].get("calle").toString())
                        et_colonia.setText(resultado.documents[0].get("colonia").toString())
                        et_codigo_postal.setText(
                            resultado.documents[0].get("codigo_postal").toString()
                        )
                        et_localidad.setText(resultado.documents[0].get("localidad").toString())
                        et_correo.setText(correoUsuario)
                        et_contrasenia.setText(
                            resultado.documents[0].get("clavedeacceso").toString()
                        )
                    }else{
                        Snackbar.make(
                            btn_guardar_cambios,
                            "Error - " + resultado.size() + " coincidencias de usuarios",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    detenerEfectoProgressBar()
                }
                .addOnFailureListener{ error ->
                    Snackbar.make(btn_guardar_cambios, error.toString(), Snackbar.LENGTH_SHORT).show()
                    detenerEfectoProgressBar()
                }
    }

    private fun guardarCambios(){
        val nombre = et_nombre.text.toString()
        val apellidos = et_apellidos.text.toString()
        val telefono = et_telefono.text.toString()
        val calle = et_calle.text.toString()
        val colonia = et_colonia.text.toString()
        val codigo_postal = et_codigo_postal.text.toString()
        val localidad = et_localidad.text.toString()
        val correo = et_correo.text.toString()
        val contrasenia = et_contrasenia.text.toString()
        if(validarInformacion(
                nombre,
                apellidos,
                telefono,
                calle,
                colonia,
                codigo_postal,
                localidad,
                correo,
                contrasenia
            )){
                activarEfectoProgressBar()
            val datosActualizados = hashMapOf<String, Any>(
                "apellidos" to apellidos,
                "calle" to calle,
                "clavedeacceso" to contrasenia,
                "codigo_postal" to codigo_postal,
                "colonia" to colonia,
                "correo" to correo,
                "localidad" to localidad,
                "nombre" to nombre,
                "telefono" to telefono,
                "tipo" to "Cliente"
            )
            AppPreferences.usuarioActual = correo
            AppPreferences.contraseñaActual = contrasenia
            db.collection("usuarios").document(AppPreferences.claveUsuario).update(datosActualizados)
                    .addOnSuccessListener { resultado ->
                        detenerEfectoProgressBar()
                        Snackbar.make(
                            btn_guardar_cambios,
                            "Datos actualizados",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        detenerEfectoProgressBar()
                    }
                    .addOnFailureListener{ error ->
                        Snackbar.make(btn_guardar_cambios, error.toString(), Snackbar.LENGTH_SHORT).show()
                        detenerEfectoProgressBar()
                    }
        }
    }

    private fun validarInformacion(
        nombre: String, apellidos: String, telefono: String, calle: String, colonia: String,
        codigo_postal: String, localidad: String, correo: String, contrasenia: String): Boolean {
        if(!nombre.trim().equals("") && !apellidos.trim().equals("") && !telefono.trim().equals("") && !calle.trim().equals(
                ""
            )
                && !colonia.trim().equals("") && !codigo_postal.trim().equals("") && !localidad.trim().equals(
                ""
            )
                && !correo.trim().equals("") && !contrasenia.trim().equals("")){
            if(formatoNombre(nombre)){
                if(formatoNombre(apellidos)){
                    if(codigo_postal.length == 5){
                        if(formatoCorreo(correo)){
                            if(formatoContrasenia(contrasenia)){
                                return true;
                            }else{ Snackbar.make(
                                btn_guardar_cambios,
                                "Error en Contraseña - Formato incorrecto (Incluye al menos un caracter especial, número y mayúscula)",
                                Snackbar.LENGTH_SHORT
                            ).show() }
                        }else{ Snackbar.make(
                            btn_guardar_cambios,
                            "Error en Correo - Formato de correo electrónico incorrecto",
                            Snackbar.LENGTH_SHORT
                        ).show() }
                    }else{ Snackbar.make(
                        btn_guardar_cambios,
                        "Error en Código Postal - Formato incorrecto (5 digitos)",
                        Snackbar.LENGTH_SHORT
                    ).show() }
                }else{ Snackbar.make(
                    btn_guardar_cambios,
                    "Error en Apellidos - Formato incorrecto (Sólo letras)",
                    Snackbar.LENGTH_SHORT
                ).show() }
            }else{ Snackbar.make(
                btn_guardar_cambios,
                "Error en Nombre - Formato incorrecto (Sólo letras)",
                Snackbar.LENGTH_SHORT
            ).show() }
        }else{ Snackbar.make(
            btn_guardar_cambios,
            "Error - Uno o más campos están vacíos",
            Snackbar.LENGTH_SHORT
        ).show() }
        return false
    }

    fun activarEfectoProgressBar(){
        terminar_progressBar = true;
        Thread(Runnable {
            while(!terminar_progressBar){
                progressBar.progress += 5
                Thread.sleep(50)
            }
        }).start()
    }

    fun detenerEfectoProgressBar(){
        terminar_progressBar = true
        linear_progress.visibility = View.GONE
    }

    //Validaciones
    fun formatoNombre(cadena: String?): Boolean {
        return Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", cadena)
    }

    fun formatoCorreo(cadena: String?): Boolean {
        return Pattern.matches(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@+[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})+$",
            cadena
        )
    }

    fun formatoContrasenia(cadena: String?): Boolean {
        return Pattern.matches(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&\\*]).{6,}$",
            cadena
        )
    }

    //Acciones barra de herramientas
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}