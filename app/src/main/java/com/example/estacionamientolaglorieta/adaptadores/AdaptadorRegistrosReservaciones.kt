package com.example.estacionamientolaglorieta.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.estacionamientolaglorieta.R
import com.example.estacionamientolaglorieta.pojos.Reservacion
import kotlin.collections.ArrayList

class AdaptadorRegistrosReservaciones constructor(private var resguardos: ArrayList<Reservacion>) : RecyclerView.Adapter<AdaptadorRegistrosReservaciones.AdaptadorViewHolder>(){

    class AdaptadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cajon: TextView = itemView.findViewById(R.id.card_reservacion_cajon)
        var estatus: TextView = itemView.findViewById(R.id.card_reservacion_estatus)
        var vehiculo: TextView = itemView.findViewById(R.id.card_reservacion_vehiculo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.cardview_registros_reservaciones, parent, false)
        return AdaptadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdaptadorViewHolder, position: Int) {
        val registro = resguardos[position]
        holder.cajon.setText(registro.cajon)
        holder.estatus.setText(registro.estatus)
        holder.vehiculo.setText(registro.vehiculo)
    }

    override fun getItemCount(): Int { return resguardos.size }

}