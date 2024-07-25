package com.eunice.tripsplanner.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.eunice.tripsplanner.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startFragment(TripsHomeFragment.newInstance(), true)
    }


    companion object {
        fun FragmentActivity.startFragment(fragment: Fragment, isAddToBackStack: Boolean) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            if (isAddToBackStack)
                fragmentTransaction
                    .replace(R.id.container, fragment)
                    .addToBackStack(fragment.javaClass.simpleName)
                    .commit()
            else fragmentTransaction.replace(R.id.container, fragment).commit()
        }
    }
    
}