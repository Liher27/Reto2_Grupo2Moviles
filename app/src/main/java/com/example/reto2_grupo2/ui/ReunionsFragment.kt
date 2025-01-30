package com.example.reto2_grupo2.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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

    private lateinit var reunionThemeText: EditText
    private lateinit var reunionReasonText: EditText
    private lateinit var reunionDateText: EditText
    private lateinit var reunionClassText: EditText
    private lateinit var reunionProfessorText: EditText
    private lateinit var reunionHourText: EditText

    private lateinit var acceptButton: Button
    private lateinit var cancelButton: Button
    private lateinit var forceAcceptButton: Button
    private lateinit var createReunionButton: Button

    private lateinit var professorSpinner: Spinner
    private lateinit var selectionSpinner: Spinner

    private lateinit var selectedReunion: Reunion


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


        createReunionButton = view.findViewById(R.id.createReunionButton)
        acceptButton = view.findViewById(R.id.acceptButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        forceAcceptButton = view.findViewById(R.id.forceButton)

        professorSpinner = view.findViewById(R.id.professorsSpinner)
        selectionSpinner = view.findViewById(R.id.selectionSpinner)

        fillReunions(client)

        acceptButton.setOnClickListener {
            if (reunionText.text.isNotEmpty()) {
                socketClient?.acceptReunion(selectedReunion.reunionId)
                fillReunions(client)
            } else
                Toast.makeText(requireContext(), "No hay reuniones pendientes", Toast.LENGTH_SHORT)
                    .show()
        }
        cancelButton.setOnClickListener {
            if (reunionText.text.isNotEmpty()) {
                socketClient?.cancelReunion(selectedReunion.reunionId)
                fillReunions(client)
            } else
                Toast.makeText(requireContext(), "No hay reuniones pendientes", Toast.LENGTH_SHORT)
                    .show()
        }

        forceAcceptButton.setOnClickListener {
            if (reunionText.text.isNotEmpty()) {
                if (selectedReunion.professor.userId == client?.userId) {
                    socketClient?.forceAcceptReunion(selectedReunion.reunionId)
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

        createReunionButton.setOnClickListener {
            if (reunionThemeText.text.isNotEmpty() && reunionReasonText.text.isNotEmpty() && reunionDateText.text.isNotEmpty() &&
                reunionClassText.text.isNotEmpty() && reunionProfessorText.text.isNotEmpty() && reunionHourText.text.isNotEmpty()
            ) {
                socketClient?.createReunion(
                    reunionThemeText.text.toString(),
                    reunionReasonText.text.toString(),
                    reunionDateText.text.toString(),
                    reunionHourText.text.toString().toInt(),
                    reunionClassText.text.toString(),
                    reunionProfessorText.text.toString(),
                    client?.userId
                )
            }
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
        selectedReunion = reunion
        Log.d("reunionStatus", selectedReunion.title)
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