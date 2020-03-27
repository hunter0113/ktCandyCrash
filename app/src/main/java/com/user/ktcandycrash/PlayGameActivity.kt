package com.user.ktcandycrash
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_play_game.*


class PlayGameActivity : AppCompatActivity() {

    var btx: Button? = null
    var firstLocation: String = ""
    var secondLocation: String = ""
    var btarr= arrayListOf<Button>()
    var removeYes=0
    //紀錄顏色
    val mapColor = Array(12, { Array(12, {it-> -100 }) })
    //紀錄位置
    val mapLocation = Array(12, { Array(12, { it -> "" }) })

    //確認相鄰
    var arenext = 0

    //若交換失敗的動畫，有返回動畫
    //向右
    val rani = TranslateAnimation(0f, -200f, 0f, 0f)
    //向左
    val lani = TranslateAnimation(0f, +200f, 0f, 0f)
    //向上
    val uani = TranslateAnimation(0f, 0f, 0f, -200f)
    //向下
    val dani = TranslateAnimation(0f, 0f, 0f, +200f)

    //若交換成功的動畫，無返回動畫
    //向右
    val rani2 = TranslateAnimation(0f, -200f, 0f, 0f)
    //向左
    val lani2 = TranslateAnimation(0f, +200f, 0f, 0f)
    //向上
    val uani2 = TranslateAnimation(0f, 0f, 0f, -200f)
    //向下
    val dani2 = TranslateAnimation(0f, 0f, 0f, +200f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)
        var count = 0
        val rnd = Random()

        for (i in 0..11) {
            for (j in 0..7) {
                if(i>=2&&j>=2){
                    if(i<=9&&j<=5) {
                        //不大於五的隨機數
                        count++
                        mapColor[i][j] = rnd.nextInt(5)
                        Log.e("number", mapColor[i][j].toString())
                    }
                }
                else{
                    mapColor[i][j] = -1
                }
            }
        }

        rani.duration = 500
        lani.duration = 500
        uani.duration = 500
        dani.duration = 500
        rani.repeatCount = 1
        lani.repeatCount = 1
        uani.repeatCount = 1
        dani.repeatCount = 1
        rani.repeatMode = ValueAnimator.REVERSE
        lani.repeatMode = ValueAnimator.REVERSE
        uani.repeatMode = ValueAnimator.REVERSE
        dani.repeatMode = ValueAnimator.REVERSE

        rani2.duration = 500
        lani2.duration = 500
        uani2.duration = 500
        dani2.duration = 500
        rani2.repeatCount = 0
        lani2.repeatCount = 0
        uani2.repeatCount = 0
        dani2.repeatCount = 0
        rani2.repeatMode = ValueAnimator.REVERSE
        lani2.repeatMode = ValueAnimator.REVERSE
        uani2.repeatMode = ValueAnimator.REVERSE
        dani2.repeatMode = ValueAnimator.REVERSE



