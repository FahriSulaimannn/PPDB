package com.fahri.ppdb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fahri.ppdb.databinding.CardPeringkatBinding
import com.fahri.ppdb.databinding.HeaderPeringkatBinding

class AdapterPeringkat(private var itemList: List<ModelPeringkat>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER_VIEW_TYPE = 0
    private val ITEM_VIEW_TYPE = 1

    inner class HeaderViewHolder(val binding: HeaderPeringkatBinding) : RecyclerView.ViewHolder(binding.root)

    inner class PeringkatViewHolder(val binding: CardPeringkatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_VIEW_TYPE -> {
                val binding = HeaderPeringkatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            ITEM_VIEW_TYPE -> {
                val binding = CardPeringkatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PeringkatViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                // Tidak ada aksi untuk header
            }
            is PeringkatViewHolder -> {
                if (position > 0) {
                    val item = itemList[position - 1]
                    holder.binding.apply {
                        tvNama.text = item.name
                        tvJumlahNilai.text = item.nilai.toString()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = itemList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_VIEW_TYPE else ITEM_VIEW_TYPE
    }

    fun updateData(newList: List<ModelPeringkat>) {
        itemList = newList
        notifyDataSetChanged()
    }
}

