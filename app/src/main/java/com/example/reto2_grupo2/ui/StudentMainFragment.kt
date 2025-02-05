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
import com.example.reto2_grupo2.Singleton.SocketClientSingleton.socketClient
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.Schedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudentMainFragment : Fragment() {
    private var client: Client? = null
    private lateinit var calendarView: CalendarView


    private lateinit var textOfTheDay: TextView
    private lateinit var calendar: Calendar
    private lateinit var dayOfWeek: String
    private var dayOfTheYear: Int = 0

    private lateinit var filteredSchedules: List<Schedule>

    private lateinit var cardFirstClass: TextView
    private lateinit var cardSecondClass: TextView
    private lateinit var cardThirdClass: TextView
    private lateinit var cardFourthClass: TextView
    private lateinit var cardFiveClass: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT, Client::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.student_main_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        calendarView = view.findViewById(R.id.calendarView)
        textOfTheDay = view.findViewById(R.id.dayInfo4)

        cardFirstClass = view.findViewById(R.id.cardFirstClass)
        cardSecondClass = view.findViewById(R.id.cardSecondClass)
        cardThirdClass = view.findViewById(R.id.cardThirdClass)
        cardFourthClass = view.findViewById(R.id.cardFourthClass)
        cardFiveClass = view.findViewById(R.id.cardFifthClass)

        calendar = Calendar.getInstance()
        dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        dayOfTheYear = calendar.get(Calendar.DAY_OF_MONTH)
        textOfTheDay.text = "~$dayOfWeek: $dayOfTheYear"

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            setSchedule(year, month, dayOfMonth)
        }
    }

    private fun setSchedule(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)

        val selectedDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val selectedDayOfTheMonth = calendar.get(Calendar.DAY_OF_MONTH)

        textOfTheDay.text = "$selectedDayOfWeek, $selectedDayOfTheMonth"
        try {
            socketClient?.getScheduleSubjects(client) { schedules ->
                requireActivity().runOnUiThread {
                    filteredSchedules =
                        schedules.filter { it.scheduleDay == calendar.get(Calendar.DAY_OF_MONTH) }

                    val classesByHour = mutableMapOf<Int, MutableList<String>>()

                    filteredSchedules.forEach { schedule ->
                        val hour = schedule.scheduleHour
                        val className = "${schedule.scheduleHour}: ${schedule.subject.subjectName}"

                        if (classesByHour[hour] == null) {
                            classesByHour[hour] = mutableListOf()
                        }

                        classesByHour[hour]?.add(className)
                    }

                    cardFirstClass.text =
                        classesByHour[1]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardSecondClass.text =
                        classesByHour[2]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardThirdClass.text =
                        classesByHour[3]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardFourthClass.text =
                        classesByHour[4]?.joinToString("\n") ?: "No hay clases para esta hora"
                    cardFiveClass.text =
                        classesByHour[5]?.joinToString("\n") ?: "No hay clases para esta hora"
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No hay clases", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val ARG_CLIENT = "client"

        fun newInstance(client: Client?): StudentMainFragment {
            val fragment = StudentMainFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLIENT, client)
            fragment.arguments = args
            return fragment
        }
    }
}