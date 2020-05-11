package com.user.ktcandycrash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    lateinit var firebaseUser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


// Choose authentication providers
        val providers = arrayListOf(
            //AuthUI.IdpConfig.EmailBuilder().build(),
            //AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
            //AuthUI.IdpConfig.TwitterBuilder().build()
        )

//設置「身份驗證監聽器（FirebaseAuth.AuthStateListener）」，讓Activity啟動後隨時偵測使用者是否已登入，並做對應的處理。
        val authListener: FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
                val user: FirebaseUser? = auth.currentUser
                if (user == null) {
                    //需登入
                    val intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setAlwaysShowSignInMethodScreen(true)
                        .setIsSmartLockEnabled(false)
                        .build()
                    startActivityForResult(intent, RC_SIGN_IN)
                } else {
                    //已經登入
                    this.firebaseUser = user
                    displayInfo()
                }
            }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)

/*
// Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                /*.setTosAndPrivacyPolicyUrls(
                    "https://www.google.com/服務條款",
                    "https://www.youtube.com/隱私權政策"
                )*/
                .build(),
            RC_SIGN_IN)*/
    }

    //一旦使用者離開登入畫面（可能登入成功、失敗或取消登入）回到MainActivity，首先Android系統會自動呼叫onActivityResult方法。
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    firebaseUser = user
                }
                displayInfo()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Toast.makeText(applicationContext, response?.error?.errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    //退出 Firebase 身份驗證以及所有社交身份提供方帳號
    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "已登出", Toast.LENGTH_SHORT).show()
                /*val intent = Intent(this, MainActivity::class.java)
                startActivityForResult(intent, 0)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                Process.killProcess(Process.myPid())
                System.exit(0)*/
            }
        // [END auth_fui_signout]
    }

    //刪除 Firebase 身份验证以及所有社交身份提供方帐号
    private fun signDelete() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "已刪除", Toast.LENGTH_SHORT).show()/*
                val intent = Intent(this, MainActivity::class.java)
                startActivityForResult(intent, 0)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                Process.killProcess(Process.myPid())
                System.exit(0)*/
            }
    }

    private fun startGame() {
        Log.e("0","11111")
        val bundle = Bundle()
        bundle.putString("uid", firebaseUser.getUid())
        val intent = Intent(this,PlayGameActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        Process.killProcess(Process.myPid())
        System.exit(0)
    }

    fun click(v: View?) {
        when (v?.id) {
            R.id.btLoginOut ->
                signOut()
            R.id.btLoginDelete ->
                signDelete()
            R.id.btstart ->
                startGame()
        }
    }

    //顯示使用者姓名
    private fun displayInfo() {
      /*  val txtUserName = findViewById<TextView>(R.id.txtUserName)
        txtUserName.text = "歡迎您~"+firebaseUser?.displayName+"\n"+"UID為:"+firebaseUser.getUid();*/
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
