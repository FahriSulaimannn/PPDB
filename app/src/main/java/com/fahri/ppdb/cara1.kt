package com.fahri.ppdb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import org.checkerframework.common.subtyping.qual.Bottom

class cara1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cara1, container, false)

        // Tombol untuk kembali ke fragment sebelumnya menggunakan CardView
        view.findViewById<CardView>(R.id.backButton).setOnClickListener {
            activity?.onBackPressed()
        }

        // Set up the button click listener for nextButton (Button)
        view.findViewById<Button>(R.id.nextButton).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, cara2())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

}