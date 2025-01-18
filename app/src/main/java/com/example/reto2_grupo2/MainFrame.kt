package com.example.reto2_grupo2

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.reto2_grupo2.databinding.ProfessorMainFrameBinding
import com.example.reto2_grupo2.databinding.StudentMainFrameBinding
import com.example.reto2_grupo2.ui.ProfessorMainFragment
import com.example.reto2_grupo2.ui.ProfileFragment
import com.example.reto2_grupo2.ui.Reunions_fragment

class MainFrame : AppCompatActivity() {

    private lateinit var mainFrameBinding: ProfessorMainFrameBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var studentFrame: StudentMainFrameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainFrameBinding = ProfessorMainFrameBinding.inflate(layoutInflater)
        studentFrame = StudentMainFrameBinding.inflate(layoutInflater)
        //Aqui deberia de ponerse el panel inferior del tipo de usuario, pasado a traves del intent.putExtra()
        setContentView(mainFrameBinding.root)

        mainFrameBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.home -> {
                    replaceFragment(ProfessorMainFragment())
                    true
                }

                R.id.reunions -> {
                    replaceFragment(Reunions_fragment())
                    true
                }

                R.id.user -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> {
                    false
                }
            }

        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fM = supportFragmentManager
        val fT = fM.beginTransaction()
        fT.replace(R.id.frameLayout, fragment)
        fT.commit()

    }

}