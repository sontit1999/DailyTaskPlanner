package com.example.dailytaskplanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.databinding.FragHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragHomeBinding.bind(inflater.inflate(R.layout.frag_home, container, false))
        return binding.root
    }
}