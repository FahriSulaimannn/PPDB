package com.fahri.ppdb

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dataContainer = findViewById<LinearLayout>(R.id.dataContainer)

        // Get the currently logged-in user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid

        if (userUID != null) {
            // Reference to Firebase Database for the logged-in user's data
            val databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(userUID)

            // Fetch data from Firebase
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Clear previous data
                    dataContainer.removeAllViews()

                    // Loop through all the children and add them as TextViews
                    for (dataSnapshot in snapshot.children) {
                        val data = dataSnapshot.key + ": " + dataSnapshot.getValue(String::class.java)
                        if (data != null) {
                            val textView = TextView(this@MainActivity5)
                            textView.text = data
                            textView.textSize = 18f
                            textView.setPadding(0, 10, 0, 10)
                            dataContainer.addView(textView)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
        }
    }
}
