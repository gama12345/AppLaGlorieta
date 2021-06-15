package com.example.estacionamientolaglorieta.adaptadores

class Resguardo constructor(fechas: String, estatus: String, vehiculo: String, servicios: String, cobro: String){

    var fechas: String = fechas
        get() = field ?: fechas
        set(nuevaFecha){ field = nuevaFecha }
    var estatus = estatus
        get() = field ?: estatus
        set(nuevoEstatus){ field = nuevoEstatus }
    var vehiculo = vehiculo
        get() = field ?: vehiculo
        set(nuevoVehiculo){ field = nuevoVehiculo }
    var servicios = servicios
        get() = field ?: servicios
        set(nuevoServicio){ field = nuevoServicio }
    var cobro = cobro
        get() = field ?: cobro
        set(nuevoCobro){ field = nuevoCobro }

}