package com.fahri.ppdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterBerita(
    private val dataList: List<ModelBerita>
    ) : RecyclerView.Adapter<AdapterBerita.DataViewHolder>() {

    // ViewHolder untuk menyimpan referensi elemen dalam layout
    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val judulTextView: TextView = itemView.findViewById(R.id.judul)
        val lokasiTextView: TextView = itemView.findViewById(R.id.lokasi)
        val tglTextView: TextView = itemView.findViewById(R.id.tgl)
        val isiTextView: TextView = itemView.findViewById(R.id.isi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        // Inflate layout XML ke dalam ViewHolder
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_berita, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        // Ambil data sesuai posisi
        val data = dataList[position]
        // Binding data ke ViewHolder
        holder.judulTextView.text = data.judul
        holder.lokasiTextView.text = data.lokasi
        holder.tglTextView.text = data.tgl
        holder.isiTextView.text = data.isi
    }

    override fun getItemCount(): Int {
        return dataList.size // Jumlah item dalam data
    }
}
