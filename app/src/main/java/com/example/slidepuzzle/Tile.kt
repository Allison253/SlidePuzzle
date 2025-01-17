package com.example.slidepuzzle

import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import androidx.versionedparcelable.VersionedParcelize


class Tile(id: Int){
        var id=id
        lateinit var img: ImageView
        var curp: Int=0 //current position
        var fp: Int=0 //final, correct position


}