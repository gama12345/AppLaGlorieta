package com.example.estacionamientolaglorieta.pojos

class Reservacion constructor(cajon: String, vehiculo: String, estatus: String){

    var cajon: String = cajon
        get() = field ?: cajon
        set(nuevoCajon){ field = nuevoCajon }
    var estatus = estatus
        get() = field ?: estatus
        set(nuevoEstatus){ field = nuevoEstatus }
    var vehiculo = vehiculo
        get() = field ?: vehiculo
        set(nuevoVehiculo){ field = nuevoVehiculo }

}