        for (i in 0..11) {
            for (j in 0..7){

                var x = Button(this)
                var xText = ""
                xText = mapColor[i][j].toString()
                x.text=""

                if(i>=2&&j>=2){
                    if(i<=9&&j<=5){

                        //記住位置
                        mapLocation[i][j]=i.toString()+","+j.toString()+","+xText
                        Log.e("ij", mapLocation[i][j])
                        val getC = mapLocation[i][j].split(",")
                        //getC[0] 為y軸
                        //getC[1] 為x軸
                        //getC[2] 為顏色

                        if(getC[2].equals("0")){
                            x.setBackgroundResource(R.drawable.bluecandy)
                        }
                        if(getC[2].equals("1")){
                            x.setBackgroundResource(R.drawable.greapcandy)
                        }
                        if(getC[2].equals("2")){
                            x.setBackgroundResource(R.drawable.greencandy)
                        }
                        if(getC[2].equals("3")){
                            x.setBackgroundResource(R.drawable.redcandy)
                        }
                        if(getC[2].equals("4")){
                            x.setBackgroundResource(R.drawable.yellowcandy)
                        }
                        if(getC[2].equals("7")){
                            x.setBackgroundResource(R.drawable.chocolate)
                        }

                        x.setOnClickListener(View.OnClickListener { v ->
                            //b為第二個(當下按的) btx為上一次的
                            val b = v as Button
                            Log.e("按下", "("+getC[0]+","+getC[1]+")")
                            //b.isEnabled = false  讓他不能按
                            if (btx == null || btx==b) {
                                //表示為第一次按 把它存起來然後按第二次 並記錄位置 判定避免同一個連按
                                btx = b
                                firstLocation=getC[0]+","+getC[1]+","+getC[2]

                            }else {
                                secondLocation=getC[0]+","+getC[1]+","+getC[2]
                                //產生一對 兩個都變白清空 一樣清空btx
                                var splitfrist= firstLocation.split(",")
                                var splitsecond= secondLocation.split(",")
                                Log.e("splitfrist", firstLocation)
                                Log.e("splitsecond", secondLocation)
                                //splitfrist[0] 為y軸
                                //splitfrist[1] 為x軸
                                //splitsecond[0] 為y軸
                                //splitsecond[1] 為x軸

                                //splitfrist[2] 為第一顏色
                                //splitsecond[2] 為第二顏色
                                if(mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])] != -2 && mapColor[Integer.parseInt(splitsecond[0])][Integer.parseInt(splitsecond[1])] != -2){

                                    val xx = Integer.parseInt(splitfrist[0]) - Integer.parseInt(splitsecond[0])
                                    val yy = Integer.parseInt(splitfrist[1]) - Integer.parseInt(splitsecond[1])

                                    //交換顏色/數字 取得這兩個按鈕的位置 再去mapcolor將對應位置換顏色
                                    var saveColor = mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]
                                    mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])] = mapColor[Integer.parseInt(splitsecond[0])][Integer.parseInt(splitsecond[1])]
                                    mapColor[Integer.parseInt(splitsecond[0])][Integer.parseInt(splitsecond[1])] = saveColor
                                    removeYes = 0
                                    checkAll()

                                    if(xx==0&&yy==1){
                                        if(removeYes==1){
                                            //先右後左
                                            Log.e("AAAAAA", "先右後左")
                                            b.startAnimation(lani2)
                                            btx!!.startAnimation(rani2)
                                            arenext=1
                                        }else{
                                            //先右後左
                                            Log.e("AAAAAA", "先右後左")
                                            b.startAnimation(lani)
                                            btx!!.startAnimation(rani)
                                            arenext=1
                                        }
                                    }
                                    if(xx==0&&yy==-1){
                                        if(removeYes==1){
                                            //先左後右
                                            Log.e("AAAAAA", "先左後右")
                                            b.startAnimation(rani2)
                                            btx!!.startAnimation(lani2)
                                            arenext=1
                                        }else{
                                            //先左後右
                                            Log.e("AAAAAA", "先左後右")
                                            b.startAnimation(rani)
                                            btx!!.startAnimation(lani)
                                            arenext=1
                                        }
                                    }
                                    if(xx==1&&yy==0){
                                        if(removeYes==1){
                                            //先下後上
                                            Log.e("AAAAAA", "先下後上")
                                            b.startAnimation(dani2)
                                            btx!!.startAnimation(uani2)
                                            arenext=1
                                        }else{
                                            //先下後上
                                            Log.e("AAAAAA", "先下後上")
                                            b.startAnimation(dani)
                                            btx!!.startAnimation(uani)
                                            arenext=1
                                        }
                                    }
                                    if(xx==-1&&yy==0){
                                        if(removeYes==1){
                                            //先上後下
                                            Log.e("AAAAAA", "先上後下")
                                            b.startAnimation(uani2)
                                            btx!!.startAnimation(dani2)
                                            arenext = 1
                                        }else {
                                            //先上後下
                                            Log.e("AAAAAA", "先上後下")
                                            b.startAnimation(uani)
                                            btx!!.startAnimation(dani)
                                            arenext = 1
                                        }
                                    }

                                    val handler = Handler()
                                    handler.postDelayed(Runnable {
                                        if(arenext==1){

                                            if(removeYes == 1 && mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]!=-2){
                                                //代表本次交換有效 且 兩個其中一個沒消失
                                                //顏色維持交換 其中一個維持空白
                                                if(mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]==0){
                                                    btx!!.setBackgroundResource(R.drawable.bluecandy)
                                                }
                                                if(mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]==1){
                                                    btx!!.setBackgroundResource(R.drawable.greapcandy)
                                                }
                                                if(mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]==2){
                                                    btx!!.setBackgroundResource(R.drawable.greencandy)
                                                }
                                                if(mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]==3){
                                                    btx!!.setBackgroundResource(R.drawable.redcandy)
                                                }
                                                if(mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]==4){
                                                    btx!!.setBackgroundResource(R.drawable.yellowcandy)
                                                }
                                                if(mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]==7){
                                                    btx!!.setBackgroundResource(R.drawable.chocolate)
                                                }
                                            }else if (removeYes == 1 && saveColor!=-2){
                                                if(saveColor==0){
                                                    b!!.setBackgroundResource(R.drawable.bluecandy)
                                                }
                                                if(saveColor==1){
                                                    b!!.setBackgroundResource(R.drawable.greapcandy)
                                                }
                                                if(saveColor==2){
                                                    b!!.setBackgroundResource(R.drawable.greencandy)
                                                }
                                                if(saveColor==3){
                                                    b!!.setBackgroundResource(R.drawable.redcandy)
                                                }
                                                if(saveColor==4){
                                                    b!!.setBackgroundResource(R.drawable.yellowcandy)
                                                }
                                                if(saveColor==7){
                                                    b!!.setBackgroundResource(R.drawable.chocolate)
                                                }
                                            }
                                            else{
                                                //交換回來
                                                //什麼事都沒發生
                                                var reColor = mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])]
                                                mapColor[Integer.parseInt(splitfrist[0])][Integer.parseInt(splitfrist[1])] = mapColor[Integer.parseInt(splitsecond[0])][Integer.parseInt(splitsecond[1])]
                                                mapColor[Integer.parseInt(splitsecond[0])][Integer.parseInt(splitsecond[1])] = reColor
                                            }
                                            randomClear()
                                            btx = null
                                            firstLocation = ""
                                            secondLocation = ""
                                            arenext = 0
                                        }
                                    }, 10)
                                }
                            }
                        })
                        if(i==2) {
                            r01.addView(x)
                        }
                        if(i==3) {
                            r02.addView(x)
                        }
                        if(i==4) {
                            r03.addView(x)
                        }
                        if(i==5) {
                            r04.addView(x)
                        }
                        if(i==6) {
                            r05.addView(x)
                        }
                        if(i==7) {
                            r06.addView(x)
                        }
                        if(i==8) {
                            r07.addView(x)
                        }
                        if(i==9) {
                            r08.addView(x)
                        }
                    }else{
                        //外圍兩圈皆為空白
                    }
                }else{
                    //外圍兩圈皆為空白
                }
                btarr.add(x)
            }
        }
        for (i in 0..11) {
            for (j in 0..7){
                if(i>=2&&j>=2){
                    if(i<=9&&j<=5) {
//                        var metrics = DisplayMetrics()
//                        windowManager.defaultDisplay.getMetrics(metrics)
//                        width= metrics.widthPixels/9
//                        height = (metrics.heightPixels-300)/9
                        var params = btarr[j+(8*i)].getLayoutParams()
                        params.height=235
                        params.width=250
                    }
                }
            }
        }
        checkAll()
        randomClear()
    }

    private fun checkAll() {
        //比較數字/顏色
        //全圖判斷
        removeYes = 0
        for (n in 2..9) {
            for (m in 2..5) {
                if(mapColor[n][m]!=-2||mapColor[n][m]!=7){
                    if (mapColor[n][m] == mapColor[n][m+1] && mapColor[n][m] == mapColor[n][m+2] && mapColor[n][m] == mapColor[n+1][m+2] && mapColor[n][m] == mapColor[n+2][m+2]) {
                        //判斷五個連線
                        //***
                        //  *
                        //  *
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n][m+1] = -2
                        mapColor[n][m+2] = 7
                        mapColor[n+1][m+2] = -2
                        mapColor[n+2][m+2] = -2

                        removeYes = 1

                    }else if (mapColor[n][m] == mapColor[n + 1][m] && mapColor[n][m] == mapColor[n + 2][m] && mapColor[n][m] == mapColor[n][m+1] && mapColor[n][m] == mapColor[n][m+2]) {
                        //判斷五個連線
                        //***
                        //*
                        //*
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = 7
                        mapColor[n + 1][m] = -2
                        mapColor[n + 2][m] = -2
                        mapColor[n][m+1] = -2
                        mapColor[n][m+2] = -2

                        removeYes = 1
                        Log.e("cccccc", "五個連線")

                    }else if (mapColor[n][m] == mapColor[n][m+1] && mapColor[n][m] == mapColor[n][m+2] && mapColor[n][m] == mapColor[n+1][m+1] && mapColor[n][m] == mapColor[n+2][m+1]) {
                        //判斷五個連線
                        //***
                        // *
                        // *
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n + 1][m+1] = 7
                        mapColor[n + 2][m+1] = -2
                        mapColor[n][m+1] = -2
                        mapColor[n][m+2] = -2

                        removeYes = 1
                        Log.e("cccccc", "五個連線")

                    }else if (mapColor[n][m] == mapColor[n + 1][m] && mapColor[n][m] == mapColor[n + 2][m] && mapColor[n][m] == mapColor[n+2][m+1] && mapColor[n][m] == mapColor[n+2][m+2]) {
                        //判斷五個連線
                        //*
                        //*
                        //***
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n + 1][m] = -2
                        mapColor[n + 2][m] = 7
                        mapColor[n + 1][m+1] = -2
                        mapColor[n + 2][m+2] = -2

                        removeYes = 1
                        Log.e("cccccc", "五個連線")

                    }else if (mapColor[n][m] == mapColor[n+1][m] && mapColor[n][m] == mapColor[n+2][m] && mapColor[n][m] == mapColor[n+2][m-1] && mapColor[n][m] == mapColor[n+2][m-2]) {
                        //判斷五個連線
                        //  *
                        //  *
                        //***
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n + 1][m] = -2
                        mapColor[n + 2][m] = -2
                        mapColor[n + 2][m-1] = -2
                        mapColor[n + 2][m-2] = 7

                        removeYes = 1
                        Log.e("cccccc", "五個連線")

                    }else if (mapColor[n][m] == mapColor[n + 1][m] && mapColor[n][m] == mapColor[n + 2][m] && mapColor[n][m] == mapColor[n+2][m+1] && mapColor[n][m] == mapColor[n+2][m-1]) {
                        //判斷五個連線
                        // *
                        // *
                        //***
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n + 1][m] = -2
                        mapColor[n + 2][m] = 7
                        mapColor[n + 1][m+1] = -2
                        mapColor[n + 2][m-1] = -2

                        removeYes = 1
                        Log.e("cccccc", "五個連線")

                    } else if (mapColor[n][m] == mapColor[n + 1][m] && mapColor[n][m] == mapColor[n + 2][m] && mapColor[n][m] == mapColor[n + 3][m] && mapColor[n][m] == mapColor[n + 4][m] && mapColor[n + 3][m]!=-1 && mapColor[n + 4][m]!=-1) {
                        //判斷五個垂直
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n + 1][m] = -2
                        mapColor[n + 2][m] = -2
                        mapColor[n + 3][m] = -2
                        mapColor[n + 4][m] = 7

                        removeYes = 1
                        Log.e("cccccc", "五個垂直")

                    } else if (mapColor[n][m] == mapColor[n + 1][m] && mapColor[n][m] == mapColor[n + 2][m] && mapColor[n][m] == mapColor[n +3][m] && mapColor[n +3][m]!=-1) {
                        //判斷四個垂直連線 過+3要加判定 因為外圍只有兩圈
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n + 1][m] = -2
                        mapColor[n + 2][m] = -2
                        mapColor[n + 3][m] = 7

                        removeYes = 1
                        Log.e("cccccc", "4垂直連線")

                    } else if (mapColor[n][m] == mapColor[n][m + 1] && mapColor[n][m] == mapColor[n][m + 2] && mapColor[n][m] == mapColor[n][m +3] && mapColor[n][m +3]!=-1) {
                        //判斷四個水平連線
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())

                        mapColor[n][m] = -2
                        mapColor[n][m + 1] = -2
                        mapColor[n][m + 2] = -2
                        mapColor[n][m + 3] = 7

                        removeYes = 1
                        Log.e("cccccc", "4水平連線")
                    }
                    else if (mapColor[n][m] == mapColor[n + 1][m] && mapColor[n][m] == mapColor[n + 2][m]) {
                        //判斷三個垂直連線
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())
                        btarr[m + n*8].setBackgroundResource(R.drawable.clear)
                        btarr[m + (n+1)*8].setBackgroundResource(R.drawable.clear)
                        btarr[m + (n+2)*8].setBackgroundResource(R.drawable.clear)

                        mapColor[n][m] = -2
                        mapColor[n + 1][m] = -2
                        mapColor[n + 2][m] = -2

                        removeYes = 1
                        Log.e("cccccc", "3垂直連線")
                    }
                    else if (mapColor[n][m] == mapColor[n][m + 1] && mapColor[n][m] == mapColor[n][m + 2]) {
                        //判斷三個水平連線
                        Log.e("cccccc", n.toString()+"  "+m.toString())
                        Log.e("btarr[m + n*8].text", btarr[m + n*8].text.toString())
                        btarr[m + n*8].setBackgroundResource(R.drawable.clear)
                        btarr[m+1 + n*8].setBackgroundResource(R.drawable.clear)
                        btarr[m+2 + n*8].setBackgroundResource(R.drawable.clear)

                        mapColor[n][m] = -2
                        mapColor[n][m + 1] = -2
                        mapColor[n][m + 2] = -2

                        removeYes = 1
                        Log.e("cccccc", "3水平連線")
                    }
                }
            }
        }
    }

    private fun randomClear(){
        //將空白的 -2填入隨機數字及圖案
        for (n in 2..9) {
            for (m in 2..5) {
                if(mapColor[n][m]==-2){
                    val random = Random()
                    var newNumber = random.nextInt(5)

                    mapColor[n][m] = newNumber
                    if(newNumber==0){
                        btarr[m + n*8].setBackgroundResource(R.drawable.bluecandy)
                    }
                    if(newNumber==1){
                        btarr[m + n*8].setBackgroundResource(R.drawable.greapcandy)
                    }
                    if(newNumber==2){
                        btarr[m + n*8].setBackgroundResource(R.drawable.greencandy)
                    }
                    if(newNumber==3){
                        btarr[m + n*8].setBackgroundResource(R.drawable.redcandy)
                    }
                    if(newNumber==4){
                        btarr[m + n*8].setBackgroundResource(R.drawable.yellowcandy)
                    }
                }
                else if(mapColor[n][m]==7){
                    btarr[m + n*8].setBackgroundResource(R.drawable.chocolate)
                }
            }
        }
    }
}