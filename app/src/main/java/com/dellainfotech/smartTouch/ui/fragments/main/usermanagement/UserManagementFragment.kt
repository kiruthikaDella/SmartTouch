package com.dellainfotech.smartTouch.ui.fragments.main.usermanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.adapters.UserManagementAdapter
import com.dellainfotech.smartTouch.databinding.FragmentUserManagementBinding
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class UserManagementFragment : BaseFragment() {

    private lateinit var binding: FragmentUserManagementBinding
    private lateinit var userAdapter: UserManagementAdapter
    private var userList = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userList.clear()
        userList.add("Patty Garg")
        userList.add("Olive Yew")
        userList.add("Aida Bugg")
        userList.add("Maureen Biologist")
        userList.add("Teri Dactyl")
        userList.add("Patty Garg")
        userList.add("Olive Yew")
        userList.add("Aida Bugg")
        userList.add("Patty Garg")

        userAdapter = UserManagementAdapter(userList)
        binding.recyclerRegisteredUser.adapter = userAdapter

        binding.ivRegisterUser.setOnClickListener {
            findNavController().navigate(UserManagementFragmentDirections.actionUserManagementFragmentToAddUserFragment())
        }
    }

}