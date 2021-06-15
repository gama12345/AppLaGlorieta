package com.example.estacionamientolaglorieta.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.estacionamientolaglorieta.R
import kotlin.collections.ArrayList

class AdaptadorRegistrosResguardos constructor(private var resguardos: ArrayList<Resguardo>) : RecyclerView.Adapter<AdaptadorRegistrosResguardos.AdaptadorViewHolder>(){

    class AdaptadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fechas: TextView = itemView.findViewById(R.id.card_reservacion_cajon)
        var estatus: TextView = itemView.findViewById(R.id.card_reservacion_estatus)
        var vehiculo: TextView = itemView.findViewById(R.id.card_reservacion_vehiculo)
        var servicios: TextView = itemView.findViewById(R.id.card_servicios)
        var cobro: TextView = itemView.findViewById(R.id.card_cobro)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.cardview_registros_resguardos, parent, false)
        return AdaptadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdaptadorViewHolder, position: Int) {
        val registro = resguardos[position]
        holder.fechas.setText(registro.fechas)
        holder.estatus.setText(registro.estatus)
        holder.vehiculo.setText(registro.vehiculo)
        holder.servicios.setText(registro.servicios)
        holder.cobro.setText(registro.cobro)
    }

    override fun getItemCount(): Int { return resguardos.size }

}