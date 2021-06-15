package com.example.estacionamientolaglorieta

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btn_cuenta: ImageButton
    private lateinit var btn_registros: ImageButton
    private lateinit var btn_vehiculos: ImageButton
    private lateinit var btn_cerrar_sesion: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppPreferences.init(this)
        verificarUsuario();
    }

    private fun verificarUsuario() {
        if(AppPreferences.esIdentificado){
            instanciarComponentes()
        }else{
            val intent = Intent(this, InicioSesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun instanciarComponentes(){
        btn_cuenta = findViewById(R.id.imageBtn_main_cuenta)
        btn_cuenta.setOnClickListener{ navegarPantallaCuenta("CuentaUsuario") }
        btn_registros = findViewById(R.id.imageBtn_main_registros)
        btn_registros.setOnClickListener { navegarPantallaCuenta("Registros") }
        btn_vehiculos = findViewById(R.id.imageBtn_main_vehiculos)
        btn_vehiculos.setOnClickListener { navegarPantallaCuenta("Vehiculos") }
        btn_cerrar_sesion = findViewById(R.id.imageBtn_main_cerrar_sesion)
        btn_cerrar_sesion.setOnClickListener{ cerrarSesion() }
    }

    private fun cerrarSesion(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Deseas cerrar sesión?")
            .setPositiveButton("Si") { dialog, id ->
                AppPreferences.esIdentificado = false
                AppPreferences.usuarioActual = ""
                AppPreferences.contraseñaActual = ""
                AppPreferences.claveUsuario = ""
                val intent = Intent(this, InicioSesion::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No") { dialog, id ->
                // User cancelled the dialog
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED)
    }

    private fun navegarPantallaCuenta(tipo: String){
        lateinit var intent: Intent
        when (tipo) {
            "CuentaUsuario" -> intent = Intent(this, CuentaUsuario::class.java)
            "Vehiculos" -> intent = Intent(this, Vehiculos::class.java)
            "Registros" -> intent = Intent(this, Registros::class.java)
        }
        startActivity(intent)
    }
}