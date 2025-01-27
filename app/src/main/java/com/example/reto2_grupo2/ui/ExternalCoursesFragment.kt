package com.example.reto2_grupo2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.ExternalCourse
import com.example.reto2_grupo2.socketIO.SocketClient
import com.google.android.gms.maps.MapView

class ExternalCoursesFragment : Fragment() {
    private var client: Client? = null
    private var socketClient: SocketClient? = null
    private var externalCoursesSpinner: Spinner? = null
    private var externalCourseDescription: TextView? = null
    private var externalCourseUbi: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT, Client::class.java)
        socketClient = SocketClient(this)
        socketClient!!.connect()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.external_courses_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        externalCoursesSpinner = view.findViewById(R.id.externalCoursesSpinner)
        externalCourseDescription = view.findViewById(R.id.externalCourseInfoText)
        externalCourseUbi = view.findViewById(R.id.externalCourseUbi)
        fillExternalCoursesSpinner()
    }

    private fun fillExternalCoursesSpinner() {
        socketClient?.getExternalCourses(client) { externalCourses ->
            if (externalCourses.isNotEmpty()) {
                val title = externalCourses.map { it.title }.toMutableList() // Extract titles here
                requireActivity().runOnUiThread {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        title
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    externalCoursesSpinner?.adapter = adapter

                    externalCoursesSpinner?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                putInfo(externalCourses[position])
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            } else {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "No se han encontrado documentos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun putInfo(externalCourse: ExternalCourse) {
        externalCourseDescription?.text = String.format(
            "%s \n %s \n %s horas \n fecha de inicio: %s \n fecha de fin: %s",
            externalCourse.title,
            externalCourse.description,
            externalCourse.schedule,
            externalCourse.startDate,
            externalCourse.endDate
        )
    }

    companion object {
        private const val ARG_CLIENT = "client"


        fun newInstance(client: Client?): ExternalCoursesFragment {
            val fragment = ExternalCoursesFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLIENT, client)
            fragment.arguments = args
            return fragment
        }
    }
}