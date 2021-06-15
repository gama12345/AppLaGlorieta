package com.example.estacionamientolaglorieta

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.estacionamientolaglorieta.adaptadores.FragmentPagerRegistros
import com.example.estacionamientolaglorieta.adaptadores.FragmentPagerVehiculos
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Vehiculos : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentPager: FragmentPagerVehiculos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehiculos)

        configurarBarraHerramientas()
        instanciarComponentes()
    }

    private fun configurarBarraHerramientas(){
        setSupportActionBar(findViewById(R.id.barra_herramientas))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val actionBar = supportActionBar
        actionBar?.setTitle("Vehiculos")
    }


    private fun instanciarComponentes(){
        fragmentPager = FragmentPagerVehiculos(this)
        viewPager = findViewById(R.id.viewPager_vehiculos)
        viewPager.adapter = fragmentPager
        var tabLayout: TabLayout = findViewById(R.id.tabLayout_vehiculos)
        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            if(position == 0){ tab.text = "Registros"}
            if(position == 1){ tab.text = "Nuevo vehiculo"}
        }.attach()
    }

    //Acciones barra de herramientas
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}