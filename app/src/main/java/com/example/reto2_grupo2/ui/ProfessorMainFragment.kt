package com.example.reto2_grupo2.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.Singleton.SocketClientSingleton
import com.example.reto2_grupo2.entity.Client
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfessorMainFragment : Fragment() {
    private var client: Client? = null
    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT, Client::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.professor_main_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        val cardFirtsClass = view.findViewById<TextView>(R.id.cardFirstClass)
        val cardSecondClass = view.findViewById<TextView>(R.id.cardSecondClass)
        val cardThirdClass = view.findViewById<TextView>(R.id.cardThirdClass)
        val cardFourthClass = view.findViewById<TextView>(R.id.cardFourtClass)
        val cardFiveClass = view.findViewById<TextView>(R.id.cardFiveClass)
        val textOfTheDay = view.findViewById<TextView>(R.id.textView5)

        val calendar = Calendar.getInstance()
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val dayOfTheYear = calendar.get(Calendar.DAY_OF_MONTH)
        textOfTheDay.text = "~$dayOfWeek: $dayOfTheYear"

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            calendar.set(year, month, dayOfMonth)

            val selectedDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
            val selectedDayOfTheMonth = calendar.get(Calendar.DAY_OF_MONTH)

            textOfTheDay.text = "~$selectedDayOfWeek: $selectedDayOfTheMonth"

            Toast.makeText(requireContext(), "Fecha seleccionada: $selectedDate", Toast.LENGTH_SHORT).show()

            SocketClientSingleton.socketClient?.getScheduleSubjects(client) { schedules ->
                requireActivity().runOnUiThread {
                    val filteredSchedules = schedules.filter { it.scheduleDay == calendar.get(Calendar.DAY_OF_MONTH) }

                    val classesByHour = mutableMapOf<Int, MutableList<String>>()

                    filteredSchedules.forEach { schedule ->
                        val hour = schedule.scheduleHour
                        val className = "${schedule.scheduleHour}: ${schedule.subject.subjectName}"

                        if (classesByHour[hour] == null) {
                            classesByHour[hour] = mutableListOf()
                        }

                        classesByHour[hour]?.add(className)
                    }

                    cardFirtsClass.text = classesByHour[1]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardSecondClass.text = classesByHour[2]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardThirdClass.text = classesByHour[3]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardFourthClass.text = classesByHour[4]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardFiveClass.text = classesByHour[5]?.joinToString("\n") ?: "No hay clases para esta hora"
                }
            }
        }
    }

    companion object {
        private const val ARG_CLIENT = "client"

        fun newInstance(client: Client?): ProfessorMainFragment {
            val fragment = ProfessorMainFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLIENT, client)
            fragment.arguments = args
            return fragment
        }
    }
}
