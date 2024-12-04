package com.fahri.ppdb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fahri.ppdb.databinding.CardPeringkatBinding

class AdapterPeringkat(
    private var itemList: List<ModelPeringkat>
) : RecyclerView.Adapter<AdapterPeringkat.PeringkatViewHolder>() {

    inner class PeringkatViewHolder(val binding: CardPeringkatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeringkatViewHolder {
        val binding = CardPeringkatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeringkatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeringkatViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.apply {
            tvNama.text = item.name
            tvJumlahNilai.text = item.nilai.toString()
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateData(newList: List<ModelPeringkat>) {
        itemList = newList
        notifyDataSetChanged()
    }
}
