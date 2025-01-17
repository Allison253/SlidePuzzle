package com.example.slidepuzzle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.slidepuzzle.databinding.FragmentWinBinding


/**
 * A simple [Fragment] subclass.
 * Use the [WinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WinFragment : DialogFragment() {
    private lateinit var binding: FragmentWinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentWinBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_win, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.popupWindowButton.setOnClickListener { CloseDialog()}
    }

    private fun CloseDialog(){
        dialog!!.dismiss()
    }


    }
