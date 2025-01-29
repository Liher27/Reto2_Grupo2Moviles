package com.example.reto2_grupo2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.Singleton.SocketClientSingleton
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.socketIO.SocketClient

class ReunionsFragment : Fragment() {
    private var client: Client? = null

    private val socketClient = SocketClientSingleton.socketClient

    private lateinit var reunionTextView: TextView
    private lateinit var themeTextView: TextView
    private lateinit var reasonTextView: TextView
    private lateinit var dateAndHourTextView: TextView
    private lateinit var classTextView: TextView
    private lateinit var professorTextView: TextView
    private lateinit var hourTextView: TextView
    private lateinit var dateText: TextView

    private lateinit var acceptButton: Button
    private lateinit var cancelButton: Button
    private lateinit var forceAcceptButton: Button
    private lateinit var createReunionButton: Button

    private lateinit var professorSpinner: Spinner
    private lateinit var selectionSpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT, Client::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reunions_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        reunionTextView = view.findViewById(R.id.reunionTextView)
        themeTextView = view.findViewById(R.id.themeTextView)
        reasonTextView = view.findViewById(R.id.reasonTextView)
        dateAndHourTextView = view.findViewById(R.id.dateAndHourTextView)
        classTextView = view.findViewById(R.id.classTextView)
        professorTextView = view.findViewById(R.id.professorTextView)
        hourTextView = view.findViewById(R.id.hourText)
        dateText = view.findViewById(R.id.dateText)

        createReunionButton = view.findViewById(R.id.createReunionButton)
        acceptButton = view.findViewById(R.id.acceptButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        forceAcceptButton = view.findViewById(R.id.forceButton)

        professorSpinner = view.findViewById(R.id.professorsSpinner)
        selectionSpinner = view.findViewById(R.id.selectionSpinner)

        fillReunions(client)

    }

    private fun fillReunions(client: Client?) {
        socketClient?.getReunions(client) {
        }
    }

    companion object {
        private const val ARG_CLIENT = "client"

        fun newInstance(client: Client?): ReunionsFragment {
            val fragment = ReunionsFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLIENT, client)
            fragment.arguments = args
            return fragment
        }
    }
}