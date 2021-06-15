package com.example.estacionamientolaglorieta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.estacionamientolaglorieta.adaptadores.AdaptadorRegistrosResguardos
import com.example.estacionamientolaglorieta.adaptadores.Resguardo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class VerResguardos : Fragment() {
    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var view_fragement: View
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view_fragement = inflater.inflate(R.layout.fragment_resguardos, container, false)
        return view_fragement
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        instanciarComponentes()
        cargarRegistrosResguardos()
    }

    private fun instanciarComponentes(){
        spinner = view_fragement.findViewById(R.id.spinner_resguardos)
        ArrayAdapter.createFromResource(requireContext(), R.array.estatus_resguardos, android.R.layout.simple_spinner_item)
                .also{ adaptador ->
                    adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adaptador
                }
        spinner.onItemSelectedListener = filtrarResguardos
        val lim = LinearLayoutManager(context)
        lim.orientation = LinearLayoutManager.VERTICAL
        recyclerView = view_fragement.findViewById(R.id.rv_resguardos)
        recyclerView.layoutManager = lim
        refreshLayout = view_fragement.findViewById(R.id.refresh_resguardos)
        refreshLayout.setOnRefreshListener(recargar)
    }

    var filtrarResguardos: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val opcion_seleccionada: String = spinner.getSelectedItem().toString()
            if(opcion_seleccionada.equals("Todos")){ cargarRegistrosResguardos() }
            else{ cargarRegistrosResguardosFiltro(opcion_seleccionada) }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun cargarRegistrosResguardos(){
        var lista = arrayListOf<Resguardo>()
        db.collection("resguardos").orderBy("estatus").get()
                .addOnSuccessListener { resultados ->
                    if(!resultados.isEmpty){
                        for(registro in resultados){
                            var fechas = "Llegada: "+registro.get("fecha_llegada").toString()
                            var salida = registro.get("fecha_salida").toString()
                            if(!salida.equals("")){
                                fechas = "$fechas - Salida: $salida"
                            }
                            var estatus = "Estatus: "+registro.get("estatus").toString()
                            var placas_vehiculo = registro.get("vehiculo").toString()
                            var servicios = "Servicios extra: "+registro.get("servicios_extra").toString()
                            var cobro = registro.get("cobro").toString()
                            if(cobro.equals("")){ cobro = "0.00" }
                            lista.add(Resguardo(fechas, estatus, placas_vehiculo, servicios, cobro))
                        }
                        db.collection("vehiculos").whereEqualTo("propietario", AppPreferences.claveUsuario).get()
                                .addOnSuccessListener { resultados ->
                                    Log.d("DATA ==>> ", ""+resultados.size())
                                    var registros = arrayListOf<Resguardo>()
                                    for(resguardo in lista){
                                        for(vehiculo in resultados){
                                            if(resguardo.vehiculo.equals(vehiculo.id)){
                                                var marca = vehiculo.get("marca").toString()
                                                var color = vehiculo.get("color").toString()
                                                resguardo.vehiculo = marca+", "+color+", "+vehiculo.id
                                                registros.add(resguardo)
                                            }
                                        }
                                    }
                                    if(registros.size == 0){
                                        Snackbar.make(spinner, "No se encontraron registros de resguardos", Snackbar.LENGTH_SHORT).show()
                                    }
                                    val adaptador = AdaptadorRegistrosResguardos(registros)
                                    recyclerView.adapter = adaptador
                                }
                                .addOnFailureListener{ error ->
                                    Snackbar.make(spinner, error.toString(), Snackbar.LENGTH_SHORT).show()
                                }
                    }else{
                        Snackbar.make(spinner, "No se encontraron registros de resguardos", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{ error ->
                    Snackbar.make(spinner, error.toString(), Snackbar.LENGTH_SHORT).show()
                }
    }

    private fun cargarRegistrosResguardosFiltro(tipo: String){
        var lista = arrayListOf<Resguardo>()
        db.collection("resguardos").whereEqualTo("estatus", tipo).get()
                .addOnSuccessListener { resultados ->
                    if(!resultados.isEmpty){
                        for(registro in resultados){
                            var fechas = "Llegada: "+registro.get("fecha_llegada").toString()
                            var salida = registro.get("fecha_salida").toString()
                            if(!salida.equals("")){
                                fechas = "$fechas - Salida: $salida"
                            }
                            var estatus = "Estatus: "+registro.get("estatus").toString()
                            var placas_vehiculo = registro.get("vehiculo").toString()
                            var servicios = "Servicios extra: "+registro.get("servicios_extra").toString()
                            var cobro = registro.get("cobro").toString()
                            if(cobro.equals("")){ cobro = "0.00" }
                            lista.add(Resguardo(fechas, estatus, placas_vehiculo, servicios, cobro))
                        }
                        db.collection("vehiculos").whereEqualTo("propietario", AppPreferences.claveUsuario).get()
                                .addOnSuccessListener { resultados ->
                                    var registros = arrayListOf<Resguardo>()
                                    for(resguardo in lista){
                                        for(vehiculo in resultados){
                                            if(resguardo.vehiculo.equals(vehiculo.id)){
                                                var marca = vehiculo.get("marca").toString()
                                                var color = vehiculo.get("color").toString()
                                                resguardo.vehiculo = marca+", "+color+", "+vehiculo.id
                                                registros.add(resguardo)
                                            }
                                        }
                                    }
                                    if(registros.size == 0){
                                        Snackbar.make(spinner, "No se encontraron registros de resguardos", Snackbar.LENGTH_SHORT).show()
                                    }
                                    val adaptador = AdaptadorRegistrosResguardos(registros)
                                    recyclerView.adapter = adaptador
                                }
                                .addOnFailureListener{ error ->
                                    Snackbar.make(spinner, error.toString(), Snackbar.LENGTH_SHORT).show()
                                }
                    }else{
                        Snackbar.make(spinner, "No se encontraron registros de resguardos", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{ error ->
                    Snackbar.make(spinner, error.toString(), Snackbar.LENGTH_SHORT).show()
                }
    }
    
    var recargar = OnRefreshListener {
        cargarRegistrosResguardos()
        refreshLayout.isRefreshing = false
    }

}