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
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.socketIO.SocketClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudentMainFragment : Fragment() {
    private var client: Client? = null
    private var socketClient: SocketClient? = null
    private lateinit var calendarView: CalendarView
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
        return inflater.inflate(R.layout.student_main_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        calendarView = view.findViewById(R.id.calendarView)
        val cardTitle = view.findViewById<TextView>(R.id.cardTitle)
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

            cardTitle.text = ""
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