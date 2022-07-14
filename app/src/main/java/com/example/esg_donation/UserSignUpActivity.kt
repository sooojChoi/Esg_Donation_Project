package com.example.esg_donation

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.esg_donation.databinding.ActivityDonatorSignupBinding
import com.example.esg_donation.databinding.ActivityMainBinding
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class UserSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonatorSignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDonatorSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // 비밀번호가 일치하는지 검사한다.
        binding.editTextForCheckPassword.doOnTextChanged { text, start, before, count ->
            if(text.toString() == binding.editTextForPassword2.text.toString()){
                binding.passwordCheckTextView.text = ""
            }else{
                binding.passwordCheckTextView.text = "비밀번호가 일치하지 않습니다."
            }
        }
        binding.editTextForPassword2.doOnTextChanged { text, start, before, count ->
            if(text.toString() == binding.editTextForCheckPassword.text.toString()){
                binding.passwordCheckTextView.text = ""
            }else{
                binding.passwordCheckTextView.text = "비밀번호가 일치하지 않습니다."
            }
        }


        // 회원가입 버튼 눌렸을 때
        binding.btnCompleteSignUp.setOnClickListener {
            // 모든 정보를 빠짐없이 입력하고 비밀번호 입력이 일치하는지 확인
            if(binding.editTextTextEmailAddress.text.toString() != "" && binding.editTextTextPersonName.text.toString() != "" &&
                    binding.editTextForPassword2.text.toString() != "" && binding.passwordCheckTextView.text.toString() == "")
                    {
                        // 비밀번호 조건에 맞추어 비밀번호를 입력했는지 확인후 회원가입과 로그인 진행
                        if(checkPassword(binding.editTextForPassword2.text.toString())){
                            auth.createUserWithEmailAndPassword(binding.editTextTextEmailAddress.text.toString(), binding.editTextForPassword2.text.toString())
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success")

                                        // 회원가입이 되면 자동으로 로그인이 됨.
                                        auth.signInWithEmailAndPassword(binding.editTextTextEmailAddress.text.toString(), binding.editTextForPassword2.text.toString())
                                            .addOnCompleteListener(this) { task2 ->
                                                if (task2.isSuccessful) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d(TAG, "signInWithEmail:success")
                                                    //     val user = auth.currentUser
                                                    //updateUI(user)

                                                    val intent = Intent(this, HomeActivity::class.java)
                                                    startActivity(intent)
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w(TAG, "signInWithEmail:failure", task2.exception)
                                                    Toast.makeText(baseContext, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show()
                                                    //updateUI(null)
                                                }
                                            }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                        Toast.makeText(baseContext, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show()
                                        //    updateUI(null)
                                    }
                                }
                        }else{

                        }
            }else{
                Toast.makeText(this, "모든 정보를 정확히 입력해주세요.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // 뒤로가기 버튼을 누르면 액티비티를 종료한다.
        this.binding.backButton.setOnClickListener {
            this.finish()
        }
    }

    // 비밀번호가 조건에 맞는지 검사해주는 함수
    private fun checkPassword(pw: String): Boolean {
        // 문자열 길이 검사
        if(pw.length < 8 || pw.length > 16){
            Toast.makeText(this, "비밀번호 길이를 8~16문자로 만들어주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        // 문자열에 영문자와 숫자, 특수기호가 모두 포함되어있는지 검사
        if(!(isContainEng(pw) && isContainNumber(pw) && isContainSpecial(pw))){
            Toast.makeText(this, "비밀번호에 영문자와 숫자, 특수기호가 모두 포함되어야합니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    //문자열에 특수기호가 있는지 검사
    private fun isContainSpecial(str: String): Boolean{
        val pattern: Pattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]")
        for(c in str){
            if(pattern.matcher(c.toString()).find()){
                return true
            }
        }
        //Toast.makeText(this, "비밀번호에 특수기호를 포함시켜주세요.", Toast.LENGTH_SHORT).show()
        return false
    }

    // 문자열에 영문자가 포함되어있는지 검사
    private fun isContainEng(str:String): Boolean{
        for(c in str){
            if(c in 'A'..'Z' || c in 'a'..'z'){
                return true
            }
        }
    //    Toast.makeText(this, "비밀번호에 영문자를 포함시켜주세요.", Toast.LENGTH_SHORT).show()
        return false
    }
    //문자열에 숫자가 포함되어있는지 검사
    private fun isContainNumber(str:String): Boolean{
        for(c in str){
            if(c in '0'..'9'){
                return true
            }
        }
      //  Toast.makeText(this, "비밀번호에 숫자를 포함시켜주세요.", Toast.LENGTH_SHORT).show()
        return false
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
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}