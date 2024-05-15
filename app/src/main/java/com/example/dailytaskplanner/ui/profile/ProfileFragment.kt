package com.example.dailytaskplanner.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.databinding.FragHomeBinding
import com.example.dailytaskplanner.databinding.FragProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragProfileBinding.bind(inflater.inflate(R.layout.frag_profile, container, false))
        return binding.root
    }
}