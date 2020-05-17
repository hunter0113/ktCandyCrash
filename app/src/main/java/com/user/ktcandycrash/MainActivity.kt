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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var firebaseUser : FirebaseUser

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationInProgress = false
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private val TAG = "AAAAAAAA"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Choose authentication providers
        val providers = arrayListOf(
            //AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
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

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // ...
            }
        }
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
        val txtUserName = findViewById<TextView>(R.id.txtUserName)
        txtUserName.text = "歡迎您~"+firebaseUser?.displayName+"\n"+"UID為:"+firebaseUser.getUid();
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        verificationInProgress = true
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }
}
