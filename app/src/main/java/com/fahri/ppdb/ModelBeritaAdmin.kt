package com.fahri.ppdb

data class ModelBeritaAdmin(
    var id: String = "",  // Tambahkan id untuk menyimpan kunci dari Firebase
    var judul: String = "",
    var isi: String = "",
    var lokasi: String = "",
    var tanggal: String = ""
)


