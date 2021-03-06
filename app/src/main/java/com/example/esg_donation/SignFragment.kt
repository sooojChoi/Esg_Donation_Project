package com.example.esg_donation

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.esg_donation.databinding.ActivityDonatorLoginBinding
import com.example.esg_donation.databinding.ActivityDonatorSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class SignInFragment : Fragment(R.layout.activity_donator_login){
    lateinit var binding: ActivityDonatorLoginBinding
    private lateinit var auth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityDonatorLoginBinding.bind(view)

        auth = Firebase.auth

        // 회원가입 화면으로 넘어간다.
        binding.btnSignup.setOnClickListener {
//            val intent = Intent(this, UserSignUpActivity::class.java)
//            startActivity(intent)
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        // 로그인 버튼 누름
        binding.btnSignin.setOnClickListener {
            if (binding.editTextTextForId.text.toString() != "" && binding.editTextForPassword.text.toString() != "") {
                binding.progressBar2.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(
                    binding.editTextTextForId.text.toString(),
                    binding.editTextForPassword.text.toString()
                )
                    .addOnCompleteListener(activity as LogInActivity) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            //     val user = auth.currentUser
                            //updateUI(user)

//                            val intent = Intent(this, HomeActivity::class.java)
//                            startActivity(intent)
                            (activity as LogInActivity).finishActivity()
                        } else {
                            // If sign in fails, display a message to the user.
                            binding.progressBar2.visibility = View.GONE
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                activity, "아이디 또는 비밀번호가 일치하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            //updateUI(null)
                        }
                    }
            } else {
                binding.progressBar2.visibility = View.GONE
                Toast.makeText(
                    activity, "아이디와 비밀번호를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT

                ).show()
            }

        }
    }

}

class SignUpFragment : Fragment(R.layout.activity_donator_signup){
    lateinit var binding: ActivityDonatorSignupBinding
    private lateinit var auth: FirebaseAuth
    var gender:String? = "남자"
    val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityDonatorSignupBinding.bind(view)

        // Initialize Firebase Auth
        auth = Firebase.auth


        // 이메일 형식을 올바르게 입력했는지 검사한다.
        binding.editTextTextEmailAddress.doOnTextChanged { text, start, before, count ->
            val pattern = Patterns.EMAIL_ADDRESS
            if(pattern.matcher(text.toString()).matches()){
                // 이메일이 맞다면
                binding.emailNoticeTextView.visibility = View.INVISIBLE
            }else{
                // 이메일 형식이 아니라면
                binding.emailNoticeTextView.visibility = View.VISIBLE
            }
        }


        // 비밀번호가 일치하는지 검사한다.
        binding.editTextForCheckPassword.doOnTextChanged { text, start, before, count ->
            if(text.toString() == binding.editTextForPassword2.text.toString()){
                binding.passwordCheckTextView.text = ""
                binding.passwordCheckTextView.visibility = View.GONE
            }else{
                binding.passwordCheckTextView.text = "비밀번호가 일치하지 않습니다."
                binding.passwordCheckTextView.visibility = View.VISIBLE
            }
        }
        binding.editTextForPassword2.doOnTextChanged { text, start, before, count ->
            if(text.toString() == binding.editTextForCheckPassword.text.toString()){
                binding.passwordCheckTextView.text = ""
                binding.passwordCheckTextView.visibility = View.GONE
            }else{
                binding.passwordCheckTextView.text = "비밀번호가 일치하지 않습니다."
                binding.passwordCheckTextView.visibility = View.VISIBLE
            }
        }





