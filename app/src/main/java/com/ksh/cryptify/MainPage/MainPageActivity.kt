package com.ksh.cryptify.MainPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.ksh.cryptify.R
import com.ksh.cryptify.databinding.ActivityMainpageBinding

class MainPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainpageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationUI.setupWithNavController(binding.navBar, findNavController(R.id.nav_host))
    }
}