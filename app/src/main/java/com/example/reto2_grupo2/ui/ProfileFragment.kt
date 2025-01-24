package com.example.reto2_grupo2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var languageSpinner: Spinner
    private lateinit var themeSpinner: Spinner
    private lateinit var oldPasswordTxt: EditText
    private lateinit var newPasswordTxt: EditText
    private lateinit var repeatNewPasswordTxt: EditText

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        languageSpinner = view.findViewById(R.id.languageSpinner)
        themeSpinner = view.findViewById(R.id.themeSpinner)

        oldPasswordTxt = view.findViewById(R.id.oldPasswordTxt)
        newPasswordTxt = view.findViewById(R.id.newPasswordTxt)
        repeatNewPasswordTxt = view.findViewById(R.id.repeatNewPasswordTxt)

        setupLanguageSpinner()
        setupThemeSpinner()

        val savedTheme = sharedPreferences.getString("selected_theme", "light")
        themeSpinner.setSelection(
            when (savedTheme) {
                "light" -> 0
                "dark" -> 1
                else -> 0
            }
        )

        val savedLanguage = sharedPreferences.getString("selected_language", "es")
        languageSpinner.setSelection(
            when (savedLanguage) {
                "es" -> 0
                "en" -> 1
                "eu" -> 2
                else -> 0
            }
        )
    }


    private fun setupThemeSpinner() {
        sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                val selectedTheme = when (p2) {
                    0 -> "light"
                    1 -> "dark"
                    else -> "light"
                }
                val currentTheme = sharedPreferences.getString("selected_theme", "light")
                if (currentTheme != selectedTheme) {
                    sharedPreferences.edit().putString("selected_theme", selectedTheme).apply()
                    AppCompatDelegate.setDefaultNightMode(
                        when (selectedTheme) {
                            "light" -> AppCompatDelegate.MODE_NIGHT_NO
                            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                            else -> AppCompatDelegate.MODE_NIGHT_NO
                        }
                    )
                    requireActivity().recreate()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun setupLanguageSpinner() {
        sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                val selectedLanguage = when (p2) {
                    0 -> "es"
                    1 -> "en"
                    2 -> "eu"
                    else -> "es"
                }

                val currentLanguage = sharedPreferences.getString("selected_language", "es")

                if (currentLanguage != selectedLanguage) {
                    sharedPreferences.edit().putString("selected_language", selectedLanguage)
                        .apply()
                    setLocale(selectedLanguage, requireContext())
                    requireActivity().recreate()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setLocale(languageCode: String, context: Context) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}