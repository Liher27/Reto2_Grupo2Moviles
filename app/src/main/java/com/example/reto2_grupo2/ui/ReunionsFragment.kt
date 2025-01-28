package com.example.reto2_grupo2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.Singleton.SocketClientSingleton
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.socketIO.SocketClient

class ReunionsFragment : Fragment() {
    private var client: Client? = null
    private val socketClient = SocketClientSingleton.socketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = arguments?.getParcelable(ARG_CLIENT, Client::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reunions_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }

    companion object {
        private const val ARG_CLIENT = "client"

        fun newInstance(client: Client?): ReunionsFragment {
            val fragment = ReunionsFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLIENT, client)
            fragment.arguments = args
            return fragment
        }
    }
}