package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import com.fahri.ppdb.databinding.FragmentPage3Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Page3Fragment : Fragment() {

    private var _binding: FragmentPage3Binding? = null
    private val binding get() = _binding!!
    private var databaseReference = FirebaseDatabase.getInstance().reference
    private var valueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPage3Binding.inflate(inflater, container, false)

        // Firebase Auth and Database reference
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users").child(userUID ?: "")

        // ValueEventListener for Firebase
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (context != null) { // Pastikan fragment masih terhubung ke konteks
                    binding.nama.text = snapshot.child("name").getValue(String::class.java) ?: "N/A"
                    binding.nisn.text = snapshot.child("nisn").getValue(String::class.java) ?: "N/A"
                    binding.nik.text = snapshot.child("nik").getValue(String::class.java) ?: "N/A"
                    binding.kelamin.text = snapshot.child("gender").getValue(String::class.java) ?: "N/A"
                    binding.tempatTanggal.text = "${snapshot.child("birthPlace").getValue(String::class.java) ?: "N/A"}, ${snapshot.child("birthdate").getValue(String::class.java) ?: "N/A"}"
                    binding.ortu.text = snapshot.child("parentName").getValue(String::class.java) ?: "N/A"
                    binding.alamat.text = snapshot.child("address").getValue(String::class.java) ?: "N/A"
                    binding.kota.text = snapshot.child("city").getValue(String::class.java) ?: "N/A"
                    binding.telp.text = snapshot.child("phone").getValue(String::class.java) ?: "N/A"
                    binding.asalSekolah.text = snapshot.child("schoolOrigin").getValue(String::class.java) ?: "N/A"
                    binding.agama.text = snapshot.child("religion").getValue(String::class.java) ?: "N/A"
                    binding.indo.text = snapshot.child("nilaiIndo").getValue(String::class.java) ?: "N/A"
                    binding.inggris.text = snapshot.child("nilaiIng").getValue(String::class.java) ?: "N/A"
                    binding.matematika.text = snapshot.child("nilaiMat").getValue(String::class.java) ?: "N/A"
                    binding.ipa.text = snapshot.child("nilaiIPA").getValue(String::class.java) ?: "N/A"

                    // Get and update status
                    val status = snapshot.child("status").getValue(String::class.java)

                    if (status == "approve") {
                        // If the status is "approve"
                        binding.status.text = "Status: Approved"
                        binding.deskripsi.text = "Your application has been approved. You can proceed with the next steps."
                        binding.icon.setImageResource(R.drawable.check) // Approved icon
                    } else {
                        // If the status is "pending" or any other value
                        binding.status.text = "Status: Pending"
                        binding.deskripsi.text = "Your application is still pending approval."
                        binding.icon.setImageResource(R.drawable.proces) // Pending icon
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (context != null) { // Pastikan fragment masih terhubung ke konteks
                    Toast.makeText(requireContext(), "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish() // Close current Activity
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove Firebase listener to prevent memory leaks
        valueEventListener?.let { databaseReference.removeEventListener(it) }
        valueEventListener = null
        _binding = null
    }
}
