package com.example.esg_donation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.esg_donation.databinding.ActivityShowOcrInfoBinding
import com.example.esg_donation.databinding.SignUp1Binding

class ShowOcrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowOcrInfoBinding
    private lateinit var documentUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowOcrInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 선택한 파일의 uri를 가져온다.
        documentUri = Uri.parse(intent.getStringExtra("uri"))
        Log.i("uri: ", documentUri.toString())

    }
}