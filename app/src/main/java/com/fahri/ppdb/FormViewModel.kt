import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FormViewModel : ViewModel() {

    // Fields from the first page of the form
    val name = MutableLiveData<String>()
    val nisn = MutableLiveData<String>()
    val nik = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val birthdate = MutableLiveData<String>()
    val birthPlace = MutableLiveData<String>()
    val parentName = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val schoolOrigin = MutableLiveData<String>()
    val religion = MutableLiveData<String>()
    val driveLink = MutableLiveData<String>()
    val nilaiIndo = MutableLiveData<String>()
    val nilaiIng = MutableLiveData<String>()
    val nilaiMat = MutableLiveData<String>()
    val nilaiIPA = MutableLiveData<String>()
    val driveLink2 = MutableLiveData<String>()

    // Function to submit data to Firebase Realtime Database
    fun submitToDatabase() {
        val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        val userData = mapOf(
            "name" to name.value,
            "nisn" to nisn.value,
            "nik" to nik.value,
            "gender" to gender.value,
            "birthdate" to birthdate.value,
            "birthPlace" to birthPlace.value,
            "parentName" to parentName.value,
            "address" to address.value,
            "city" to city.value,
            "phone" to phone.value,
            "schoolOrigin" to schoolOrigin.value,
            "religion" to religion.value,
            "driveLink" to driveLink.value,
            "nilaiIndo" to nilaiIndo.value,
            "nilaiIng" to nilaiIng.value,
            "nilaiMat" to nilaiMat.value,
            "nilaiIPA" to nilaiIPA.value,
            "driveLink2" to driveLink2.value,
        )

        database.child("users").push().setValue(userData)
            .addOnSuccessListener {
                Log.d("FormViewModel", "Data successfully sent to Realtime Database")
            }
            .addOnFailureListener { e ->
                Log.e("FormViewModel", "Failed to send data: ${e.message}")
            }
    }

    fun setNilaiIndo(nilai: String) {
        nilaiIndo.value = nilai
    }

    fun setNilaiIng(nilai: String) {
        nilaiIng.value = nilai
    }

    fun setNilaiMat(nilai: String) {
        nilaiMat.value = nilai
    }

    fun setNilaiIPA(nilai: String) {
        nilaiIPA.value = nilai
    }

    fun setDriveLink2(link: String) {
        driveLink2.value = link
    }


}
