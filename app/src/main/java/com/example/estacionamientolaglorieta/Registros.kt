package com.example.estacionamientolaglorieta

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.estacionamientolaglorieta.fragmentpagers.FragmentPagerRegistros
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Registros : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentPager: FragmentPagerRegistros

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        configurarBarraHerramientas()
        instanciarComponentes()
    }

    private fun configurarBarraHerramientas(){
        setSupportActionBar(findViewById(R.id.barra_herramientas))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val actionBar = supportActionBar
        actionBar?.setTitle("Registros")
    }

    private fun instanciarComponentes(){
        fragmentPager = FragmentPagerRegistros(this)
        viewPager = findViewById(R.id.viewPager_registros)
        viewPager.adapter = fragmentPager
        var tabLayout: TabLayout = findViewById(R.id.tabLayout_registros)
        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            if(position == 0){ tab.text = "Resguardos"}
            if(position == 1){ tab.text = "Reservaciones"}
            if(position == 2){ tab.text = "Nueva reservación"}
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