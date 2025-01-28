package com.example.reto2_grupo2.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.Singleton.SocketClientSingleton
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.ExternalCourse
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
const val ACCESS_FINE_LOCATION_CODE = 100

class ExternalCoursesFragment : Fragment(), OnMapReadyCallback {
    private var client: Client? = null
    private var externalCoursesSpinner: Spinner? = null
    private var externalCourseDescription: TextView? = null
    private lateinit var externalCourseUbi: MapView
    private val socketClient = SocketClientSingleton.socketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT, Client::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.external_courses_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalCoursesSpinner = view.findViewById(R.id.externalCoursesSpinner)
        externalCourseDescription = view.findViewById(R.id.externalCourseInfoText)

        externalCourseUbi = view.findViewById(R.id.externalCourseUbi)
        externalCourseUbi.onCreate(savedInstanceState)

        askForPermissions(savedInstanceState)

        fillExternalCoursesSpinner()
    }

    private fun fillExternalCoursesSpinner() {
        if (externalCoursesSpinner?.adapter != null) return

        socketClient?.getExternalCourses(client) { externalCourses ->
            if (isAdded) {
                if (externalCourses.isNotEmpty()) {
                    val title = externalCourses.map { it.title }.toMutableList()
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
                                    putUbi(externalCourses[position])
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }
                            }
                    }
                } else {
                    if (isAdded) {
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
        }
    }

    private fun putUbi(externalCourse: ExternalCourse) {
        val latitudeLong = externalCourse.latitude.toDouble()
        val longitudeLong = externalCourse.longitude.toDouble()


        val latLng = LatLng(latitudeLong, longitudeLong)

        val marker = MarkerOptions().position(latLng).title(externalCourse.title)
        externalCourseUbi.getMapAsync { map ->
            map.addMarker(marker)
        }
    }

    private fun putInfo(externalCourse: ExternalCourse) {
        externalCourseDescription?.text = String.format(
            "%s \n \n %s \n Tiempo total: %s horas \n Fecha de inicio: %s \n Fecha de fin: %s",
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

    //Métodos para evitar crasheos al volver al fragment rapidamente.
    override fun onDestroyView() {
        super.onDestroyView()
        externalCoursesSpinner = null
        externalCourseDescription = null
        externalCourseUbi.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        externalCourseUbi.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        externalCourseUbi.onResume()
    }

    override fun onStart() {
        super.onStart()
        externalCourseUbi.onStart()
    }

    override fun onStop() {
        super.onStop()
        externalCourseUbi.onStop()
    }

    override fun onPause() {
        externalCourseUbi.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        externalCourseUbi.onDestroy()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        externalCourseUbi.onLowMemory()
    }

    private fun askForPermissions(savedInstanceState: Bundle?) {
        //Pedimos permisos si no los tiene..
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_CODE
            )
        } else {
            //Si los tiene, inicializamos el mapa
            externalCourseUbi.let { mapView ->
                mapView.onCreate(savedInstanceState)
                mapView.getMapAsync(this)
            }

        }
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(
                requireContext(),
                "No se ha permitido el acceso a la localización",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}