package com.fahri.ppdb

import android.os.Parcel
import android.os.Parcelable

data class User(
    var id: String = "",
    val name: String = "",
    val nisn: String = "",
    val address: String = "",
    val birthPlace: String = "",
    val birthdate: String = "",
    val city: String = "",
    val driveAkta: String = "",
    val driveFoto: String = "",
    val driveFotoIjazah: String = "",
    val driveFotoSertif: String = "",
    val driveKK: String = "",
    val gender: String = "",
    val nik: String = "",
    val nilaiIPA: String = "",
    val nilaiIndo: String = "",
    val nilaiIng: String = "",
    val nilaiMat: String = "",
    val parentName: String = "",
    val phone: String = "",
    val religion: String = "",
    val schoolOrigin: String = "",
    var status: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        nisn = parcel.readString() ?: "",
        address = parcel.readString() ?: "",
        birthPlace = parcel.readString() ?: "",
        birthdate = parcel.readString() ?: "",
        city = parcel.readString() ?: "",
        driveAkta = parcel.readString() ?: "",
        driveFoto = parcel.readString() ?: "",
        driveFotoIjazah = parcel.readString() ?: "",
        driveFotoSertif = parcel.readString() ?: "",
        driveKK = parcel.readString() ?: "",
        gender = parcel.readString() ?: "",
        nik = parcel.readString() ?: "",
        nilaiIPA = parcel.readString() ?: "",
        nilaiIndo = parcel.readString() ?: "",
        nilaiIng = parcel.readString() ?: "",
        nilaiMat = parcel.readString() ?: "",
        parentName = parcel.readString() ?: "",
        phone = parcel.readString() ?: "",
        religion = parcel.readString() ?: "",
        schoolOrigin = parcel.readString() ?: "",
        status = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(nisn)
        parcel.writeString(address)
        parcel.writeString(birthPlace)
        parcel.writeString(birthdate)
        parcel.writeString(city)
        parcel.writeString(driveAkta)
        parcel.writeString(driveFoto)
        parcel.writeString(driveFotoIjazah)
        parcel.writeString(driveFotoSertif)
        parcel.writeString(driveKK)
        parcel.writeString(gender)
        parcel.writeString(nik)
        parcel.writeString(nilaiIPA)
        parcel.writeString(nilaiIndo)
        parcel.writeString(nilaiIng)
        parcel.writeString(nilaiMat)
        parcel.writeString(parentName)
        parcel.writeString(phone)
        parcel.writeString(religion)
        parcel.writeString(schoolOrigin)
        parcel.writeString(status)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User = User(parcel)
        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}