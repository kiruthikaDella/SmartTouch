package com.voinismartiot.voni.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.voinismartiot.voni.api.repository.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(private val repository: BaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as HomeRepository) as T
            modelClass.isAssignableFrom(ContactUsViewModel::class.java) -> ContactUsViewModel(
                repository as ContactUsRepository
            ) as T
            modelClass.isAssignableFrom(UserManagementViewModel::class.java) -> UserManagementViewModel(
                repository as UserManagementRepository
            ) as T
            else -> throw IllegalArgumentException("ViewModelClass not found.")
        }
    }
}