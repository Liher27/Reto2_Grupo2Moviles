package com.example.reto2_grupo2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.Singleton.SocketClientSingleton
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.Reunion

class ReunionsFragment : Fragment() {
    private var client: Client? = null

    private val socketClient = SocketClientSingleton.socketClient

    private lateinit var reunionText: TextView

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

    private var reunionId: Int = 0
    private var reunionOwnerId: Int = 0


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

        reunionText = view.findViewById(R.id.reunionText)
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

        acceptButton.setOnClickListener {
            if (reunionText.text.isNotEmpty()) {
                socketClient?.acceptReunion(reunionId)
                fillReunions(client)
            } else
                Toast.makeText(requireContext(), "No hay reuniones pendientes", Toast.LENGTH_SHORT)
                    .show()
        }

        cancelButton.setOnClickListener {
            if (reunionText.text.isNotEmpty()) {
                socketClient?.cancelReunion(reunionId)
                fillReunions(client)
            } else
                Toast.makeText(requireContext(), "No hay reuniones pendientes", Toast.LENGTH_SHORT)
                    .show()
        }

        forceAcceptButton.setOnClickListener {
            if (reunionText.text.isNotEmpty()) {
                if (reunionOwnerId == client?.userId) {
                    socketClient?.forceAcceptReunion(reunionId)
                    fillReunions(client)
                } else
                    Toast.makeText(
                        requireContext(),
                        "No eres el dueño de la reunion",
                        Toast.LENGTH_SHORT
                    )
                        .show()
            } else
                Toast.makeText(requireContext(), "No hay reuniones pendientes", Toast.LENGTH_SHORT)
                    .show()
        }


    }

    private fun fillReunions(client: Client?) {
        socketClient?.getReunions(client) { reunions ->
            val title = reunions.map { it.title }.toMutableList()
            requireActivity().runOnUiThread {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    title
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                selectionSpinner.adapter = adapter
            }

            selectionSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        showReunion(reunions[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
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

    private fun showReunion(reunion: Reunion) {
        reunionId = reunion.reunionId

        reunionOwnerId = reunion.professor.userId

        //Si la reunion esta en un estado importante, no debe cambiarse mas veces.
        if (reunion.reunionState == 11 || reunion.reunionState == 0 || reunion.reunionState == 10) {
            acceptButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
            forceAcceptButton.visibility = View.GONE
        } else {
            acceptButton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            forceAcceptButton.visibility = View.VISIBLE
        }

        val reunionState = when (reunion.reunionState) {
            11 -> "Forzada"
            10 -> "Aceptada"
            0 -> "Rechazada"
            else -> "Pendiente"
        }

        val owner =
            if (reunion.professor.userId == client?.userId) "TU" else reunion.professor.userId

        val professorsId = reunion.assistants.map { it.professor.userId }
        reunionText.text = String.format(
            "%s \n \n %s \n Dia: %s Hora: %s \n Aula: %s \n Estado de la reunion: %s \n Asistentes: %s \n Dueño de la reunion: %s",
            reunion.title,
            reunion.affair,
            reunion.day,
            reunion.hour,
            reunion.class_,
            reunionState,
            professorsId,
            owner
        )
    }
}