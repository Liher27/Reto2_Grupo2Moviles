package com.example.reto2_grupo2.ui

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.socketIO.SocketClient


private const val ARG_USER = "user"

class DocumentsDownloadFragment : Fragment() {
    private lateinit var downloadButton: Button
    private var manager: DownloadManager? = null
    private lateinit var documentSelectionSpinner: Spinner
    private lateinit var filterSpinner: Spinner
    private var socketClient: SocketClient? = null
    private var documentLink: String = ""
    private var documentsLinks: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = arguments?.getParcelable<Client>(ARG_USER)
        socketClient = SocketClient(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.document_download_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        documentSelectionSpinner = view.findViewById(R.id.documentSelectionSpinner)
        filterSpinner = view.findViewById(R.id.filterSpinner)
        downloadButton = view.findViewById(R.id.documentDownloadButton)

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> filterByCourse()
                    1 -> filterByCycle()
                    2 -> filterBySubject()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        //Aqui dependiendo del tamaño del arrayList de Strings que nos devuelva el anterior spinner,
        // pondremos un tamaño mayor o menor, y llenaremos con nombres como: Documento + i
        // y luego que sea on item selected, y que pille el link y lo ponga en documentLink


        downloadButton.setOnClickListener {
            downloadDocument()
        }
    }

    private fun filterByCourse() {
        socketClient?.filterByCourse()
    }

    private fun filterByCycle() {

    }

    private fun filterBySubject() {

    }

    private fun downloadDocument() {
        val url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationInExternalFilesDir(requireContext(), "/downloads", "documento.pdf")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        manager = getSystemService(requireContext(), DownloadManager::class.java)
        manager?.enqueue(request)
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