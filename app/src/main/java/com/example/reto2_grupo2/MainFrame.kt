package com.example.reto2_grupo2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.databinding.ProfessorMainFrameBinding
import com.example.reto2_grupo2.databinding.StudentMainFrameBinding
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.ui.DocumentsDownloadFragment
import com.example.reto2_grupo2.ui.ExternalCoursesFragment
import com.example.reto2_grupo2.ui.ProfessorMainFragment
import com.example.reto2_grupo2.ui.ProfileFragment
import com.example.reto2_grupo2.ui.ReunionsFragment
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

        val client: Client? = intent.getParcelableExtra("user")
        if (client?.userType == true) {
            setContentView(mainFrameBinding.root)
            bottomNavigationView = mainFrameBinding.bottomNavigationView
        } else {
            setContentView(studentFrame.root)
            bottomNavigationView = studentFrame.bottomNavigationView
        }



        bottomNavigationView.setOnItemSelectedListener { item ->
            handleBottomNavigation(item.itemId, client?.userType, client)
            true
        }
    }


    private fun handleBottomNavigation(itemId: Int, userType: Boolean?, client: Client?) {
        when (itemId) {
            R.id.home -> {
                if (userType == true) {
                    replaceFragment(ProfessorMainFragment.newInstance(client))
                } else {
                    replaceFragment(StudentMainFragment.newInstance(client))
                }
            }

            R.id.reunions -> replaceFragment(ReunionsFragment.newInstance(client))
            R.id.user -> replaceFragment(ProfileFragment.newInstance(client))
            R.id.documents_download -> replaceFragment(DocumentsDownloadFragment.newInstance(client))
            R.id.external_courses -> replaceFragment(ExternalCoursesFragment.newInstance(client))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fM = supportFragmentManager
        val existingFragment = fM.findFragmentByTag(fragment::class.java.simpleName)
        if (existingFragment != null) {
            fM.beginTransaction()
                .replace(R.id.frameLayout, existingFragment)
                .commit()
        } else {
            fM.beginTransaction()
                .replace(R.id.frameLayout, fragment, fragment::class.java.simpleName)
                .commit()
        }
    }

}