package com.example.estacionamientolaglorieta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegistrarVehiculo : Fragment() {
    private lateinit var et_placas: EditText
    private lateinit var et_marca: EditText
    private lateinit var et_modelo: EditText
    private lateinit var et_color: EditText
    private lateinit var spinner_tamanio: Spinner
    private lateinit var btn_registrar: Button
    private lateinit var view_fragement: View
    private lateinit var db: FirebaseFirestore
    lateinit var progressBar: ProgressBar
    lateinit var linear_progress: LinearLayout
    var terminar_progressBar = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view_fragement = inflater.inflate(R.layout.fragment_registrar_vehiculo, container, false)
        return view_fragement
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        instanciarComponentes()
    }

    private fun instanciarComponentes(){
        et_placas = view_fragement.findViewById(R.id.et_registrar_vehiculo_placas)
        et_marca = view_fragement.findViewById(R.id.et_registrar_vehiculo_marca)
        et_modelo = view_fragement.findViewById(R.id.et_registrar_vehiculo_modelo)
        et_color = view_fragement.findViewById(R.id.et_registrar_vehiculo_color)
        spinner_tamanio = view_fragement.findViewById(R.id.spinner_registrar_vehiculo_tamanio)
        btn_registrar = view_fragement.findViewById(R.id.btn_registrar_vehiculo)
        btn_registrar.setOnClickListener { iniciarRegistro() }
        var adaptador = ArrayAdapter.createFromResource(requireContext(), R.array.tamanios, android.R.layout.simple_spinner_item)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_tamanio.adapter = adaptador
        progressBar = view_fragement.findViewById(R.id.progressBar_registrar_vehiculo)
        linear_progress = view_fragement.findViewById(R.id.linear_registrar_vehiculo_progress)
        detenerEfectoProgressBar()
    }

    private fun iniciarRegistro(){
        activarEfectoProgressBar()
        val placas = et_placas.text.toString()
        val marca = et_marca.text.toString()
        val modelo = et_modelo.text.toString()
        val color = et_color.text.toString()
        val tamanio = spinner_tamanio.selectedItem.toString()
        if(validarDatosVehiculo(placas, marca, modelo, color)){
            db.collection("vehiculos").document(placas).get()
                    .addOnSuccessListener { resultado ->
                        if(!resultado.exists()){
                            val vehiculo = hashMapOf<String, Any>(
                                    "marca" to marca,
                                    "modelo" to modelo,
                                    "propietario" to AppPreferences.claveUsuario,
                                    "color" to color,
                                    "tamaño" to tamanio
                            )
                            db.collection("vehiculos").document(placas).set(vehiculo)
                                    .addOnSuccessListener {
                                        limpiarCampos()
                                        detenerEfectoProgressBar()
                                        Snackbar.make(btn_registrar, "Se ha registrado su nuevo vehiculo", Snackbar.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener{ error ->
                                        detenerEfectoProgressBar()
                                        Snackbar.make(btn_registrar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                    }
                        }else{
                            detenerEfectoProgressBar()
                            Snackbar.make(btn_registrar, "Error - Vehiculo con placas: $placas ya registrado", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener{ error ->
                        detenerEfectoProgressBar()
                        Snackbar.make(btn_registrar, error.toString(), Snackbar.LENGTH_SHORT).show()
                    }
        }
    }

    private fun limpiarCampos(){
        et_placas.setText("")
        et_marca.setText("")
        et_modelo.setText("")
        et_color.setText("")
        spinner_tamanio.setSelection(0)
    }

    private fun validarDatosVehiculo(placas: String, marca: String, modelo: String, color: String): Boolean{
        if(placas.trim() != "" && marca.trim() != "" && modelo.trim() != "" && color.trim() != ""){
            if(formatoPlacas(placas)){
                if(formatoColor(color)){
                    return true
                }else{
                    Snackbar.make(btn_registrar, "Error color - Use sólo letras", Snackbar.LENGTH_LONG).show()
                }
            }else{
                Snackbar.make(btn_registrar, "Formato de placas no válido - Use un formato válido, ejemplo: '765-XHL' o K-34-PJK", Snackbar.LENGTH_LONG).show()
            }
        }else{
            Snackbar.make(btn_registrar, "Error - Uno o más campos estan vacíos", Snackbar.LENGTH_LONG).show()
        }
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
    open fun formatoPlacas(cadena: String?): Boolean {
        if(Pattern.matches("^[A-Z]{3}\\-[0-9]{2}\\-[0-9]{2}$", cadena)){ return true }
        if(Pattern.matches("^[A-Z]\\-\\d{3}\\-[A-Z]{3}$", cadena)){ return true }
        if(Pattern.matches("^\\d{3}\\-[A-Z]{3}$", cadena)){ return true }
        return Pattern.matches("^[A-Z]\\-\\d{2}\\-[A-Z]{3}$", cadena)
    }
    open fun formatoColor(cadena: String?): Boolean {
        return Pattern.matches("[(A-ZÁ-Úa-zá-ú)*\\s*]+", cadena)
    }
}