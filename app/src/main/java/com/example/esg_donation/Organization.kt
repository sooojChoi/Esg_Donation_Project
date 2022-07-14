package com.example.esg_donation

import android.net.Uri

class Organization(val business_name:String,
                   val representative:String, val business_number: String) {
   // lateinit var business_name:String   // 법인명
  //  lateinit var representative:String  // 대표자
  //  lateinit var business_number: String // 사업자 등록 번호
    var showed_name:String = ""  // 단체 활동명
    var profile_image: Uri =  Uri.EMPTY  // 프로필 사진 주소
    var short_introduce:String = ""  // 짧은 소개글
    var long_introduce: String = ""  // 긴 소개글
    var homepage_link: String = ""   // 관련 링크





}