        // 회원가입 버튼 눌렸을 때
        binding.btnCompleteSignUp.setOnClickListener {
            // 모든 정보를 빠짐없이 입력하고 비밀번호 입력이 일치하는지 확인
            if(binding.editTextTextEmailAddress.text.toString() != "" && binding.editTextTextPersonName.text.toString() != "" &&
                binding.editTextForPassword2.text.toString() != "" && binding.passwordCheckTextView.text.toString() == "")
            {
                // 비밀번호 조건에 맞추어 비밀번호를 입력했는지 & 이메일 형식이 올바른지 확인 후 회원가입과 로그인 진행
                if(checkPassword(binding.editTextForPassword2.text.toString()) && binding.passwordCheckTextView.visibility == View.GONE){
                    binding.progressBar3.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(binding.editTextTextEmailAddress.text.toString(), binding.editTextForPassword2.text.toString())
                        .addOnCompleteListener(activity as LogInActivity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")

                                val userInfo = hashMapOf(
                                    "name" to binding.editTextTextPersonName.text.toString(),
                                    "email" to binding.editTextTextEmailAddress.text.toString(),
                                    "gender" to gender
                                )
                                db.collection("User").document(binding.editTextTextEmailAddress.text.toString())
                                    .set(userInfo)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully written!")
                                        // 회원가입이 되면 자동으로 로그인이 됨.
                                        auth.signInWithEmailAndPassword(binding.editTextTextEmailAddress.text.toString(), binding.editTextForPassword2.text.toString())
                                            .addOnCompleteListener(activity as LogInActivity) { task2 ->
                                                if (task2.isSuccessful) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d(TAG, "signInWithEmail:success")
                                                    //     val user = auth.currentUser
                                                    //updateUI(user)

//                                            val intent = Intent(activity, HomeActivity::class.java)
//                                            startActivity(intent)
                                                    (activity as LogInActivity).finishActivity()
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w(TAG, "signInWithEmail:failure", task2.exception)
                                                    binding.progressBar3.visibility = View.GONE
                                                    Toast.makeText(context, "로그인에 실패하였습니다.",
                                                        Toast.LENGTH_SHORT).show()
                                                    //updateUI(null)
                                                }
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error writing document", e)
                                        binding.progressBar3.visibility = View.GONE
                                        Toast.makeText(context, "회원가입에 실패하였습니다.",
                                            Toast.LENGTH_SHORT).show()
                                        Log.w(TAG, "회원가입에 실패하였습니다.", e)
                                    }

//                                db.collection("User")
//                                    .add(userInfo)
//                                    .addOnSuccessListener { documentReference ->
//
//                                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//
//                                    }
//                                    .addOnFailureListener { e ->
//
//                                    }


                            } else {
                                // If sign in fails, display a message to the user.
                                binding.progressBar3.visibility = View.GONE
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(context, "회원가입에 실패하였습니다.",
                                    Toast.LENGTH_SHORT).show()
                                //    updateUI(null)
                            }
                        }
                }else{

                }
            }else{
                binding.progressBar3.visibility = View.GONE
                Toast.makeText(context, "모든 정보를 정확히 입력해주세요.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // 성별이 체크될 때
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            if(i == R.id.radioButton){
                binding.radioButton.setTextColor(Color.parseColor("#FFFFFF"))
                binding.radioButton2.setTextColor(Color.parseColor("#464646"))
                binding.radioButton3.setTextColor(Color.parseColor("#464646"))
                Log.i(TAG, "남자")
                gender = "남자"
            }else if(i == R.id.radioButton2){
                binding.radioButton.setTextColor(Color.parseColor("#464646"))
                binding.radioButton2.setTextColor(Color.parseColor("#FFFFFF"))
                binding.radioButton3.setTextColor(Color.parseColor("#464646"))
                Log.i(TAG, "여자")
                gender = "여자"
            }else if(i == R.id.radioButton3){
                binding.radioButton.setTextColor(Color.parseColor("#464646"))
                binding.radioButton2.setTextColor(Color.parseColor("#464646"))
                binding.radioButton3.setTextColor(Color.parseColor("#FFFFFF"))
                Log.i(TAG, "선택안함")
                gender = null
            }
        }



    }



    // 비밀번호가 조건에 맞는지 검사해주는 함수
    private fun checkPassword(pw: String): Boolean {
        // 문자열 길이 검사
        if(pw.length < 8 || pw.length > 16){
            Toast.makeText(context, "비밀번호 길이를 8~16문자로 만들어주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        // 문자열에 영문자와 숫자, 특수기호가 모두 포함되어있는지 검사
        if(!(isContainEng(pw) && isContainNumber(pw) && isContainSpecial(pw))){
            Toast.makeText(context, "비밀번호에 영문자와 숫자, 특수기호가 모두 포함되어야합니다.", Toast.LENGTH_SHORT).show()
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

}


