package com.ly.studydemo.binder

import android.os.Parcel
import android.os.Parcelable

class MyData() : Parcelable {

    var data1: Int = 0
    var data2: Int = 0

    constructor(parcel: Parcel) : this() {
        readFromParcel(parcel)
    }

    private fun readFromParcel(parcel: Parcel) {
        data1 = parcel.readInt()
        data2 = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(data1)
        parcel.writeInt(data2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyData> {
        override fun createFromParcel(parcel: Parcel): MyData {
            return MyData(parcel)
        }

        override fun newArray(size: Int): Array<MyData?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "MyData(data1=$data1, data2=$data2)"
    }

}