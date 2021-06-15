package com.example.estacionamientolaglorieta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegistrarCliente : AppCompatActivity() {
    private lateinit var et_nombre: EditText
    private lateinit var et_apellidos: EditText
    private lateinit var et_telefono: EditText
    private lateinit var et_calle: EditText
    private lateinit var et_colonia: EditText
    private lateinit var et_codigo_postal: EditText
    private lateinit var et_localidad: EditText
    private lateinit var et_correo: EditText
    private lateinit var et_contraseña: EditText
    private lateinit var et_confirmar_contraseña: EditText
    private lateinit var btn_registrar: Button
    private lateinit var btn_cancelar: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)
        db = FirebaseFirestore.getInstance()
        instanciarComponentes()
    }

    //Funciones
    private fun instanciarComponentes(){
        et_nombre = findViewById(R.id.et_registro_nombre)
        et_apellidos = findViewById(R.id.et_registro_apellidos)
        et_telefono = findViewById(R.id.et_registro_telefono)
        et_calle = findViewById(R.id.et_registro_calle)
        et_colonia = findViewById(R.id.et_registro_colonia)
        et_codigo_postal = findViewById(R.id.et_registro_codigo_postal)
        et_localidad = findViewById(R.id.et_registro_localidad)
        et_correo = findViewById(R.id.et_registro_correo_electronico)
        et_contraseña = findViewById(R.id.et_registro_contraseña)
        et_confirmar_contraseña = findViewById(R.id.et_registro_confirmar_contraseña)
        btn_cancelar = findViewById(R.id.btn_registro_cancelar)
        btn_cancelar.setOnClickListener { navegarPantallaInicio() }
        btn_registrar = findViewById(R.id.btn_registro_registrar)
        btn_registrar.setOnClickListener { iniciarRegistro() }
    }

    private fun limpiarCampos(){
        et_nombre.setText("")
        et_apellidos.setText("")
        et_telefono.setText("")
        et_calle.setText("")
        et_colonia.setText("")
        et_codigo_postal.setText("")
        et_localidad.setText("")
        et_correo.setText("")
        et_contraseña.setText("")
        et_confirmar_contraseña.setText("")
    }

    private fun iniciarRegistro(){
        val nombre = et_nombre.text.toString()
        val apellidos = et_apellidos.text.toString()
        val telefono = et_telefono.text.toString()
        val calle = et_calle.text.toString()
        val colonia = et_colonia.text.toString()
        val codigo_postal = et_codigo_postal.text.toString()
        val localidad = et_localidad.text.toString()
        val correo = et_correo.text.toString()
        val contraseña = et_contraseña.text.toString()
        val confirmar_contraseña = et_confirmar_contraseña.text.toString()
        if(validarDatosRegistro(nombre, apellidos, telefono, calle, colonia, codigo_postal, localidad, correo, contraseña, confirmar_contraseña)){
            //Validando unicidad del correo electrónico
            db.collection("usuarios").whereEqualTo("tipo", "Cliente").whereEqualTo("correo", correo).get()
                    .addOnSuccessListener { resultado ->
                        if(resultado.size() == 0){
                            registrarDatos(nombre, apellidos, telefono, calle, colonia, codigo_postal, localidad, correo, contraseña)
                        }else{
                            Snackbar.make(btn_registrar, "Correo electrónico no válido. Intenta con uno diferente.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener{ error ->
                        Snackbar.make(btn_registrar, error.toString(), Snackbar.LENGTH_SHORT).show()
                    }
        }
    }

    private fun validarDatosRegistro(nombre: String, apellidos: String, telefono: String, calle: String, colonia: String,
                                     codigo_postal: String, localidad: String, correo: String, contrasenia: String, confirmar_contrasenia: String): Boolean {
        if(!nombre.trim().equals("") && !apellidos.trim().equals("") && !telefono.trim().equals("") && !calle.trim().equals("")
            && !colonia.trim().equals("") && !codigo_postal.trim().equals("") && !localidad.trim().equals("")
            && !correo.trim().equals("") && !contrasenia.trim().equals("") && !confirmar_contrasenia.trim().equals("")){
            if(formatoNombre(nombre)){
                if(formatoNombre(apellidos)){
                    if(codigo_postal.length == 5){
                        if(formatoCorreo(correo)){
                            if(formatoContrasenia(contrasenia)){
                                if(contrasenia.equals(confirmar_contrasenia)){
                                    return true;
                                }else{ Snackbar.make(btn_registrar, "Error en Contraseña - La contraseña no coincide con su confirmación", Snackbar.LENGTH_SHORT).show() }
                            }else{ Snackbar.make(btn_registrar, "Error en Contraseña - Formato incorrecto (Incluye al menos un caracter especial, número y mayúscula)", Snackbar.LENGTH_SHORT).show() }
                        }else{ Snackbar.make(btn_registrar, "Error en Correo - Formato de correo electrónico incorrecto", Snackbar.LENGTH_SHORT).show() }
                    }else{ Snackbar.make(btn_registrar, "Error en Código Postal - Formato incorrecto (5 digitos)", Snackbar.LENGTH_SHORT).show() }
                }else{ Snackbar.make(btn_registrar, "Error en Apellidos - Formato incorrecto (Sólo letras)", Snackbar.LENGTH_SHORT).show() }
            }else{ Snackbar.make(btn_registrar, "Error en Nombre - Formato incorrecto (Sólo letras)", Snackbar.LENGTH_SHORT).show() }
        }else{ Snackbar.make(btn_registrar, "Error - Uno o más campos están vacíos", Snackbar.LENGTH_SHORT).show() }
        return false
    }

    private fun registrarDatos(nombre: String, apellidos: String, telefono: String, calle: String, colonia: String,
                               codigo_postal: String, localidad: String, correo: String, contrasenia: String){
        val cliente = hashMapOf(
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
        db.collection("usuarios").add(cliente)
                .addOnSuccessListener {
                    limpiarCampos()
                    Snackbar.make(btn_registrar, "Se ha registrado correctamente", Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener{ error ->
                    Snackbar.make(btn_registrar, error.toString(), Snackbar.LENGTH_SHORT).show()
                }
    }

    //Validaciones
    open fun formatoNombre(cadena: String?): Boolean {
        return Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", cadena)
    }

    fun formatoCorreo(cadena: String?): Boolean {
        return Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@+[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})+$", cadena)
    }

    fun formatoContrasenia(cadena: String?): Boolean {
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&\\*]).{6,}$", cadena)
    }

    //Navegacion
    private fun navegarPantallaInicio(){
        val intent = Intent(this, InicioSesion::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}