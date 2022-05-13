package com.voinismartiot.voni.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.voinismartiot.voni.api.NetworkModule
import com.voinismartiot.voni.api.repository.BaseRepository
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.viewmodel.ViewModelFactory

abstract class BaseFragment<VM : ViewModel, B : ViewBinding, R : BaseRepository> :
    Fragment() {

    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected val networkModel = NetworkModule.provideSmartTouchApi(NetworkModule.provideRetrofit())
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(requireActivity(), factory).get(getViewModel())

        hideKeyboard()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModelStore.clear()
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getFragmentRepository(): R

    protected fun isInternetConnected(): Boolean {
        var isInternetConnected = false
        NotifyManager.internetInfo.observe(viewLifecycleOwner) {
            isInternetConnected = it
        }
        return isInternetConnected
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        try {
            val imm =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}