package com.example.esg_donation

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.esg_donation.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth



        // 읽기 권한을 요청한다.
        providePermissions()


        // 회원가입 화면으로 넘어간다.
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, UserSignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener {
            if(binding.editTextTextForId.text.toString() != "" && binding.editTextForPassword.text.toString() != ""){
                auth.signInWithEmailAndPassword(binding.editTextTextForId.text.toString(), binding.editTextForPassword.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                       //     val user = auth.currentUser
                            //updateUI(user)

                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "아이디 또는 비밀번호가 일치하지 않습니다.",
                                Toast.LENGTH_SHORT).show()
                            //updateUI(null)
                        }
                    }
        }


    }
//    private fun updateUI(user: FirebaseUser?) {
//        //hideProgressBar()
//        if (user != null) {
//            binding.status.text = getString(R.string.emailpassword_status_fmt,
//                user.email, user.isEmailVerified)
//            binding.detail.text = getString(R.string.firebase_status_fmt, user.uid)
//
//            binding.emailPasswordButtons.visibility = View.GONE
//            binding.emailPasswordFields.visibility = View.GONE
//            binding.signedInButtons.visibility = View.VISIBLE
//
//            if (user.isEmailVerified) {
//                binding.verifyEmailButton.visibility = View.GONE
//            } else {
//                binding.verifyEmailButton.visibility = View.VISIBLE
//            }
//        } else {
//            binding.status.setText(R.string.signed_out)
//            binding.detail.text = null
//
//            binding.emailPasswordButtons.visibility = View.VISIBLE
//            binding.emailPasswordFields.visibility = View.VISIBLE
//            binding.signedInButtons.visibility = View.GONE
//        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    fun providePermissions(): Array<String> {
        return arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView: View? = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


}