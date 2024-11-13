package com.fahri.ppdb

import FormViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Page3Fragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var nameTextView: TextView
    private lateinit var nisnTextView: TextView
    private lateinit var nikTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var birthPlaceTextView: TextView
    private lateinit var parentNameTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var schoolOriginTextView: TextView
    private lateinit var religionTextView: TextView
    private lateinit var indoTextView: TextView
    private lateinit var inggrisTextView: TextView
    private lateinit var matematikaTextView: TextView
    private lateinit var ipaTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var statusImageView: ImageView // ImageView for status icon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_page3, container, false)

        // Initialize views
        nameTextView = rootView.findViewById(R.id.nama)
        nisnTextView = rootView.findViewById(R.id.nisn)
        nikTextView = rootView.findViewById(R.id.nik)
        genderTextView = rootView.findViewById(R.id.kelamin)
        birthPlaceTextView = rootView.findViewById(R.id.tempat_tanggal)
        parentNameTextView = rootView.findViewById(R.id.ortu)
        addressTextView = rootView.findViewById(R.id.alamat)
        cityTextView = rootView.findViewById(R.id.kota)
        phoneTextView = rootView.findViewById(R.id.telp)
        schoolOriginTextView = rootView.findViewById(R.id.asal_sekolah)
        religionTextView = rootView.findViewById(R.id.agama)
        indoTextView = rootView.findViewById(R.id.indo)
        inggrisTextView = rootView.findViewById(R.id.inggris)
        matematikaTextView = rootView.findViewById(R.id.matematika)
        ipaTextView = rootView.findViewById(R.id.ipa)
        statusTextView = rootView.findViewById(R.id.status) // TextView to show status
        descriptionTextView = rootView.findViewById(R.id.deskripsi) // Deskripsi
        statusImageView = rootView.findViewById(R.id.icon) // ImageView for status icon

        // Get the current user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid

        // Fetch user data from Firebase
        if (userUID != null) {
            val databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userUID)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Display user data
                    nameTextView.text = snapshot.child("name").getValue(String::class.java) ?: "N/A"
                    nisnTextView.text = snapshot.child("nisn").getValue(String::class.java) ?: "N/A"
                    nikTextView.text = snapshot.child("nik").getValue(String::class.java) ?: "N/A"
                    genderTextView.text = snapshot.child("gender").getValue(String::class.java) ?: "N/A"
                    birthPlaceTextView.text = "${snapshot.child("birthPlace").getValue(String::class.java) ?: "N/A"}, ${snapshot.child("birthdate").getValue(String::class.java) ?: "N/A"}"
                    parentNameTextView.text = snapshot.child("parentName").getValue(String::class.java) ?: "N/A"
                    addressTextView.text = snapshot.child("address").getValue(String::class.java) ?: "N/A"
                    cityTextView.text = snapshot.child("city").getValue(String::class.java) ?: "N/A"
                    phoneTextView.text = snapshot.child("phone").getValue(String::class.java) ?: "N/A"
                    schoolOriginTextView.text = snapshot.child("schoolOrigin").getValue(String::class.java) ?: "N/A"
                    religionTextView.text = snapshot.child("religion").getValue(String::class.java) ?: "N/A"
                    indoTextView.text = snapshot.child("nilaiIndo").getValue(String::class.java) ?: "N/A"
                    inggrisTextView.text = snapshot.child("nilaiIng").getValue(String::class.java) ?: "N/A"
                    matematikaTextView.text = snapshot.child("nilaiMat").getValue(String::class.java) ?: "N/A"
                    ipaTextView.text = snapshot.child("nilaiIPA").getValue(String::class.java) ?: "N/A"

                    // Get and update status
                    val status = snapshot.child("status").getValue(String::class.java)

                    if (status == "approve") {
                        // If the status is "approve"
                        statusTextView.text = "Status: Approved"
                        descriptionTextView.text = "Your application has been approved. You can proceed with the next steps."
                        statusImageView.setImageResource(R.drawable.check) // Approved icon
                    } else {
                        // If the status is "pending" or any other value
                        statusTextView.text = "Status: Pending"
                        descriptionTextView.text = "Your application is still pending approval."
                        statusImageView.setImageResource(R.drawable.proces) // Pending icon
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(requireContext(), "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Page2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Page2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
