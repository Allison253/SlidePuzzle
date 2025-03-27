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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var pvm: PuzzleViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding= DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainView=findViewById(R.id.mainLayout)

        pvm=ViewModelProvider(this)[PuzzleViewModel::class.java]
        if (pvm.curImg==null){
            pvm.curImg=BitmapFactory.decodeResource(resources, R.drawable.image)
        }


        //binding.puzzleViewModel=pvm
    }



    override fun onResume(){
        super.onResume()
        Log.d(lcm, "on Resume")

    }

    override fun onStop() {
        Log.d(lcm, mainView.childCount.toString())
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
        if (pvm.boardView==null){
            resetAndDraw(pvm.tileBy)
        }else{
            //REDRAW!
            mainView=findViewById(R.id.mainLayout)
            pvm.boardView!!.reDraw(mainView, pvm.tileBy*pvm.tileBy, pvm.boardView!!.emptyspace)
            binding.moves.text= "Number of Moves: ${pvm.numMoves}"
        }
        binding.shuffleBtn.setOnClickListener{(onShuffle())}
        binding.selectImage.setOnClickListener{(findPic())}
        binding.gridSelector.setOnClickListener{updateTileBy()}



    }


    private fun updateTileBy(){

        if (pvm.tileBy==3){
            pvm.tileBy=4
            binding.gridSelector.setImageResource(R.drawable.gridfour)
        }else{
            pvm.tileBy=3
            binding.gridSelector.setImageResource(R.drawable.grid)
        }
        resetAndDraw(pvm.tileBy)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(lcm, "on RESTART called")
    }

    private fun resetAndDraw(numTiles: Int){
        if (pvm.boardView==null){
            pvm.boardView= BoardView(this)
        }
        //reset values
        pvm.boardView!!.resetPuzzle(mainView)

        pvm.boardView!!.draw(mainView, numTiles*numTiles,pvm.curImg!!)
        for (myTile in pvm.boardView!!.p) {
            var myImage: ImageView=myTile.img
            myImage.setOnClickListener{(onClickImage(myTile.img.id, myTile))}

        }

    }




   private fun onClickImage(id: Int, tile:Tile){
       Log.d(ma, "Clicked for image id="+id.toString())
       pvm.numMoves += 1

        if (!pvm.isSolved){

            //row=tile.curp//tileby

            var row =tile.curp/(pvm.tileBy)
            var col=tile.curp%(pvm.tileBy)

            var emptyRow=pvm.boardView!!.emptyspace/(pvm.tileBy)
            var emptyCol=pvm.boardView!!.emptyspace%pvm.tileBy

            var clickspace  =tile.curp

            if (row==emptyRow || col==emptyCol){
                binding.moves.text= "Number of Moves: ${pvm.numMoves}"
                var shift: Int =0
                if (row==emptyRow){
                    shift=if(col<emptyCol)-1 else 1
                }else{
                    shift=if(row<emptyRow)(-pvm.tileBy) else pvm.tileBy
                }
                //to do : Debug here
                while (pvm.boardView!!.emptyspace!=clickspace){
                    //swap closest click space
                    //we must first find tile that is on the closest click space
                    pvm.boardView!!.moveTile(pvm.boardView!!.findTile(pvm.boardView!!.emptyspace+shift))
                }

                pvm.isSolved=pvm.boardView!!.isSolved()
                if (pvm.isSolved){
                    WinFragment().show(supportFragmentManager, "Win!")

                    if (mainView.resources.configuration.orientation==1){
                        val p=Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                            emitter = Emitter(200, TimeUnit.MILLISECONDS).max(100),
                            position = Position.Relative(0.5, 0.3),
                            timeToLive = 5000
                        )
                        binding.konfettiView?.start(p)
                    }else{
                        val p1=Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                            emitter = Emitter(200, TimeUnit.MILLISECONDS).max(100),
                            position = Position.Relative(0.5, 0.5),
                            timeToLive = 5000

                        )
                        val p2=Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                            emitter = Emitter(200, TimeUnit.MILLISECONDS).max(100),
                            position = Position.Relative(0.25, 0.5),
                            timeToLive = 5000

                        )
                        val p3=Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                            emitter = Emitter(200, TimeUnit.MILLISECONDS).max(100),
                            position = Position.Relative(0.75, 0.5),
                            timeToLive = 5000

                        )
                        binding.konfettiView?.start(p2)
                        binding.konfettiView?.start(p3)
                        binding.konfettiView?.start(p1)
                    }



                }

            }
        }



    }

    fun onShuffle(){

        pvm.numMoves=0
        binding.moves.text= "Number of Moves: 0"
        pvm.boardView!!.shuffleBoard(pvm.tileBy*pvm.tileBy)
        pvm.isSolved=false

    }

    fun findPic(){

        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
//TESTING WHAT HAPPENS IF WE SET IT TO NOTHINGGG THEN RESET
        pvm.boardView=null
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))

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
            pvm.curImg=bitmap
            resetAndDraw(pvm.tileBy)
        }




    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

      }






}