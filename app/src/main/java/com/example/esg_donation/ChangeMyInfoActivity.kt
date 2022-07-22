package com.example.esg_donation

import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.esg_donation.databinding.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class ChangeMyInfoActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityChangeMyinfoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangeMyinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    // 이 액티비티를 종료하는 함수. 프래그먼트에서 사용하기 위함.
    fun finishActivity(){
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView3)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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

class EditMyInfoHomeFragment : Fragment(R.layout.edit_my_info_fragment){
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    lateinit var binding: EditMyInfoFragmentBinding
    private var gender: String? = ""
    private val viewModel:ViewModelForEditMyInfo by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditMyInfoFragmentBinding.bind(view)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.change_my_info_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.change_button -> {

                        if(binding.editTextTextEmail.visibility == View.INVISIBLE){
                            Toast.makeText(
                                context, "이메일 형식이 올바르지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }
                        // viewModel에 변경할 정보들을 저장한다.
                        viewModel.setUserInfoForChange(binding.nameEditText.text.toString(),
                            binding.editTextTextEmail.text.toString(), gender)
                        viewModel.setMode(false)

                        // 비밀번호 인증을 위해 다음 화면으로 넘어간다.
                        findNavController().navigate(R.id.action_editMyInfoHomeFragment_to_checkPassWordFragment)

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.loadingLayout2.visibility = View.VISIBLE
        binding.scrollview.visibility = View.GONE

        db.collection("User")
            .whereEqualTo("email", currentUser?.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.nameEditText.setText(document.get("name").toString())
                    binding.editTextTextEmail.setText(document.get("email").toString())
                    val g = document.get("gender").toString()
                    if(g == "여자"){
                        gender = "여자"
                        binding.radioButton2.isChecked = true
                    }else if(g == "남자"){
                        gender = "남자"
                        binding.radioButton.isChecked = true
                    }else{
                        gender = "null"
                        binding.radioButton3.isChecked = true
                    }
                }
                binding.loadingLayout2.visibility = View.GONE
                binding.scrollview.visibility = View.VISIBLE

            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "정보를 가져오는데 실패했습니다. ",
                    Toast.LENGTH_SHORT).show()
                (activity as ChangeMyInfoActivity).finishActivity()
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


        // 프로필 사진 변경하는 버튼 눌렀을 때
        binding.imageButton.setOnClickListener {

        }

        binding.editTextTextEmail.doOnTextChanged { text, start, before, count ->
            val pattern = Patterns.EMAIL_ADDRESS
            if(pattern.matcher(text.toString()).matches()){
                // 이메일이 맞다면
                binding.emailWarningTextView.visibility = View.INVISIBLE
            }else{
                // 이메일 형식이 아니라면
                binding.emailWarningTextView.visibility = View.VISIBLE
            }
        }

        // 비밀번호 변경하기 위해 버튼 눌렀을 때
        binding.buttonForChangingPw.setOnClickListener {

            viewModel.setMode(true)
            // 비밀번호 변경을 위해 다음 화면으로 넘어간다.
            findNavController().navigate(R.id.action_editMyInfoHomeFragment_to_checkPassWordFragment)

        }

        // textView에 underline 생성하기
        val underlinStrLogOut = "로그아웃"
        val mSpannableString1 = SpannableString(underlinStrLogOut)
        mSpannableString1.setSpan(UnderlineSpan(), 0, mSpannableString1.length, 0)
        binding.logOutTextView.text= mSpannableString1

        val underlinStrSignOut = "회원탈퇴"
        val mSpannableString2 = SpannableString(underlinStrSignOut)
        mSpannableString2.setSpan(UnderlineSpan(), 0, mSpannableString2.length, 0)
        binding.signOutTextView.text= mSpannableString2


        binding.logOutTextView.setOnClickListener {
            Log.i(TAG,"로그아웃 버튼 눌림")
            auth.signOut()
            (activity as ChangeMyInfoActivity).finishActivity()
        }
        binding.signOutTextView.setOnClickListener {
            Log.i(TAG,"회원탈퇴 버튼 눌림")


        }
    }



}

class CheckPassWordFragment: Fragment(R.layout.edit_password_fragment){
    lateinit var binding: EditPasswordFragmentBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val viewModel:ViewModelForEditMyInfo by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = EditPasswordFragmentBinding.bind(view)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        // 변경 전 이메일을 저장한다.
        val preEamil = currentUser!!.email

