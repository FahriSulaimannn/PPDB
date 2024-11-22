package com.fahri.ppdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val userList: List<User>,
    private val onApproveClick: (User) -> Unit,
    private val onCancelClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.nama)
        val tvNisn: TextView = view.findViewById(R.id.nisn)
        val btnApprove: Button = view.findViewById(R.id.btnSetuju)
        val btnCancel: Button = view.findViewById(R.id.btnBatal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = user.name
        holder.tvNisn.text = user.nisn

        // Atur visibilitas tombol berdasarkan status
        if (user.status == "approve") {
            holder.btnApprove.visibility = View.GONE // Sembunyikan jika sudah disetujui
        } else {
            holder.btnApprove.visibility = View.VISIBLE // Tampilkan jika belum disetujui
        }

        holder.btnApprove.setOnClickListener { onApproveClick(user) }
        holder.btnCancel.setOnClickListener { onCancelClick(user) }
    }

    override fun getItemCount(): Int = userList.size
}
