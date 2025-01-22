package com.example.reto2_grupo2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.entity.Client
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentMainFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.student_main_fragment, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.MONTH, -4)
        val minDate = calendar.timeInMillis
        calendarView.minDate = minDate

        calendar.add(Calendar.MONTH, 8)
        calendar.add(Calendar.DAY_OF_MONTH, 13)
        val maxDate = calendar.timeInMillis
        calendarView.maxDate = maxDate
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