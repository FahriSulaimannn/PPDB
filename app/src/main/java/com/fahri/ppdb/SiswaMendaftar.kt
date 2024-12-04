package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SiswaMendaftar : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var etSearch: EditText
    private val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app").reference

    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siswa_mendaftar)
        enableEdgeToEdge()

        recyclerView = findViewById(R.id.recyclerView)
        etSearch = findViewById(R.id.etSearch)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchData()

        recyclerView.isNestedScrollingEnabled = false

        val cvBack = findViewById<CardView>(R.id.cvBack)

        // Handle tombol kembali menggunakan CardView
        cvBack.setOnClickListener {
            finish() // Menutup activity sepenuhnya
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchData() {
        val usersRef = database.child("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        user.id = userSnapshot.key ?: ""
                        userList.add(user)
                    }
                }

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
            context = this, // Tambahkan context
            userList = userList,
            onItemClick = { user -> navigateToDetail(user) },
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

    private fun navigateToDetail(user: User) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("user", user) // Kirim objek User
        startActivity(intent)
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
