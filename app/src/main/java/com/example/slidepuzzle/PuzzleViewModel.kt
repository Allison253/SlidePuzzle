package com.example.slidepuzzle

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel

class PuzzleViewModel: ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var boardView: BoardView? = null

    var isSolved: Boolean=true
    var tileBy: Int=3
    var numMoves: Int=0

    var curImg: Bitmap?=null
    
}