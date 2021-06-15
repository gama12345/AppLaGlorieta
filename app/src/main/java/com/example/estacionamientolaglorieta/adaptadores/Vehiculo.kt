package com.example.estacionamientolaglorieta.adaptadores

class Vehiculo constructor(marca: String, modelo: String, color: String, tamanio: String, placas: String){

    var marca: String = marca
        get() = field ?: marca
        set(valor){ field = valor }

    var modelo: String = modelo
        get() = field ?: modelo
        set(valor){ field = valor }

    var color: String = color
        get() = field ?: color
        set(valor){ field = valor }

    var tamanio: String = tamanio
        get() = field ?: tamanio
        set(valor){ field = valor }

    var placas: String = placas
        get() = field ?: placas
        set(valor){ field = valor }

}