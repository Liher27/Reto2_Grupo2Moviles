package com.example.reto2_grupo2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.databinding.ProfessorMainFrameBinding
import com.example.reto2_grupo2.databinding.StudentMainFrameBinding
import com.example.reto2_grupo2.ui.DocumentsDownloadFragment
import com.example.reto2_grupo2.ui.ExternalCoursesFragment
import com.example.reto2_grupo2.ui.ProfessorMainFragment
import com.example.reto2_grupo2.ui.ProfileFragment
import com.example.reto2_grupo2.ui.Reunions_fragment
import com.example.reto2_grupo2.ui.StudentMainFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFrame : AppCompatActivity() {

    private lateinit var mainFrameBinding: ProfessorMainFrameBinding
    private lateinit var studentFrame: StudentMainFrameBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainFrameBinding = ProfessorMainFrameBinding.inflate(layoutInflater)
        studentFrame = StudentMainFrameBinding.inflate(layoutInflater)

        setContentView(studentFrame.root)
        bottomNavigationView = studentFrame.bottomNavigationView


        bottomNavigationView.setOnItemSelectedListener { item ->
            handleBottomNavigation(item.itemId)
            true
        }
    }

    private fun handleBottomNavigation(itemId: Int?) {
        when (itemId) {
            R.id.home -> replaceFragment(StudentMainFragment())
            R.id.reunions -> replaceFragment(Reunions_fragment())
            R.id.user -> replaceFragment(ProfileFragment())
            R.id.documents_download -> replaceFragment(DocumentsDownloadFragment())
            R.id.external_courses -> replaceFragment(ExternalCoursesFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fM = supportFragmentManager
        val fT = fM.beginTransaction()
        fT.replace(R.id.frameLayout, fragment)
        fT.commit()
    }
}