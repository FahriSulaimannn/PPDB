package com.fahri.ppdb

import FormViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Page2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Page2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val formViewModel: FormViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page2, container, false)

        val etNilaiIndo = view.findViewById<EditText>(R.id.et_nilaiIndo)
        val etNilaiIng = view.findViewById<EditText>(R.id.et_nilaiIng)
        val etNilaiMat = view.findViewById<EditText>(R.id.et_nilaiMat)
        val etNilaiIPA = view.findViewById<EditText>(R.id.et_nilaiIPA)
        val etLinkFoto2 = view.findViewById<EditText>(R.id.et_linkfoto2)
        val btnSubmit = view.findViewById<Button>(R.id.btn_submit)
        val cvBack = view.findViewById<CardView>(R.id.cvBack)

        // Memulihkan data dari ViewModel dan menampilkan di EditText
        etNilaiIndo.setText(formViewModel.nilaiIndo.value)
        etNilaiIng.setText(formViewModel.nilaiIng.value)
        etNilaiMat.setText(formViewModel.nilaiMat.value)
        etNilaiIPA.setText(formViewModel.nilaiIPA.value)
        etLinkFoto2.setText(formViewModel.driveLink2.value)

        // Menyimpan data ke ViewModel saat input berubah
        etNilaiIndo.addTextChangedListener(createTextWatcher(formViewModel::setNilaiIndo))
        etNilaiIng.addTextChangedListener(createTextWatcher(formViewModel::setNilaiIng))
        etNilaiMat.addTextChangedListener(createTextWatcher(formViewModel::setNilaiMat))
        etNilaiIPA.addTextChangedListener(createTextWatcher(formViewModel::setNilaiIPA))
        etLinkFoto2.addTextChangedListener(createTextWatcher(formViewModel::setDriveLink2))

        cvBack.setOnClickListener {
            // Sebelum kembali, simpan data ke formViewModel
            saveInputToViewModel()
            activity?.onBackPressed()  // Kembali ke halaman sebelumnya
        }

        // Terapkan pembatasan input nilai maksimal 100 ke setiap EditText
        setMaxValueWithDecimal(etNilaiIndo)
        setMaxValueWithDecimal(etNilaiIng)
        setMaxValueWithDecimal(etNilaiMat)
        setMaxValueWithDecimal(etNilaiIPA)

        btnSubmit.setOnClickListener {
            // Gunakan validateFields untuk memeriksa apakah ada field yang kosong
            if (validateFields(etNilaiIndo, etNilaiIng, etNilaiMat, etNilaiIPA, etLinkFoto2)) {
                // Lanjutkan proses submit jika validasi sukses
                // Logika submit ke Firebase atau tindakan lain di sini
                formViewModel.nilaiIndo.value = etNilaiIndo.text.toString()
                formViewModel.nilaiIng.value = etNilaiIng.text.toString()
                formViewModel.nilaiMat.value = etNilaiMat.text.toString()
                formViewModel.nilaiIPA.value = etNilaiIPA.text.toString()
                formViewModel.driveLink2.value = etLinkFoto2.text.toString()

                showConfirmationDialog()
            }
        }

        etLinkFoto2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val googleDrivePattern = "https://drive\\.google\\.com/.*".toRegex()
                if (!googleDrivePattern.matches(s.toString())) {
                    etLinkFoto2.error = "Masukkan link Google Drive yang valid"
                } else {
                    etLinkFoto2.error = null
                }
            }
        })

        return view
    }

    // Fungsi untuk menyimpan data ke ViewModel
    private fun saveInputToViewModel() {
        formViewModel.nilaiIndo.value = view?.findViewById<EditText>(R.id.et_nilaiIndo)?.text.toString()
        formViewModel.nilaiIng.value = view?.findViewById<EditText>(R.id.et_nilaiIng)?.text.toString()
        formViewModel.nilaiMat.value = view?.findViewById<EditText>(R.id.et_nilaiMat)?.text.toString()
        formViewModel.nilaiIPA.value = view?.findViewById<EditText>(R.id.et_nilaiIPA)?.text.toString()
        formViewModel.driveLink2.value = view?.findViewById<EditText>(R.id.et_linkfoto2)?.text.toString()
    }

    // Fungsi untuk membuat TextWatcher untuk setiap EditText
    private fun createTextWatcher(setter: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                setter(s.toString())
            }
        }
    }

    private fun validateFields(vararg fields: EditText): Boolean {
        var allFieldsValid = true
        fields.forEach { field ->
            val value = field.text.toString().toFloatOrNull()

            if (field.text.isBlank()) {
                field.error = "Harus diisi"
                field.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
                allFieldsValid = false
            } else if (value != null && value > 100) {
                field.error = "Nilai maksimal adalah 100"
                field.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
                allFieldsValid = false
            } else {
                field.error = null
                field.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
            }
        }
        return allFieldsValid
    }


    private fun setMaxValueWithDecimal(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toFloatOrNull()
                if (value != null && value > 100) {
                    editText.error = "Nilai maksimal adalah 100"
                } else {
                    editText.error = null // Hapus error jika input valid
                }
            }
        })
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi Data")
        builder.setMessage(
                    "Nama Lengakap: ${formViewModel.name.value}\n" +
                    "NISN: ${formViewModel.nisn.value}\n" +
                    "NIK: ${formViewModel.nik.value}\n" +
                    "Jenis Kelamin: ${formViewModel.gender.value}\n" +
                    "Tanggal Lahi: ${formViewModel.birthdate.value}\n" +
                    "Tempat Lahir: ${formViewModel.birthPlace.value}\n" +
                    "Nama Orangtua: ${formViewModel.parentName.value}\n" +
                    "Alamat Lengkap: ${formViewModel.address.value}\n" +
                    "Kota/Kabupaten: ${formViewModel.city.value}\n" +
                    "No. Telp: ${formViewModel.phone.value}\n" +
                    "Asal Sekolah: ${formViewModel.schoolOrigin.value}\n" +
                    "Agama: ${formViewModel.religion.value}\n" +
                    "Foto KK, Akta, Foto Pribadi: ${formViewModel.driveLink.value}\n" +
                    "Nilai Bahasa Indonesia: ${formViewModel.nilaiIndo.value}\n" +
                    "Nilai Bahasa Inggris: ${formViewModel.nilaiIng.value}\n" +
                    "Nilai Matematika: ${formViewModel.nilaiMat.value}\n" +
                    "Nilai IPA: ${formViewModel.nilaiIPA.value}\n" +
                    "Foto Ijazah dan Sertifikat: ${formViewModel.driveLink2.value}\n\n" +
                    "Apakah data sudah benar?")

        builder.setPositiveButton("Ya") { _, _ ->
            submitDataToFirebase()
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun submitDataToFirebase() {
        // Ambil UID pengguna yang sedang login
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Cek apakah userId ada (pengguna sudah login)
        if (userId != null) {
            // Referensi ke node 'users' di Firebase Realtime Database
            val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
            val userRef = database.child("users").child(userId)

            // Siapkan data yang ingin disimpan dengan status awal "pending"
            val userData = mapOf(
                "name" to formViewModel.name.value,
                "nisn" to formViewModel.nisn.value,
                "nik" to formViewModel.nik.value,
                "gender" to formViewModel.gender.value,
                "birthdate" to formViewModel.birthdate.value,
                "birthPlace" to formViewModel.birthPlace.value,
                "parentName" to formViewModel.parentName.value,
                "address" to formViewModel.address.value,
                "city" to formViewModel.city.value,
                "phone" to formViewModel.phone.value,
                "schoolOrigin" to formViewModel.schoolOrigin.value,
                "religion" to formViewModel.religion.value,
                "driveLink" to formViewModel.driveLink.value,
                "nilaiIndo" to formViewModel.nilaiIndo.value,
                "nilaiIng" to formViewModel.nilaiIng.value,
                "nilaiMat" to formViewModel.nilaiMat.value,
                "nilaiIPA" to formViewModel.nilaiIPA.value,
                "driveLink2" to formViewModel.driveLink2.value,
                "status" to "pending" // Tambahkan status awal sebagai "pending"
            )

            // Simpan data ke Firebase
            userRef.setValue(userData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    navigateToNextFragment()  // Pindah ke fragment berikutnya
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseError", "Gagal menyimpan data: ${exception.localizedMessage}")
                    Toast.makeText(context, "Gagal menyimpan data: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Pengguna tidak terdeteksi!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNextFragment() {
        val nextFragment = Page3Fragment() // Ganti dengan fragment tujuan berikutnya
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Page3Fragment())
            .addToBackStack(null)
            .commit()
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