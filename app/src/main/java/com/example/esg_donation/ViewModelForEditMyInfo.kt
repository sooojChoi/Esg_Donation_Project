package com.example.esg_donation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelForEditMyInfo : ViewModel() {
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val gender = MutableLiveData<String?>()
    val isForNewPw = MutableLiveData<Boolean>()

    fun setUserInfoForChange(name: String, email: String, gender: String?){
        this.name.value = name
        this.email.value = email
        this.gender.value = gender
    }

    // 비밀번호 변경을 위한 이동이면 true 가 저장, 회원 정보 변경을 위한 이동이면 false 가 저장되어있음
    fun setMode(mode:Boolean){
        isForNewPw.value = mode
    }
}