package com.example.reto2_grupo2.ui

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.Document
import com.example.reto2_grupo2.socketIO.SocketClient


class DocumentsDownloadFragment : Fragment() {
    private lateinit var downloadButton: Button
    private var manager: DownloadManager? = null
    private lateinit var documentSelectionSpinner: Spinner
    private lateinit var filterSpinner: Spinner
    private var socketClient: SocketClient? = null
    private var documentLink: String = ""
    private var client: Client? = null
    private var links: List<String>? = null
    private var documents: List<Document>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT)
        socketClient = SocketClient(this)
        socketClient!!.connect()
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
            if (documentLink != "")
                downloadDocument(documentLink)
            else
                Toast.makeText(requireContext(), "Seleccione un documento", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun filterByCourse() {
        documents = socketClient?.filterByCourse(client)
        val linksList = documents?.map { it.link }
        if (documents != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                linksList!!
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            documentSelectionSpinner.adapter = adapter
            adapter.notifyDataSetChanged()
            documentSelectionSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        documentLink = documents!![position].link
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
        } else
            Toast.makeText(requireContext(), "No se han encontrado documentos", Toast.LENGTH_SHORT)
                .show()
    }

    private fun filterByCycle() {
        documents = socketClient?.filterByCycle(client)
        val linksList = documents?.map { it.link }
        if (documents != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                linksList!!
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            documentSelectionSpinner.adapter = adapter
            adapter.notifyDataSetChanged()
            documentSelectionSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        documentLink = documents!![position].link
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
        } else
            Toast.makeText(requireContext(), "No se han encontrado documentos", Toast.LENGTH_SHORT)
                .show()
    }

    private fun filterBySubject() {
        socketClient?.filterBySubject(client) { links ->
            if (links.isNotEmpty()) {
                requireActivity().runOnUiThread {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        links
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    documentSelectionSpinner.adapter = adapter

                    documentSelectionSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                documentLink = links[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            } else {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "No se han encontrado documentos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun downloadDocument(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationInExternalFilesDir(requireContext(), "/downloads", "documento.pdf")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setMimeType("application/pdf")
        manager = getSystemService(requireContext(), DownloadManager::class.java)
        manager?.enqueue(request)
    }

    companion object {
        private const val ARG_CLIENT = "client"

        fun newInstance(client: Client?): DocumentsDownloadFragment {
            val fragment = DocumentsDownloadFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLIENT, client)
            fragment.arguments = args
            return fragment
        }
    }
}