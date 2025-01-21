package com.example.reto2_grupo2.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DocumentsDownloadFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var downloadButton : Button
    private var manager: DownloadManager? = null

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
        return inflater.inflate(R.layout.document_download_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        downloadButton = view.findViewById(R.id.button2)
        downloadButton.setOnClickListener {
            downloadDocument()
        }
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DocumentsDownloadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}