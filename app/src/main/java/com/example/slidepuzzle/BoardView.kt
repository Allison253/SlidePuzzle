package com.example.slidepuzzle

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import java.util.Collections
import java.util.Dictionary
import kotlin.math.floor
import kotlin.math.sqrt
const val bv="MyBoardView"
class BoardView (context:Context): View (context) {

    lateinit var newView:ImageView
    val p=ArrayList<Tile>()
    val d= mutableMapOf<Int, position>()

    var emptyspace: Int=0



    //https://medium.com/@NumberShapes/kotlin-dynamically-creating-an-imageview-during-runtime-aec9268f9ccf

    fun draw(myView: ViewGroup, numTiles: Int, curBitmap: Bitmap) {
        val tiles: Int= sqrt(numTiles.toDouble()).toInt()
        var w:Int=0
        var posx=0F
        var posy=0F
        if (myView.resources.configuration.orientation==1){
            val screenWidth = myView.width-50
            w= (screenWidth-(10*tiles))/tiles //for imageView
             posx = 25F
             posy = 250F

        }else{
            val screenHeight = myView.height-50
            w= (screenHeight-(10*tiles))/tiles //for imageView
            posy = 25F
            posx = 25F
        }





        var tileWidth=curBitmap.width/tiles //for image
        var tileHeight=curBitmap.height/tiles //for image
        emptyspace=numTiles-1
        for (i in 0..<numTiles-1){
            var myTile:Tile= Tile(i)

            //var image: Image= Image(R.drawable.vince)

            var col: Int=(i)%tiles
            var row: Int= floor((i.toDouble())/tiles).toInt()

            var newBitmap=Bitmap.createBitmap(curBitmap, col * tileWidth, row * tileHeight, tileWidth, tileHeight)

            newView = ImageView(context)
            myView.addView(newView)
            newView.id=i
            newView.layoutParams.height=w
            newView.layoutParams.width = w
            newView.x = posx
            newView.y = posy
            //newView.setImageResource(R.drawable.vince)
            var md= BitmapDrawable(resources, newBitmap)
            newView.setImageDrawable(md)
            newView.scaleType= ImageView.ScaleType.FIT_XY

            myTile.img=newView
            myTile.curp=i
            myTile.fp=i
            var myPos: position =position()
            myPos.x=posx
            myPos.y=posy
            d[i]=myPos

            if (col%tiles!=tiles-1){
                posx+=(w+10)
            }else{
                posx=25F
                posy+=(w+10)
            }
            p.add(myTile)

        }

        //add final empty space to position dictionary
        var myPos: position =position()
        myPos.x=posx
        myPos.y=posy
        d[numTiles-1]=myPos

    }


    fun reDraw(myView: ViewGroup, numTiles:Int, curBitmap: Bitmap, listPos: ArrayList<Int>,  curEmptySpace: Int){
        //redraw at new positions
        emptyspace=curEmptySpace
        val tiles: Int= sqrt(numTiles.toDouble()).toInt()
        var w:Int=0
        var posx=0F
        var posy=0F
        var startX=0F
        var startY=0F
        if (myView.resources.configuration.orientation==1){
            val screenWidth = myView.width-50
            w= (screenWidth-(10*tiles))/tiles //for imageView
            startX = 25F
            startY = 250F

        }else{
            val screenHeight = myView.height-50
            w= (screenHeight-(10*tiles))/tiles //for imageView
            startX = 25F
            startY = 25F
        }





        var tileWidth=curBitmap.width/tiles //for image
        var tileHeight=curBitmap.height/tiles //for image

        //to do: implement draw function for redraw
        //to do: Merge draw and redraw functions??
    }

    fun moveTile(t:Tile){
        //record current position to reset later
        var prevPosIndex=t.curp

        //find position of empty space
        var newPos:position= d[emptyspace]!!
        //set curp as the empty space
        t.curp=emptyspace
        //move image
        t.img.x=newPos.x
        t.img.y=newPos.y

        //reset empty
        emptyspace=prevPosIndex


    }

    fun findTile(positionToSearch: Int): Tile{
        for (tile in p){
            if (tile.curp==positionToSearch){
                return tile
            }
        }
        return p[0]
    }

    fun shuffleBoard(numTiles: Int){
        var shuffled = (-1..numTiles-2).toList().shuffled()
        shuffled=shuffled.toMutableList()
        if (!isSolvable(shuffled, numTiles)){
            //swap two first positions
            if (shuffled[0]!=-1){
                Collections.swap(shuffled,0,1)
            }else{
                Collections.swap(shuffled,1,2)
            }

        }
        for (i in 0..<numTiles){
            if (shuffled[i]!=-1){
                var myTile: Tile=p[shuffled[i]]
                var newPos:position= d[i]!!
                myTile.curp=i //reset curp
                myTile.img.x=newPos.x
                myTile.img.y=newPos.y
                //moveTile
            }else{
                emptyspace=i
            }



        }
    }

    private fun isSolvable(shuffledArray: MutableList<Int>, numTiles:Int) : Boolean{
        //countInversions
        var numberInversions: Int=0
        var arrSize=shuffledArray.size

        //to do: account for variable empty position
        //see here: https://developerslogblog.wordpress.com/2020/04/01/how-to-shuffle-an-slide-puzzle/#:~:text=You%20are%20probably%20wondering%20if,solved%20try%20as%20they%20may.
        for (i in 0..<arrSize){
            for (j in i+1..<arrSize){
                if (shuffledArray[j]!=-1 && shuffledArray[i]>shuffledArray[j]){
                    numberInversions+=1
                }
            }
        }

        if (numTiles%2==1) {
            return numberInversions % 2 == 0
        }else {
            return numberInversions % 2 == 0
        }





    }

    fun isSolved() : Boolean{
        for (tile in p) {
            if (tile.curp!=tile.fp){
                return false
            }

        }
        return true
    }

    fun resetPuzzle(myView: ViewGroup){


        if (myView.childCount>9){
            myView.removeViews(5,myView.childCount-5)
        }

        p.clear()
        d.clear()


    }



}