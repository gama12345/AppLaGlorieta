package com.example.estacionamientolaglorieta.adaptadores

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.estacionamientolaglorieta.*

class FragmentPagerVehiculos(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment = VerVehiculos()
        if(position == 1){ fragment = RegistrarVehiculo() }
        return fragment
    }

}

