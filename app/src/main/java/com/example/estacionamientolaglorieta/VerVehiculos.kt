package com.example.estacionamientolaglorieta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.estacionamientolaglorieta.adaptadores.AdaptadorRegistrosReservaciones
import com.example.estacionamientolaglorieta.adaptadores.AdaptadorRegistrosVehiculos
import com.example.estacionamientolaglorieta.adaptadores.Vehiculo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class VerVehiculos : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var view_fragement: View
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view_fragement = inflater.inflate(R.layout.fragment_ver_vehiculos, container, false)
        return view_fragement
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        instanciarComponentes()
        cargarVehiculos()
    }

    private fun instanciarComponentes(){
        val lim = LinearLayoutManager(activity)
        lim.orientation = LinearLayoutManager.VERTICAL
        recyclerView = view_fragement.findViewById(R.id.rv_registros_vehiculos)
        recyclerView.layoutManager = lim
        refreshLayout = view_fragement.findViewById(R.id.refresh_registros_vehiculos)
        refreshLayout.setOnRefreshListener(recargar)
    }

    private fun cargarVehiculos(){
        var lista = arrayListOf<Vehiculo>()
        db.collection("vehiculos").whereEqualTo("propietario", AppPreferences.claveUsuario).get()
            .addOnSuccessListener { resultados ->
                if(!resultados.isEmpty){
                    for(vehiculo in resultados){
                        var marca = vehiculo.get("marca").toString()
                        var modelo = vehiculo.get("modelo").toString()
                        var color = vehiculo.get("color").toString()
                        var tamanio = vehiculo.get("tamaño").toString()
                        var placas = vehiculo.id
                        lista.add(Vehiculo(marca, modelo, color, tamanio, placas))
                    }
                    val adaptador = AdaptadorRegistrosVehiculos(lista)
                    recyclerView.adapter = adaptador
                }else{
                    Snackbar.make(recyclerView, "Aún no tiene vehiculos registrados", Snackbar.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{ error ->
                Snackbar.make(recyclerView, error.toString(), Snackbar.LENGTH_SHORT).show()
            }
    }

    var recargar = SwipeRefreshLayout.OnRefreshListener {
        cargarVehiculos()
        refreshLayout.isRefreshing = false
    }
}