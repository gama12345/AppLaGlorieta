package com.example.estacionamientolaglorieta.fragmentpagers

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.estacionamientolaglorieta.RegistrarReservacion
import com.example.estacionamientolaglorieta.VerReservaciones
import com.example.estacionamientolaglorieta.VerResguardos

class FragmentPagerRegistros(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment = VerResguardos()
        if(position == 1){ fragment = VerReservaciones() }
        if(position == 2){ fragment = RegistrarReservacion() }
        return fragment
    }

}

