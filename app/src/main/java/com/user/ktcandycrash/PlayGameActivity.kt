package com.user.ktcandycrash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Process
import android.os.Process.myPid
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_play_game.*
import java.util.*
import kotlin.collections.ArrayList

var bt= arrayListOf<Button>()
var bx: Button?=null
var by: Button?=null

var bxx: Button?=null

var firstIdx =-1
var sconedIdx =-1

var grade = 0 //分數

class PlayGameActivity : AppCompatActivity() {
    val TAG=MainActivity::class.java.simpleName
    var imgbox = intArrayOf(
        R.drawable.greencandy,
        R.drawable.greapcandy,
        R.drawable.redcandy,
        R.drawable.yellowcandy,
        R.drawable.bluecandy,
        R.drawable.orangecandy,
        R.drawable.rainbowcandy,
        R.drawable.sugarcandy
    )
    val bgImg=R.drawable.boom
    val setalpha=AlphaAnimation(1.0f,0.0f)


    private inner class myClick: View.OnClickListener{


        override fun onClick(v: View) {
            Log.d(TAG,"ONCLICK")

            val b = v as Button
            b.isEnabled = false
            if(bx==null){
                //第一次按
                bx=b
            }
            else{
                for (n in 0..49){
                    if(b==bt[n]){
                        firstIdx = n
                        Log.e("bt",n.toString())
                    }
                    if(bx==bt[n]){
                        sconedIdx = n
                        Log.e("bx",n.toString())
                    }
                }

                Log.d(TAG,"hint")
                if(b.hint!=bx!!.hint){
                    //只要顏色不同，交換兩個

                    //確認位置是否相鄰
                    if(sconedIdx== firstIdx+1 || sconedIdx == firstIdx-1 || sconedIdx == firstIdx+10 || sconedIdx == firstIdx-10) {

                        if (sconedIdx== firstIdx+1){
                            movebutton(b, bx!!)
                        }else if(sconedIdx == firstIdx-1){
                            movebutton(b, bx!!)

                        }else if(sconedIdx == firstIdx+10){
                            movebutton(b, bx!!)

                        }else if(sconedIdx == firstIdx-10){
                            movebutton(b, bx!!)
                        }

                        bx!!.isEnabled = true
                        b.isEnabled = true
                        bx=null
                    }else{
                        bx!!.isEnabled = true
                        b.isEnabled = true
                        bx=null
                    }
                }else{
                    bx!!.isEnabled = true
                    b.isEnabled = true
                    bx=null
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        setcard()
        gradetext.text = getString(R.string.grade).plus("${grade}")
        Log.d(TAG,"onCreate")

        object : CountDownTimer(60000, 1000) {

            override fun onFinish() {
                //timertext.text = getString(R.string.done)
                AlertDialog.Builder(this@PlayGameActivity)
                    .setTitle(R.string.done)
                    .setMessage("你的最終分數是:"+ grade.toString())
                    .setPositiveButton("結束") { _, _ ->
                        val intent = Intent(this@PlayGameActivity,MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        Process.killProcess(Process.myPid())
                        System.exit(0)
                    }
                    .show()
            }

            override fun onTick(millisUntilFinished: Long) {
                timertext.text = getString(R.string.remain).plus("${millisUntilFinished/1000}")
            }
        }.start()
    }
    fun bomb(){

        var bombcount:Int=0;

        for(i in 0..29){
            if ( bt[i+20].hint!=null&& bt[i]!!.hint == bt[i + 10]!!.hint && bt[i]!!.hint == bt[i + 20]!!.hint) {

                if( bt[i].text=="-1"){
                    bombcount++
                    if(bt[i-10].hint=="6"){
                        bt[i-10].hint="7"
                        bt[i-10].text="7"
                    }else{
                        bt[i].hint="6"
                        bt[i].text="6"
                    }
                }
                else{
                    bt[i].text= "-1"
                    bombcount+=3
                }

                bt[i + 10].text = "-1"
                bt[i + 20].text = "-1"

            }
        }
        for(i in 0..47){
            if(i%10==8&&i%10==9)continue
            if (bt[i + 2].hint!=null && bt[i]!!.hint == bt[i + 1]!!.hint && bt[i]!!.hint == bt[i + 2]!!.hint) {
                if(bt[i].text=="-1"){
                    bombcount+=2
                    bt[i].hint="7"
                    bt[i].text="7"

                }
                else if( bt[i].text=="-2"){
                    if(bt[i-1].hint=="6"||bt[i-1].hint=="7"){
                        bt[i-1].hint="7"
                        bt[i-1].text="7"
                    }else{
                        if(bt[i-2].hint=="6"||bt[i-2].hint=="7")
                        else{
                            bt[i].hint="6"
                            bt[i].text="6"
                        }

                    }
                    bombcount++
                }
                else{
                    bt[i].text = "-2"
                    bombcount+=3
                }

                bt[i + 1].text = "-2"
                bt[i + 2].text = "-2"

            }
        }
        Log.d(TAG,"$bombcount")

        for( i in 0..49){
            if(bt[i].text=="-1"||bt[i].text=="-2"){
                bt[i].setBackgroundResource(bgImg)
                grade++
                gradetext.text = getString(R.string.grade).plus("${grade}")
            }
        }
        Handler().postDelayed({ deleteall()},1000)


    }

    fun bomb2(){
        for(i in 0..29){
            if ( bt[i+20].hint!=null&& bt[i]!!.hint == bt[i + 10]!!.hint && bt[i]!!.hint == bt[i + 20]!!.hint) {


                bt[i].text= "-1"
                bt[i + 10].text = "-1"
                bt[i + 20].text = "-1"

            }
        }
        for(i in 0..47){
            if(i%10==8&&i%10==9)continue
            if (bt[i + 2].hint!=null && bt[i]!!.hint == bt[i + 1]!!.hint && bt[i]!!.hint == bt[i + 2]!!.hint) {

                bt[i].text = "-1"
                bt[i + 1].text = "-1"
                bt[i + 2].text = "-1"

            }
        }
        for( i in 0..49){
            if(bt[i].text=="-1")
                bt[i].setBackgroundResource(bgImg)
        }
        deleteall()
    }
    var acount=5;
    fun deleteall(){
        var add=0;
        for(i in 0..49){
            if(bt[i].text=="-1"||bt[i].text=="-2"){

                var k=i;
                for( j in i%10 downTo 0){

                    if(j-1>=0){
                        bt[k].hint=bt[k-1].hint
                        bt[k].text=bt[k-1].text
                    }
                    else if(j-1<0) {
                        bt[k-k%10].hint=(add%6).toString()
                        bt[k-k%10].text=bt[k-k%10].hint
                        add++
                        break
                    }
                    k--
                }
            }
        }
        for (i in 0..49) {

            bt[i].setBackgroundResource(imgbox[bt[i]!!.hint.toString().toInt()])
        }
        if(acount>0){
            bomb()
            acount--;
        }
    }
   private fun setcard(){

        val rnd= Random(System.currentTimeMillis())
        val clickObj=myClick()
        for(i in 0..49){

            var x=Button(this)

            x.hint= rnd.nextInt(6).toString()
            x.text=x.hint
            x.setTextColor(Color.parseColor("#00FFFFFF"))
            x.setBackgroundResource(imgbox[x.hint.toString().toInt()])
            x.setOnClickListener(clickObj)
            when (i %10) {
                0 -> r1.addView(x)
                1 -> r2.addView(x)
                2 -> r3.addView(x)
                3 -> r4.addView(x)
                4 -> r5.addView(x)
                5 -> r6.addView(x)
                6 -> r7.addView(x)
                7 -> r8.addView(x)
                8 -> r9.addView(x)
                9 -> r10.addView(x)
            }
            bt.add (x)
        }
           for (i in 0..49){
               var params = bt[i].getLayoutParams()
               //params.height = 150
               params.width = 150
           }
        for(i in 0..10){
            bomb2()
        }
    }

    private fun movebutton(b:Button,bx:Button){
        setalpha.setDuration(500)
        b.startAnimation(setalpha)
        bx!!.startAnimation(setalpha)
        Handler().postDelayed({
            by = b
            bxx =bx
            val t = bxx!!.hint
            bxx!!.hint = by!!.hint
            by!!.hint = t
            bxx!!.text = bxx!!.hint
            by!!.text = by!!.hint
            bxx!!.setBackgroundResource(imgbox[bxx!!.hint.toString().toInt()])
            by!!.setBackgroundResource(imgbox[by!!.hint.toString().toInt()])
            bxx!!.isEnabled = true
            by!!.isEnabled = true
            bxx = null
            by = null
        }, 400)
        acount = 5;
        bomb()
    }
}
