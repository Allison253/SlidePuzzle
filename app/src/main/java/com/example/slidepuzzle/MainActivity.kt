package com.example.slidepuzzle

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.icu.text.Transliterator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.slidepuzzle.databinding.ActivityMainBinding

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit



private lateinit  var mainView: ViewGroup

const val ma="MyMainActivty"
const val lcm="LifeCycleManagement"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit  var boardView: BoardView
    var tileBy: Int=0

    var numMoves: Int=0
    private var isSolved:Boolean= true

    private lateinit var curImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainView=findViewById(R.id.mainLayout)
        //TO DO: Allow user to select 3x3 or 4x4 puzzle or do it random (POD style)
        //for now, set it to 3
        tileBy=3
        curImage=BitmapFactory.decodeResource(resources, R.drawable.image)


    }





    override fun onResume(){
        super.onResume()
        Log.d(lcm, "on Resume")
    }

    override fun onStop() {
        super.onStop()
        Log.d(lcm, "Stopped")
    }

    override fun onPause() {
        super.onPause()
        Log.d(lcm, "on pause")
        
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (!::boardView.isInitialized){
            boardView= BoardView(this)
            binding.shuffleBtn.setOnClickListener{(onShuffle())}
            binding.selectImage.setOnClickListener{(findPic())}
            binding.gridSelector.setOnClickListener{updateTileBy()}
            SetNumTiles(tileBy)


        }


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("Testing", savedInstanceState.getInt("numMoves").toString())
        //TO DO: Redraw

    }

    private fun updateTileBy(){

        if (tileBy==3){
            tileBy=4
            binding.gridSelector.setImageResource(R.drawable.gridfour)
        }else{
            tileBy=3
            binding.gridSelector.setImageResource(R.drawable.grid)
        }
        SetNumTiles(tileBy)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(lcm, "on RESTART called")
    }

    private fun SetNumTiles(numTiles: Int){
        //reset values
        boardView.resetPuzzle(mainView)

        tileBy=numTiles
        boardView.draw(mainView, numTiles*numTiles,curImage)
        for (myTile in boardView.p) {

            var myImage: ImageView=myTile.img

            myImage.setOnClickListener{(onClickImage(myTile.img.id, myTile))}


        }
    }




   private fun onClickImage(id: Int, tile:Tile){
       Log.d(ma, "Clicked for image id="+id.toString())
       numMoves += 1

        if (!isSolved){

            //row=tile.curp//tileby

            var row =tile.curp/(tileBy)
            var col=tile.curp%(tileBy)

            var emptyRow=boardView.emptyspace/(tileBy)
            var emptyCol=boardView.emptyspace%tileBy

            var clickspace  =tile.curp

            if (row==emptyRow || col==emptyCol){
                binding.moves.text= "Number of Moves: $numMoves"
                var shift: Int =0
                if (row==emptyRow){
                    shift=if(col<emptyCol)-1 else 1
                }else{
                    shift=if(row<emptyRow)(-tileBy) else tileBy
                }
                //to do : Debug here
                while (boardView.emptyspace!=clickspace){
                    //swap closest click space
                    //we must first find tile that is on the closest click space
                    boardView.moveTile(boardView.findTile(boardView.emptyspace+shift))
                }

                isSolved=boardView.isSolved()
                if (isSolved){
                    WinFragment().show(supportFragmentManager, "Win!")

                    val p=Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        emitter = Emitter(200, TimeUnit.MILLISECONDS).max(100),
                        position = Position.Relative(0.5, 0.3)
                    )
                    binding.konfettiView?.start(p)

                    

                }

            }
        }



    }

    fun onShuffle(){

        numMoves=0
        binding.moves.text= "Number of Moves: 0"
        boardView.shuffleBoard(tileBy*tileBy)
        isSolved=false

    }

    fun findPic(){

        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
        Log.d("resultLauncherTest","made it past")

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("resultLauncherTest","activity result called")

        if (resultCode==Activity.RESULT_OK) {
            val uri: Uri? =data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = uri?.let { contentResolver.query(it, filePathColumn, null, null, null) }
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            val test=uri.path

           var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            var source=ImageDecoder.createSource(this.contentResolver, uri)
            val bitmap2=ImageDecoder.decodeBitmap(source)
            curImage=bitmap
            SetNumTiles(tileBy)
        }




    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


      }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt("numMoves", numMoves)

        //create a list of positions
        var listPos=ArrayList<Int>()

        for (i in boardView.p.indices){

            listPos.add(boardView.p[i].curp)
        }
        outState.putIntegerArrayList("ListOfPositions", listPos)
        outState.putParcelable("myImage", curImage)




    }





}