package com.fahri.ppdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterBeritaAdmin(
    private val dataList: List<ModelBeritaAdmin>,
    private val onItemClick: (ModelBeritaAdmin) -> Unit
) : RecyclerView.Adapter<AdapterBeritaAdmin.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val judul: TextView = itemView.findViewById(R.id.judul)
        val lokasi: TextView = itemView.findViewById(R.id.lokasi)
        val tgl: TextView = itemView.findViewById(R.id.tgl)
        val isi: TextView = itemView.findViewById(R.id.isi)

        fun bind(berita: ModelBeritaAdmin) {
            judul.text = berita.judul
            lokasi.text = berita.lokasi
            tgl.text = berita.tanggal
            isi.text = berita.isi

            itemView.setOnClickListener {
                onItemClick(berita) // Panggil callback saat item diklik
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_berita_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}
