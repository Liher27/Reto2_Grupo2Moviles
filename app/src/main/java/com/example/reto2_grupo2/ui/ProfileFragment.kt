package com.example.reto2_grupo2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.Singleton.SocketClientSingleton
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.socketIO.SocketClient
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var languageSpinner: Spinner
    private lateinit var themeSpinner: Spinner
    private lateinit var oldPasswordTxt: EditText
    private lateinit var newPasswordTxt: EditText
    private lateinit var repeatNewPasswordTxt: EditText
    private var client: Client? = null
    private lateinit var changePasswordButton: Button
    private val socketClient = SocketClientSingleton.socketClient

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT, Client::class.java)
        sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

        changePasswordButton = view.findViewById(R.id.button)

        setupLanguageSpinner()
        setupThemeSpinner()

        changePasswordButton.setOnClickListener {
            if (oldPasswordTxt.text.toString() == client?.pass) {
                if (newPasswordTxt.text.toString() == repeatNewPasswordTxt.text.toString()) {
                    socketClient?.changePassword(
                        client,
                        newPasswordTxt.text.toString()
                    )
                } else Toast.makeText(
                    requireContext(), "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT)
                .show()

        }

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
        private const val ARG_CLIENT = "client"
        fun newInstance(client: Client?): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLIENT, client)
            fragment.arguments = args
            return fragment
        }
    }
}