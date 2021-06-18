package com.example.estacionamientolaglorieta

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarReservacion : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var view_fragement: View
    private lateinit var spinner_vehiculo: Spinner
    private lateinit var spinner_cajon: Spinner
    private lateinit var btn_reservar: Button
    lateinit var progressBar: ProgressBar
    lateinit var linear_progress: LinearLayout
    var terminar_progressBar = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view_fragement = inflater.inflate(R.layout.fragment_registrar_reservacion, container, false)
        return view_fragement
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        instanciarComponentes()
        cargarDatos()
    }

    private fun instanciarComponentes(){
        spinner_vehiculo = view_fragement.findViewById(R.id.spinner_registrar_reservacion_vehiculo)
        spinner_cajon = view_fragement.findViewById(R.id.spinner_registrar_reservacion_cajon)
        btn_reservar = view_fragement.findViewById(R.id.btn_registrar_reservacion_guardar)
        btn_reservar.setOnClickListener { reservar() }
        progressBar = view_fragement.findViewById(R.id.progressBar_registrar_reservacion)
        linear_progress = view_fragement.findViewById(R.id.linear_registrar_reservacion_progress)
        activarEfectoProgressBar()
    }

    private fun cargarDatos(){
        db.collection("vehiculos").whereEqualTo("propietario", AppPreferences.claveUsuario).get()
                .addOnSuccessListener { resultados ->
                    if(!resultados.isEmpty){
                        var lista = arrayListOf<String>()

                        for(vehiculo in resultados){
                            var placas = vehiculo.id
                            var marca = vehiculo.get("marca").toString()
                            var color = vehiculo.get("color").toString()
                            lista.add("$marca, $color, $placas")
                        }
                        db.collection("resguardos").whereEqualTo("estatus", "Activo").get()
                                .addOnSuccessListener { resultados ->
                                    for(resguardo in resultados){
                                        for(elemento in lista){
                                            var datos = elemento.split(", ")
                                            if(resguardo.get("vehiculo").toString().equals(datos[2])){
                                                lista.remove(elemento)
                                                break
                                            }
                                        }
                                    }
                                    db.collection("reservaciones").whereEqualTo("cliente", AppPreferences.claveUsuario).get()
                                            .addOnSuccessListener { resultados ->
                                                for(reservacion in resultados){
                                                    for(elemento in lista){
                                                        var datos = elemento.split(", ")
                                                        if(reservacion.get("vehiculo").toString().equals(datos[2])){
                                                            lista.remove(elemento)
                                                            break
                                                        }
                                                    }
                                                }
                                                var adaptador_vehiculos = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
                                                adaptador_vehiculos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                spinner_vehiculo.adapter = adaptador_vehiculos
                                                adaptador_vehiculos.addAll(lista)
                                                adaptador_vehiculos.notifyDataSetChanged()
                                                db.collection("cajones").whereEqualTo("estatus", "Disponible").orderBy("numero").get()
                                                        .addOnSuccessListener { resultados ->
                                                            if(!resultados.isEmpty){
                                                                var adaptador_cajones = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
                                                                adaptador_cajones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                                spinner_cajon.adapter = adaptador_cajones
                                                                for(cajon in resultados){
                                                                    adaptador_cajones.add("Cajón: "+cajon.get("numero").toString())
                                                                }
                                                                adaptador_cajones.notifyDataSetChanged()
                                                                detenerEfectoProgressBar()
                                                                if(adaptador_vehiculos.count == 0) {
                                                                    btn_reservar.visibility = View.GONE
                                                                    Snackbar.make(btn_reservar, "Todos sus vehiculos están en resguardo/reservación", Snackbar.LENGTH_SHORT).show()
                                                                }
                                                            }else{
                                                                detenerEfectoProgressBar()
                                                                btn_reservar.visibility = View.GONE
                                                                Snackbar.make(btn_reservar, "Sin disponibilidad. Estacionamiento lleno", Snackbar.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                        .addOnFailureListener{ error ->
                                                            detenerEfectoProgressBar()
                                                            Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                                        }
                                            }
                                            .addOnFailureListener{ error ->
                                                detenerEfectoProgressBar()
                                                Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                            }
                                }
                                .addOnFailureListener{ error ->
                                    detenerEfectoProgressBar()
                                    Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                }
                    }else{
                        detenerEfectoProgressBar()
                        btn_reservar.visibility = View.GONE
                        Snackbar.make(btn_reservar, "Sin vehiculos registrados", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{ error ->
                    detenerEfectoProgressBar()
                    Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                }
    }

    private fun reservar(){
        activarEfectoProgressBar()
        var elementos = spinner_vehiculo.selectedItem.toString().split(", ")
        var placas = elementos[2]
        elementos = spinner_cajon.selectedItem.toString().split(": ")
        var numero_cajon = elementos[1]
        db.collection("cajones").whereEqualTo("numero", Integer.parseInt(numero_cajon)).get()
                .addOnSuccessListener { resultados ->
                    if(resultados.documents[0].get("estatus").toString() == "Disponible"){
                        db.collection("reservaciones").whereEqualTo("vehiculo", placas).get()
                                .addOnSuccessListener { resultados ->
                                    if(resultados.isEmpty){
                                        db.collection("resguardos").whereEqualTo("vehiculo", placas).whereEqualTo("estatus", "Activo").get()
                                                .addOnSuccessListener { resultados ->
                                                    if(resultados.isEmpty){
                                                        val reservacion = hashMapOf<String, Any>(
                                                                "cajon" to numero_cajon,
                                                                "cliente" to AppPreferences.claveUsuario,
                                                                "vehiculo" to placas
                                                        )
                                                        db.collection("reservaciones").add(reservacion)
                                                                .addOnSuccessListener { resultado ->
                                                                    db.collection("cajones").document("cajon"+numero_cajon).update("estatus", "Reservado")
                                                                            .addOnSuccessListener { resultado ->
                                                                                detenerEfectoProgressBar()
                                                                                Snackbar.make(btn_reservar, "Reservación registrada correctamente", Snackbar.LENGTH_SHORT).show()
                                                                                cargarDatos()
                                                                            }
                                                                            .addOnFailureListener{ error ->
                                                                                detenerEfectoProgressBar()
                                                                                Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                                                            }
                                                                }
                                                                .addOnFailureListener{ error ->
                                                                    detenerEfectoProgressBar()
                                                                    Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                                                }
                                                    }else{
                                                        detenerEfectoProgressBar()
                                                        Snackbar.make(btn_reservar, "El vehiculo ya se encuantra en resguardo", Snackbar.LENGTH_SHORT).show()
                                                        cargarDatos()
                                                    }
                                                }
                                                .addOnFailureListener{ error ->
                                                    detenerEfectoProgressBar()
                                                    Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                                }
                                    }else{
                                        detenerEfectoProgressBar()
                                        Snackbar.make(btn_reservar, "El vehiculo ya cuenta con una reservación activa", Snackbar.LENGTH_SHORT).show()
                                        cargarDatos()
                                    }
                                }
                                .addOnFailureListener{ error ->
                                    detenerEfectoProgressBar()
                                    Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                                }
                    }else{
                        detenerEfectoProgressBar()
                        Snackbar.make(btn_reservar, "Cajón no disponible. Seleccione uno diferente", Snackbar.LENGTH_SHORT).show()
                        cargarDatos()
                    }
                }
                .addOnFailureListener{ error ->
                    detenerEfectoProgressBar()
                    Snackbar.make(btn_reservar, error.toString(), Snackbar.LENGTH_SHORT).show()
                }
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
}