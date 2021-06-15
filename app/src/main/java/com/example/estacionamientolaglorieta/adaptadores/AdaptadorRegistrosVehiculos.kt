package com.example.estacionamientolaglorieta.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.estacionamientolaglorieta.R
import kotlin.collections.ArrayList

class AdaptadorRegistrosVehiculos constructor(private var vehiculos: ArrayList<Vehiculo>) : RecyclerView.Adapter<AdaptadorRegistrosVehiculos.AdaptadorViewHolder>(){

    class AdaptadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var marca: TextView = itemView.findViewById(R.id.card_vehiculo_marca)
        var modelo: TextView = itemView.findViewById(R.id.card_vehiculo_modelo)
        var color: TextView = itemView.findViewById(R.id.card_vehiculo_color)
        var tamanio: TextView = itemView.findViewById(R.id.card_vehiculo_tamanio)
        var placas: TextView = itemView.findViewById(R.id.card_vehiculo_placas)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.cardview_registros_vehiculos, parent, false)
        return AdaptadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdaptadorViewHolder, position: Int) {
        val vehiculo = vehiculos[position]
        holder.marca.setText("Marca: "+vehiculo.marca)
        holder.modelo.setText("Modelo: "+vehiculo.modelo)
        holder.color.setText("Color: "+vehiculo.color)
        holder.tamanio.setText("Tamaño: "+vehiculo.tamanio)
        holder.placas.setText("Placas: "+vehiculo.placas)
    }

    override fun getItemCount(): Int { return vehiculos.size }

}