package com.fahri.ppdb

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SiswaMendaftar : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var etSearch: EditText
    private val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app").reference

    private val userList = mutableListOf<User>() // List utama untuk menyimpan semua data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siswa_mendaftar)

        recyclerView = findViewById(R.id.recyclerView)
        etSearch = findViewById(R.id.etSearch)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchData()

        // Tambahkan listener untuk input pencarian
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchData() {
        val usersRef = database.child("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        user.id = userSnapshot.key ?: "" // Assign key as ID
                        userList.add(user)
                    }
                }

                // Urutkan daftar berdasarkan status (belum approve di atas)
                val sortedList = userList.sortedBy { it.status == "approve" }
                setupAdapter(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SiswaMendaftar, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAdapter(userList: List<User>) {
        userAdapter = UserAdapter(
            userList,
            onApproveClick = { user -> approveUser(user) },
            onCancelClick = { user -> deleteUser(user) }
        )
        recyclerView.adapter = userAdapter
    }

    private fun filterList(query: String) {
        val filteredList = userList.filter {
            it.name.contains(query, ignoreCase = true) || it.nisn.contains(query, ignoreCase = true)
        }
        setupAdapter(filteredList)
    }

    private fun approveUser(user: User) {
        val userRef = database.child("users").child(user.id)
        userRef.child("status").setValue("approve").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "${user.name} disetujui.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyetujui ${user.name}.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUser(user: User) {
        val userRef = database.child("users").child(user.id)
        userRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "${user.name} berhasil dihapus.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menghapus ${user.name}.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
