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
import com.example.reto2_grupo2.entity.Professor
import com.example.reto2_grupo2.entity.Reunion
import java.text.SimpleDateFormat
import java.util.Locale

class ReunionsFragment : Fragment() {
    private var client: Client? = null

    private val socketClient = SocketClientSingleton.socketClient

    private lateinit var reunionText: TextView

    private lateinit var reunionThemeText: EditText
    private lateinit var reunionReasonText: EditText
    private lateinit var reunionDateText: EditText
    private lateinit var reunionClassText: EditText
    private lateinit var reunionHourText: EditText

    private lateinit var acceptButton: Button
    private lateinit var cancelButton: Button
    private lateinit var forceAcceptButton: Button
    private lateinit var createReunionButton: Button

    private lateinit var professorSpinner: Spinner
    private lateinit var selectionSpinner: Spinner

    private lateinit var selectedReunion: Reunion

    val reunionProfessors = ArrayList<Professor>()


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

        createReunionButton = view.findViewById(R.id.createReunionButton)
        acceptButton = view.findViewById(R.id.acceptButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        forceAcceptButton = view.findViewById(R.id.forceButton)

        reunionThemeText = view.findViewById(R.id.themeText)
        reunionReasonText = view.findViewById(R.id.reasonText)
        reunionDateText = view.findViewById(R.id.dateText)
        reunionClassText = view.findViewById(R.id.classText)
        reunionHourText = view.findViewById(R.id.hourText)

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
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            if (reunionThemeText.text.isNotEmpty() && reunionReasonText.text.isNotEmpty() && reunionDateText.text.isNotEmpty() &&
                reunionClassText.text.isNotEmpty() && reunionProfessors.isNotEmpty() && reunionHourText.text.isNotEmpty()
            ) {
                val date = dateFormat.parse(reunionDateText.text.toString())

                if (date == null) {
                    Toast.makeText(
                        requireContext(),
                        "Formato de fecha inválido (yyyy-MM-dd)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val hour = reunionHourText.text.toString().toInt()
                if (hour !in 0..23) {
                    Toast.makeText(
                        requireContext(),
                        "La hora debe estar entre 0 y 23",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                socketClient?.createReunion(
                    reunionThemeText.text.toString(),
                    reunionReasonText.text.toString(),
                    reunionDateText.text.toString(),
                    reunionHourText.text.toString().toInt(),
                    reunionClassText.text.toString(),
                    reunionProfessors,
                    client?.userId
                )
            } else {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun fillReunions(client: Client?) {
        socketClient?.getReunions(client) { reunions, professors ->
            val title = reunions.map { it.title }.toMutableList()
            requireActivity().runOnUiThread {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    title
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                selectionSpinner.adapter = adapter

                val professorsNames = professors.map { it.name }.toMutableList()

                val adapter2 = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    professorsNames
                )
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                professorSpinner.adapter = adapter2
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
            reunionProfessors.clear()
            professorSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        //Si no se encuentra en la lista, lo añadimos
                        professors[position].let { reunionProfessors.add(it) }
                        Toast.makeText(
                            requireContext(),
                            "Profesor añadido!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
        }
    }

    private fun showReunion(reunion: Reunion) {
        selectedReunion = reunion
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

        //Cambiamos el texto de la reunion variando de su estado
        val reunionState = when (reunion.reunionState) {
            11 -> "Forzada"
            10 -> "Aceptada"
            0 -> "Rechazada"
            else -> "Pendiente"
        }

        //Mostramos el nombre del dueño de la reunion
        val owner = reunion.professor.name

        //Mostramos los nombres de los profesores de la reunion
        val professors = reunion.assistants.map { it.professor.name }.joinToString(", ")

        //Añadimos todo en un string
        reunionText.text = String.format(
            "%s \n \n %s \n Dia: %s Hora: %s \n Aula: %s \n Estado de la reunion: %s \n Asistentes: %s \n Dueño de la reunion: %s",
            reunion.title,
            reunion.affair,
            reunion.day,
            reunion.hour,
            reunion.class_,
            reunionState,
            professors,
            owner
        )
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