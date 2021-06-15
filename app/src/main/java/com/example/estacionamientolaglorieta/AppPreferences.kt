package com.example.estacionamientolaglorieta

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NOMBRE = "InicioSesion"
    private const val MODO = Context.MODE_PRIVATE
    private lateinit var preferencias: SharedPreferences

    //SharedPreferences variables
    private val es_identificado = Pair("es_identificado", false)
    private val usuario = Pair("usuario", "")
    private val contraseña = Pair("contraseña", "")
    private val clave_usuario = Pair("id", "")

    fun init(contexto: Context){
        preferencias = contexto.getSharedPreferences(NOMBRE, MODO)
    }

    //funcion inline para colocar una variable y guardarla
    private inline fun SharedPreferences.edit(operacion : (SharedPreferences.Editor) -> Unit){
        val editor = edit()
        operacion(editor)
        editor.apply()
    }

    //Get y ser
    var esIdentificado: Boolean
        get() = preferencias.getBoolean(es_identificado.first, es_identificado.second)
        set(valor) = preferencias.edit{ it.putBoolean(es_identificado.first, valor) }

    var usuarioActual: String
        get() = preferencias.getString(usuario.first, usuario.second) ?: ""
        set(valor) = preferencias.edit{ it.putString(usuario.first, valor) }

    var contraseñaActual: String
        get() = preferencias.getString(contraseña.first, contraseña.second) ?: ""
        set(valor) = preferencias.edit{ it.putString(contraseña.first, valor) }

    var claveUsuario: String
        get() = preferencias.getString(clave_usuario.first, clave_usuario.second) ?: ""
        set(valor) = preferencias.edit{ it.putString(clave_usuario.first, valor) }
}