        // '확인' 버튼이 눌렸을 때
        binding.button3.setOnClickListener {
            // 회원정보를 변경하기 위해 왔을 때
            if(viewModel.isForNewPw.value == false){
                val credential = EmailAuthProvider
                    .getCredential(currentUser.email.toString(), binding.editTextTextForCurrentPassword.text.toString())

                // 사용자 재 인증을 한다. (로그인한지 오래되면 이메일 변경, 회원 탈퇴 등의 기능이 동작하지 않을수도 있음)
                currentUser.reauthenticate(credential)
                    .addOnSuccessListener {
                        // 변경사항을 저장한다
                        currentUser!!.updateEmail(viewModel.email.value.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User email address updated.")

                                    val userInfo = hashMapOf(
                                        "name" to viewModel.name.value.toString(),
                                        "email" to viewModel.email.value.toString(),
                                        "gender" to viewModel.gender.value
                                    )

                                    // 새로 user 정보를 추가한다.
                                    val ref = db.collection("User").document(viewModel.email.value.toString())
                                    ref.set(userInfo).addOnSuccessListener { documentReference ->
                                        // 이메일이 변경되었다면 기존의 user를 삭제한다. (firestore에서)
                                        if (preEamil != null && currentUser.email != preEamil) {
                                            db.collection("User").document(preEamil)
                                                .delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context, "회원정보가 수정되었습니다.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    findNavController().navigate(R.id.action_checkPassWordFragment_to_editMyInfoHomeFragment)

                                                }
                                                .addOnFailureListener { e ->
                                                    // 프로그레스 바 로딩을 끝내기

                                                    Toast.makeText(
                                                        context, "회원정보 수정에 실패하였습니다.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }else{
                                            Toast.makeText(
                                                context, "회원정보가 수정되었습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            findNavController().navigate(R.id.action_checkPassWordFragment_to_editMyInfoHomeFragment)

                                        }

                                    }
                                        .addOnFailureListener { e ->
                                            // 프로그레스 바 로딩을 끝내기
                                            Toast.makeText(
                                                context, "회원정보 수정에 실패하였습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }

                                }
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context, "사용자 인증에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            // 비밀번호를 변경하기 위해 왔을 때
            else{
                val credential = EmailAuthProvider
                    .getCredential(currentUser.email.toString(), binding.editTextTextForCurrentPassword.text.toString())

                // 사용자 재 인증을 한다. (로그인한지 오래되면 이메일 변경, 회원 탈퇴 등의 기능이 동작하지 않을수도 있음)
                currentUser.reauthenticate(credential)
                    .addOnSuccessListener {
                        // 변경할 비밀번호를 입력할 화면으로 이동한다.
                        findNavController().navigate(R.id.action_checkPassWordFragment_to_editPassWordFragment)
                    }.addOnFailureListener {
                        Toast.makeText(
                            context, "비밀번호가 일치하지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }

        }



    }
}

class EditPassWordFragment: Fragment(R.layout.edit_password_fragment2){
    lateinit var binding: EditPasswordFragment2Binding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = EditPasswordFragment2Binding.bind(view)
        auth = Firebase.auth
        val currentUser = auth.currentUser


        binding.button4.setOnClickListener {
            if(binding.editTextTextForNewPassword.text.toString() != "" && binding.editTextTextForPwCheck.text.toString() != ""){
                if(binding.textViewForPwNotice2.visibility == View.INVISIBLE){
                    if(checkPassword(binding.editTextTextForNewPassword.text.toString())){
                        // 비밀번호를 변경한다.
                        currentUser!!.updatePassword(binding.editTextTextForNewPassword.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context, "비밀번호가 변경되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // 다시 회원정보 변경 화면으로 이동한다.
                                    findNavController().navigate(R.id.action_editPassWordFragment_to_editMyInfoHomeFragment)
                                }
                            }.addOnFailureListener {
                                Toast.makeText(
                                    context, "비밀번호 변경에 실패하였습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }else{
                        Toast.makeText(
                            context, "비밀번호를 조건에 맞게 만들어주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }else{
                    Toast.makeText(
                        context, "비밀번호 재입력이 올바르지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(
                    context, "비밀번호를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }




        // 비밀번호가 일치하는지 검사한다.
        binding.editTextTextForNewPassword.doOnTextChanged { text, start, before, count ->
            if(text.toString() == binding.editTextTextForPwCheck.text.toString()){
                binding.textViewForPwNotice2.visibility = View.INVISIBLE
            }else{
                binding.textViewForPwNotice2.visibility = View.VISIBLE
            }
        }
        binding.editTextTextForPwCheck.doOnTextChanged { text, start, before, count ->
            if(text.toString() == binding.editTextTextForNewPassword.text.toString()){
                binding.textViewForPwNotice2.visibility = View.INVISIBLE
            }else{
                binding.textViewForPwNotice2.visibility = View.VISIBLE
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


