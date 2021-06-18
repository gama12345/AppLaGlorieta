package com.example.estacionamientolaglorieta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.estacionamientolaglorieta.adaptadores.AdaptadorRegistrosReservaciones
import com.example.estacionamientolaglorieta.adaptadores.AdaptadorRegistrosResguardos
import com.example.estacionamientolaglorieta.adaptadores.Reservacion
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class VerReservaciones : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var view_fragement: View
    lateinit var progressBar: ProgressBar
    lateinit var linear_progress: LinearLayout
    var terminar_progressBar = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view_fragement = inflater.inflate(R.layout.fragment_reservaciones_registradas, container, false)
        return view_fragement
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        instanciarComponentes()
        cargarRegistrosReservaciones()
    }

    private fun instanciarComponentes(){
        val lim = LinearLayoutManager(activity)
        lim.orientation = LinearLayoutManager.VERTICAL
        recyclerView = view_fragement.findViewById(R.id.rv_registros_reservaciones)
        recyclerView.layoutManager = lim
        refreshLayout = view_fragement.findViewById(R.id.refresh_registros_reservaciones)
        refreshLayout.setOnRefreshListener(recargar)
        progressBar = view_fragement.findViewById(R.id.progressBar_reservaciones)
        linear_progress = view_fragement.findViewById(R.id.linear_reservaciones_progress)
        activarEfectoProgressBar()
    }

    private fun cargarRegistrosReservaciones(){
        var lista = arrayListOf<Reservacion>()
        db.collection("reservaciones").whereEqualTo("cliente", AppPreferences.claveUsuario).get()
                .addOnSuccessListener { resultados ->
                    for(reservacion in resultados){
                        val cajon = "Cajón: "+reservacion.get("cajon").toString()
                        val vehiculo = reservacion.get("vehiculo").toString()
                        val estatus = "Activo"
                        lista.add(Reservacion(cajon, vehiculo, estatus))
                    }
                    db.collection("vehiculos").whereEqualTo("propietario", AppPreferences.claveUsuario).get()
                            .addOnSuccessListener { resultados ->
                                for(elemento in lista){
                                    for(vehiculo in resultados){
                                        if(elemento.vehiculo.equals(vehiculo.id)){
                                            elemento.vehiculo = vehiculo.get("marca").toString()+", "+vehiculo.get("color").toString()+", "+vehiculo.id
                                            break
                                        }
                                    }
                                }
                                if(lista.size == 0){
                                    Snackbar.make(recyclerView, "No tiene reservaciones activas", Snackbar.LENGTH_SHORT).show()
                                }
                                val adaptador = AdaptadorRegistrosReservaciones(lista)
                                recyclerView.adapter = adaptador
                                detenerEfectoProgressBar()
                            }
                            .addOnFailureListener{ error ->
                                detenerEfectoProgressBar()
                                Snackbar.make(recyclerView, error.toString(), Snackbar.LENGTH_SHORT).show()
                            }
                }
                .addOnFailureListener{ error ->
                    detenerEfectoProgressBar()
                    Snackbar.make(recyclerView, error.toString(), Snackbar.LENGTH_SHORT).show()
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

    var recargar = SwipeRefreshLayout.OnRefreshListener {
        activarEfectoProgressBar()
        cargarRegistrosReservaciones()
        refreshLayout.isRefreshing = false
    }
}