package com.example.esg_donation

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.example.esg_donation.databinding.FragmentMypageBinding
import com.example.esg_donation.databinding.HomeFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.home_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = HomeFragmentBinding.bind(view)
        binding.textView3.text = "Home Fragment"
    }
}

class Fragment2 : Fragment(R.layout.home_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = HomeFragmentBinding.bind(view)
        binding.textView3.text = "Fragment2"
    }
}

class Fragment3 : Fragment(R.layout.fragment_mypage){
    private lateinit var auth: FirebaseAuth
    lateinit var binding: FragmentMypageBinding
    val db = Firebase.firestore


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMypageBinding.bind(view)

        // Initialize Firebase Auth
        auth = Firebase.auth


        binding.signButton.setOnClickListener {
            val intent = Intent(activity, LogInActivity::class.java)
            startActivity(intent)
        }

        binding.nameLayout.setOnClickListener {
            val intent = Intent(activity, ChangeMyInfoActivity::class.java)
            startActivity(intent)
        }

        binding.loadingLayout.visibility = View.VISIBLE
        binding.bodyLayout.visibility = View.GONE
        binding.nameLayout.visibility = View.GONE
        binding.signLayout.visibility = View.GONE
        binding.worldImageViewLayout.visibility = View.GONE
        binding.loginNoticeLayout.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            // 로그인되어 있다면


//            val datathread: Thread = object : Thread() {
//                override fun run() {
//
//                }
//            }
//            datathread.start()
//            try {
//                datathread.join()
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }

            db.collection("User")
                .whereEqualTo("email", currentUser.email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        binding.nameTextView.text = document.get("name").toString()
                    }
                    binding.loginNoticeLayout.visibility = View.GONE
                    binding.noticeImageView.visibility = View.GONE
                    binding.nameLayout.visibility = View.VISIBLE
                    binding.signLayout.visibility = View.GONE
                    binding.worldImageViewLayout.visibility = View.GONE
                    binding.bodyLayout.visibility = View.VISIBLE
                    binding.loadingLayout.visibility = View.GONE
                }
                .addOnFailureListener { exception ->
                    binding.nameTextView.text = "오류가 발생했습니다. 다시 로그인해주세요."
                }
        }else{
            binding.loginNoticeLayout.visibility = View.VISIBLE
            binding.noticeImageView.visibility = View.VISIBLE
            binding.nameLayout.visibility = View.GONE
            binding.signLayout.visibility = View.VISIBLE
            binding.worldImageViewLayout.visibility = View.VISIBLE
        }
    }

}