package com.fahri.ppdb

import FormViewModel
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Page1Fragment : Fragment() {

    private val formViewModel: FormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page1, container, false)

        setStatusBarColor(R.color.black)
        setStatusBarTextDark(false)

        val etName = view.findViewById<EditText>(R.id.et_name)
        val etNISN = view.findViewById<EditText>(R.id.et_nisn)
        val etNIK = view.findViewById<EditText>(R.id.et_nik)
        val kelamin = view.findViewById<RadioGroup>(R.id.rg_kelamin)
        val etLaki = view.findViewById<RadioButton>(R.id.rb_laki)
        val etPerempuan = view.findViewById<RadioButton>(R.id.rb_perempuan)
        val etTglLahir = view.findViewById<EditText>(R.id.et_tgllahir)
        val etTmpLahir = view.findViewById<EditText>(R.id.et_tmplahir)
        val etOrtu = view.findViewById<EditText>(R.id.et_ortu)
        val etAlamat = view.findViewById<EditText>(R.id.et_alamat)
        val etKota = view.findViewById<EditText>(R.id.et_kota)
        val etTelp = view.findViewById<EditText>(R.id.et_telp)
        val etAsalSekolah = view.findViewById<EditText>(R.id.et_asal_sekolah)
        val etAgama = view.findViewById<Spinner>(R.id.spinner_agama)
        val etDrive = view.findViewById<EditText>(R.id.et_linkKK)
        val etDrive2 = view.findViewById<EditText>(R.id.et_linkakta)
        val etDrive3 = view.findViewById<EditText>(R.id.et_linkfoto)
        val tvLabelGender = view.findViewById<TextView>(R.id.label_gender)
        val btnLanjutkan = view.findViewById<Button>(R.id.btn_lanjutkan)
        val cvBack = view.findViewById<CardView>(R.id.cvBack)

        etDrive.addDriveLinkValidator()
        etDrive2.addDriveLinkValidator()
        etDrive3.addDriveLinkValidator()

        // Set tanggal saat diklik
        etTglLahir.setOnClickListener {
            showDatePickerDialog(etTglLahir)
        }

        // Temukan cvBack dan tambahkan klik listener
        cvBack.setOnClickListener {
            activity?.onBackPressed()  // Kembali ke halaman sebelumnya
        }

        btnLanjutkan.setOnClickListener {
            val nisn = etNISN.text.toString()
            val nik = etNIK.text.toString()

            if (validateFields(etNISN, etNIK, etTelp, etName, etTglLahir, etTmpLahir, etOrtu, etAlamat, etKota, etAsalSekolah, etDrive, etDrive2, etDrive3) &&
                validateSpinner(etAgama) && validateRadioGroup(kelamin, tvLabelGender)) {

                checkUniqueNISNAndNIK(nisn, nik) { isUnique, errorMessage ->
                    if (isUnique) {
                        // Simpan data ke ViewModel
                        formViewModel.name.value = etName.text.toString()
                        formViewModel.nisn.value = nisn
                        formViewModel.nik.value = nik
                        formViewModel.gender.value = if (etLaki.isChecked) "Laki-laki" else "Perempuan"
                        formViewModel.birthdate.value = etTglLahir.text.toString()
                        formViewModel.birthPlace.value = etTmpLahir.text.toString()
                        formViewModel.parentName.value = etOrtu.text.toString()
                        formViewModel.address.value = etAlamat.text.toString()
                        formViewModel.city.value = etKota.text.toString()
                        formViewModel.phone.value = etTelp.text.toString()
                        formViewModel.schoolOrigin.value = etAsalSekolah.text.toString()
                        formViewModel.religion.value = etAgama.selectedItem.toString()
                        formViewModel.driveKK.value = etDrive.text.toString()
                        formViewModel.driveAkta.value = etDrive2.text.toString()
                        formViewModel.driveFoto.value = etDrive3.text.toString()

                        // Navigasi ke halaman kedua
                        (activity as MainActivity4).navigateToPage2()
                    } else {
                        // Tampilkan pesan error
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        etNISN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu digunakan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak perlu digunakan
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 10) {
                    etNISN.error = "NISN harus terdiri dari 10 angka"
                } else {
                    etNISN.error = null // Menghapus error jika jumlah karakter sudah tepat 10
                }
            }
        })

        etNIK.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu digunakan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak perlu digunakan
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 16) {
                    etNIK.error = "NIK harus terdiri dari 16 angka"
                } else {
                    etNIK.error = null // Menghapus error jika jumlah karakter sudah tepat 10
                }
            }
        })

        etTelp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu digunakan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak perlu digunakan
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()

                when {
                    !input.startsWith("08") -> {
                        etTelp.error = "No.Telp harus diawali dengan '08'"
                    }
                    input.length != 12 -> {
                        etTelp.error = "No.Telp harus terdiri dari 12 angka"
                    }
                    else -> {
                        etTelp.error = null // Menghapus error jika panjang dan format benar
                    }
                }
            }
        })

        return view
    }

    private fun validateFields(etNISN: EditText, etNIK: EditText, etTelp: EditText, vararg fields: EditText): Boolean {
        var allFieldsValid = true
        fields.forEach { field ->
            if (field.text.isBlank()) {
                field.error = "Harus diisi"
                field.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
                allFieldsValid = false
            } else {
                field.error = null
                field.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
            }
        }

        // Tambahkan validasi untuk etNISN
        if (etNISN.text.isBlank()) {
            etNISN.error = "Harus diisi"
            etNISN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
            allFieldsValid = false
        } else if (etNISN.text.length != 10) {
            etNISN.error = "NISN harus terdiri dari 10 angka"
            etNISN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
            allFieldsValid = false
        } else {
            etNISN.error = null
            etNISN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }

        // Tambahkan validasi untuk etNIK
        if (etNIK.text.isBlank()) {
            etNIK.error = "Harus diisi"
            etNIK.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
            allFieldsValid = false
        } else if (etNIK.text.length != 16) {
            etNIK.error = "NIK harus terdiri dari 16 angka"
            etNIK.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
            allFieldsValid = false
        } else {
            etNIK.error = null
            etNIK.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }

        // Tambahkan validasi untuk etTelp
        if (etTelp.text.isBlank()) {
            etTelp.error = "Harus diisi"
            etTelp.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
            allFieldsValid = false
        } else if (etTelp.text.length != 12) {
            etTelp.error = "Nomor telepon harus terdiri dari 12 angka"
            etTelp.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light)
            allFieldsValid = false
        } else {
            etTelp.error = null
            etTelp.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }

        return allFieldsValid
    }

    private fun validateSpinner(spinner: Spinner): Boolean {
        return if (spinner.selectedItemPosition == 0) { // Assuming the first item is a placeholder like "Select Agama"
            (spinner.selectedView as TextView).error = "Harus dipilih"
            (spinner.selectedView as TextView).setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
            false
        } else {
            true
        }
    }

    private fun validateRadioGroup(radioGroup: RadioGroup, label: TextView): Boolean {
        return if (radioGroup.checkedRadioButtonId == -1) { // Tidak ada pilihan
            label.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
            label.text = "Jenis Kelamin (Harus dipilih)" // Menambahkan peringatan
            false
        } else {
            // Jika ada pilihan, kembalikan teks label ke normal
            label.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            label.text = "Jenis Kelamin"
            true
        }
    }

    private fun EditText.addDriveLinkValidator() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val googleDrivePattern = "https://drive\\.google\\.com/.*".toRegex()
                if (!googleDrivePattern.matches(s.toString())) {
                    this@addDriveLinkValidator.error = "Masukkan link Google Drive yang valid"
                } else {
                    this@addDriveLinkValidator.error = null
                }
            }
        })
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()

        // Batas maksimum: 15 tahun yang lalu
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.YEAR, -15)  // Mengurangi 15 tahun dari sekarang

        // Batas minimum: 21 tahun yang lalu
        val minDateCalendar = Calendar.getInstance()
        minDateCalendar.add(Calendar.YEAR, -21)  // Mengurangi 21 tahun dari sekarang

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val today = Calendar.getInstance()
                val age = today.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR)

                // If the selected date makes age less than 15 or more than 21, show error
                if (age < 15 || age > 21) {
                    editText.error = "Tanggal lahir harus membuat usia antara 15 dan 21 tahun."
                } else {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    editText.setText(dateFormat.format(selectedDate.time))
                    // Clear error if valid
                    editText.error = null
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to 21 years ago
        datePickerDialog.datePicker.minDate = minDateCalendar.timeInMillis

        // Set the maximum date to 15 years ago
        datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis

        datePickerDialog.show()
    }

    private fun checkUniqueNISNAndNIK(
        nisn: String,
        nik: String,
        onResult: (isUnique: Boolean, errorMessage: String?) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var nisnExists = false
                var nikExists = false

                for (child in snapshot.children) {
                    val userNisn = child.child("nisn").getValue(String::class.java)
                    val userNik = child.child("nik").getValue(String::class.java)

                    if (userNisn == nisn) {
                        nisnExists = true
                    }
                    if (userNik == nik) {
                        nikExists = true
                    }

                    // Jika kedua data ditemukan, hentikan pencarian lebih awal
                    if (nisnExists && nikExists) break
                }

                when {
                    nisnExists && nikExists -> onResult(false, "NISN dan NIK sudah terdaftar.")
                    nisnExists -> onResult(false, "NISN sudah terdaftar.")
                    nikExists -> onResult(false, "NIK sudah terdaftar.")
                    else -> onResult(true, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false, "Gagal memeriksa data: ${error.message}")
            }
        })
    }

    private fun setStatusBarColor(colorResId: Int) {
        activity?.window?.let { window ->
            window.statusBarColor = ContextCompat.getColor(requireContext(), colorResId)
        }
    }

    private fun setStatusBarTextDark(isDark: Boolean) {
        val decorView = activity?.window?.decorView
        if (decorView != null) {
            val flags = decorView.systemUiVisibility
            decorView.systemUiVisibility = if (isDark) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }


}
