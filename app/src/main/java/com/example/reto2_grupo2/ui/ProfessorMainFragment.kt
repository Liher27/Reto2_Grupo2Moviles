package com.example.reto2_grupo2.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.Singleton.SocketClientSingleton.socketClient
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.Reunion
import com.example.reto2_grupo2.entity.Schedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfessorMainFragment : Fragment() {
    private var client: Client? = null
    private lateinit var calendarView: CalendarView

    private lateinit var cardFirstClass: TextView
    private lateinit var cardSecondClass: TextView
    private lateinit var cardThirdClass: TextView
    private lateinit var cardFourthClass: TextView
    private lateinit var cardFiveClass: TextView
    private lateinit var textOfTheDay: TextView
    private lateinit var reunionText: TextView

    private lateinit var filteredSchedules: List<Schedule>

    private lateinit var reunionsSpinner: Spinner

    private lateinit var calendar: Calendar
    private lateinit var dayOfWeek: String
    private var dayOfTheYear: Int = 0

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

        reunionsSpinner = view.findViewById(R.id.reunionsSpinner)
        calendarView = view.findViewById(R.id.calendarView)
        cardFirstClass = view.findViewById(R.id.cardFirstClass)
        cardSecondClass = view.findViewById(R.id.cardSecondClass)
        cardThirdClass = view.findViewById(R.id.cardThirdClass)
        cardFourthClass = view.findViewById(R.id.cardFourClass)
        cardFiveClass = view.findViewById(R.id.cardFiveClass)
        textOfTheDay = view.findViewById(R.id.dayInfo)
        reunionText = view.findViewById(R.id.textView10)

        calendar = Calendar.getInstance()
        dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        dayOfTheYear = calendar.get(Calendar.DAY_OF_MONTH)
        textOfTheDay.text = "~$dayOfWeek: $dayOfTheYear"

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            setSchedule(year, month, dayOfMonth)
            setReunions(month + 1, dayOfMonth, client)
        }
    }

    private fun setSchedule(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)

        val selectedDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val selectedDayOfTheMonth = calendar.get(Calendar.DAY_OF_MONTH)

        textOfTheDay.text = "$selectedDayOfWeek, $selectedDayOfTheMonth"
        try {
            socketClient?.getScheduleSubjects(client) { schedules ->
                if (schedules.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay clases", Toast.LENGTH_SHORT).show()
                    cardFirstClass.visibility = View.GONE
                    cardSecondClass.visibility = View.GONE
                    cardThirdClass.visibility = View.GONE
                    cardFourthClass.visibility = View.GONE
                    cardFiveClass.visibility = View.GONE
                }

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

    private fun setReunions(month: Int, dayOfMonth: Int, client: Client?) {
        socketClient?.getReunions(client) { reunions, _ ->
            val conflictedReunions = reunions.filter {
                val (reunionDay, reunionMonth) = getDayAndMonth(it.day)
                reunionDay == dayOfMonth && reunionMonth == month
            }
            val title = conflictedReunions.map { it.title }.toMutableList()

            requireActivity().runOnUiThread {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    title
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reunionsSpinner.adapter = adapter

            }

            reunionsSpinner.onItemSelectedListener =
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

    private fun showReunion(reunion: Reunion) {

        val owner = reunion.professor.name

        val professors = reunion.assistants.joinToString(", ") { it.professor.name }

        reunionText.text = String.format(
            "%s \n \n %s \n Dia: %s Hora: %s \n Aula: %s \n Asistentes: %s \n Dueño de la reunion: %s",
            reunion.title,
            reunion.affair,
            reunion.day,
            reunion.hour,
            reunion.class_,
            professors,
            owner
        )
    }

    //Para conseguir en formato de int el dia y el mes de la reunion
    private fun getDayAndMonth(day: String): Pair<Int, Int> {
        val dateFormat =
            SimpleDateFormat("MMM d, yyyy", Locale("es", "ES"))
        val date: Date = dateFormat.parse(day) ?: Date()

        val calendar = Calendar.getInstance()
        calendar.time = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month =
            calendar.get(Calendar.MONTH) + 1

        return Pair(day, month)
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
