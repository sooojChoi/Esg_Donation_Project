package com.example.esg_donation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.example.esg_donation.databinding.FragmentMypageBinding
import com.example.esg_donation.databinding.HomeFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMypageBinding.bind(view)

        // Initialize Firebase Auth
        auth = Firebase.auth


        binding.signButton.setOnClickListener {
            val intent = Intent(activity, LogInActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            // 로그인되어 있다면
            binding.loginNoticeLayout.visibility = View.GONE
            binding.noticeImageView.visibility = View.GONE
            binding.nameTextView.visibility = View.VISIBLE
            binding.profileImageView.visibility = View.VISIBLE
            binding.signButton.visibility = View.GONE
        }else{
            binding.loginNoticeLayout.visibility = View.VISIBLE
            binding.noticeImageView.visibility = View.VISIBLE
            binding.nameTextView.visibility = View.GONE
            binding.profileImageView.visibility = View.GONE
            binding.signButton.visibility = View.VISIBLE
        }
    }
}