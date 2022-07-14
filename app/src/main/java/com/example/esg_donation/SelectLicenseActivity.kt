package com.example.esg_donation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.esg_donation.databinding.SignUp1Binding

class SelectLicenseActivity : AppCompatActivity() {
    private lateinit var binding: SignUp1Binding
    private lateinit var chooseFile: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignUp1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        // 이미지가 선택되면 해당 이미지의 uri를 넘겨주거나, 거기서 얻은 객체 정보를 넘겨주거나? (사업자 등록증에서 얻을 객체 클래스를 생성해서..)
        chooseFile = registerForActivityResult(ActivityResultContracts.GetContent()) {
            Log.i("", "file is selected. ${it}")
            val intent = Intent(this, ShowOcrActivity::class.java)
            intent.putExtra("uri",it.toString())

            startActivity(intent)
        }

        // 파일에서 이미지를 선택한다.
        binding.buttonForSelectImage.setOnClickListener {
            chooseFile.launch("image/Pictures/*")
        }

    }

}