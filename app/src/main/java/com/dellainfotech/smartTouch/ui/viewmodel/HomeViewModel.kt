package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.*
import com.dellainfotech.smartTouch.api.model.*
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeViewModel @ViewModelInject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _logoutResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val logoutResponse: LiveData<Resource<CommonResponse>>
        get() = _logoutResponse

    private val _roomTypeResponse: MutableLiveData<Resource<RoomTypeResponse>> = MutableLiveData()
    val roomTypeResponse: LiveData<Resource<RoomTypeResponse>>
        get() = _roomTypeResponse

    private val _getRoomResponse: MutableLiveData<Resource<GetRoomResponse>> = MutableLiveData()
    val getRoomResponse: LiveData<Resource<GetRoomResponse>>
        get() = _getRoomResponse

    private val _addRoomResponse: MutableLiveData<Resource<AddRoomResponse>> = MutableLiveData()
    val addRoomResponse: LiveData<Resource<AddRoomResponse>>
        get() = _addRoomResponse

    private val _retainStateResponse: MutableLiveData<Resource<AddRoomResponse>> = MutableLiveData()
    val retainStateResponse: LiveData<Resource<AddRoomResponse>>
        get() = _retainStateResponse

    private val _updateRoomResponse: MutableLiveData<Resource<UpdateRoomResponse>> = MutableLiveData()
    val updateRoomResponse: LiveData<Resource<UpdateRoomResponse>>
        get() = _updateRoomResponse

    private val _faqResponse: MutableLiveData<Resource<FAQResponse>> = MutableLiveData()
    val faqResponse: LiveData<Resource<FAQResponse>>
        get() = _faqResponse

    private val _getUserProfileResponse: MutableLiveData<Resource<GetProfileResponse>> =
        MutableLiveData()
    val getUserProfileResponse: LiveData<Resource<GetProfileResponse>>
        get() = _getUserProfileResponse

    private val _updateUserProfileResponse: MutableLiveData<Resource<GetProfileResponse>> = MutableLiveData()
    val updateUserProfileResponse: LiveData<Resource<GetProfileResponse>>
        get() = _updateUserProfileResponse

    private val _changePasswordResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val changePasswordResponse: LiveData<Resource<CommonResponse>>
        get() = _changePasswordResponse

    private val _updatePinStatusResponse: MutableLiveData<Resource<PinResponse>> = MutableLiveData()
    val updatePinStatusResponse: LiveData<Resource<PinResponse>>
        get() = _updatePinStatusResponse

    fun logout(
        bodyLogout: BodyLogout
    ) = viewModelScope.launch {
        _logoutResponse.value = Resource.Loading
        _logoutResponse.value = homeRepository.logout(bodyLogout)
    }

    fun roomType() = viewModelScope.launch {
        _roomTypeResponse.value = Resource.Loading
        _roomTypeResponse.value = homeRepository.roomType()
    }

    fun getRoom() = viewModelScope.launch {
        _getRoomResponse.value = Resource.Loading
        _getRoomResponse.value = homeRepository.getRoom()
    }

    fun addRoom(bodyAddRoom: BodyAddRoom) = viewModelScope.launch {
        _addRoomResponse.value = Resource.Loading
        _addRoomResponse.value = homeRepository.addRoom(bodyAddRoom)
    }

    fun updateRoom(bodyUpdateRoom: BodyUpdateRoom) = viewModelScope.launch {
        _updateRoomResponse.value = Resource.Loading
        _updateRoomResponse.value = homeRepository.updateRoom(bodyUpdateRoom)
    }

    fun getFAQ() = viewModelScope.launch {
        _faqResponse.value = Resource.Loading
        _faqResponse.value = homeRepository.getFAQ()
    }

    fun getUserProfile() = viewModelScope.launch {
        _getUserProfileResponse.value = Resource.Loading
        _getUserProfileResponse.value = homeRepository.getUserProfile()
    }

    fun updateUserProfile(bodyUpdateUserProfile: BodyUpdateUserProfile) = viewModelScope.launch {
        _updateUserProfileResponse.value = Resource.Loading
        _updateUserProfileResponse.value = homeRepository.updateUserProfile(bodyUpdateUserProfile)
    }

    fun changePassword(bodyChangePassword: BodyChangePassword) = viewModelScope.launch {
        _changePasswordResponse.value = Resource.Loading
        _changePasswordResponse.value = homeRepository.changePassword(bodyChangePassword)
    }

    fun retainState(bodyRetainState: BodyRetainState) = viewModelScope.launch {
        _retainStateResponse.value = Resource.Loading
        _retainStateResponse.value = homeRepository.retainState(bodyRetainState)
    }

    fun updatePinStatus(bodyPinStatus: BodyPinStatus) = viewModelScope.launch {
        _updatePinStatusResponse.value = Resource.Loading
        _updatePinStatusResponse.value = homeRepository.updatePinStatus(bodyPinStatus)
    }


    //
    //region Device
    //

    private val _addDeviceResponse: MutableLiveData<Resource<AddDeviceResponse>> = MutableLiveData()
    val addDeviceResponse: LiveData<Resource<AddDeviceResponse>>
        get() = _addDeviceResponse

    private val _getDeviceResponse: MutableLiveData<Resource<GetDeviceResponse>> = MutableLiveData()
    val getDeviceResponse: LiveData<Resource<GetDeviceResponse>>
        get() = _getDeviceResponse

    private val _getDeviceCustomizationSettingsResponse: MutableLiveData<Resource<DeviceCustomizationResponse>> = MutableLiveData()
    val getDeviceCustomizationSettingsResponse: LiveData<Resource<DeviceCustomizationResponse>>
        get() = _getDeviceCustomizationSettingsResponse

    private val _getDeviceFeatureSettingsResponse: MutableLiveData<Resource<DeviceFeatureResponse>> = MutableLiveData()
    val getDeviceFeatureSettingsResponse: LiveData<Resource<DeviceFeatureResponse>>
        get() = _getDeviceFeatureSettingsResponse

    private val _deleteDeviceResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val deleteDeviceResponse: LiveData<Resource<CommonResponse>>
        get() = _deleteDeviceResponse

    private val _updateDeviceNameResponse: MutableLiveData<Resource<AddDeviceResponse>> = MutableLiveData()
    val updateDeviceNameResponse: LiveData<Resource<AddDeviceResponse>>
        get() = _updateDeviceNameResponse

    private val _updateSwitchNameResponse: MutableLiveData<Resource<UpdateSwitchNameResponse>> = MutableLiveData()
    val updateSwitchNameResponse: LiveData<Resource<UpdateSwitchNameResponse>>
        get() = _updateSwitchNameResponse

    private val _customizationLockResponse: MutableLiveData<Resource<DeviceCustomizationResponse>> = MutableLiveData()
    val customizationLockResponse: LiveData<Resource<DeviceCustomizationResponse>>
        get() = _customizationLockResponse

    private val _iconListResponse: MutableLiveData<Resource<IconListResponse>> = MutableLiveData()
    val iconListResponse: LiveData<Resource<IconListResponse>>
        get() = _iconListResponse

    private val _updateSwitchIconResponse: MutableLiveData<Resource<UpdateSwitchIconResponse>> = MutableLiveData()
    val updateSwitchIconResponse: LiveData<Resource<UpdateSwitchIconResponse>>
        get() = _updateSwitchIconResponse

    private val _getControlResponse: MutableLiveData<Resource<ControlModeResponse>> = MutableLiveData()
    val getControlResponse: LiveData<Resource<ControlModeResponse>>
        get() = _getControlResponse

    private val _imageUploadResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val imageUploadResponse: LiveData<Resource<CommonResponse>>
        get() = _imageUploadResponse

    private val _deleteImageResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val deleteImageResponse: LiveData<Resource<CommonResponse>>
        get() = _deleteImageResponse

    private val _getSceneResponse: MutableLiveData<Resource<GetSceneResponse>> = MutableLiveData()
    val getSceneResponse: LiveData<Resource<GetSceneResponse>>
        get() = _getSceneResponse

    private val _addSceneResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val addSceneResponse: LiveData<Resource<CommonResponse>>
        get() = _addSceneResponse

    fun addDevice(bodyAddDevice: BodyAddDevice) = viewModelScope.launch {
        _addDeviceResponse.value = Resource.Loading
        _addDeviceResponse.value = homeRepository.addDevice(bodyAddDevice)
    }

    fun getDevice(roomId: String) = viewModelScope.launch {
        _getDeviceResponse.value = Resource.Loading
        _getDeviceResponse.value = homeRepository.getDeviceData(roomId)
    }

    fun getDeviceCustomization(deviceId: String) = viewModelScope.launch {
        _getDeviceCustomizationSettingsResponse.value = Resource.Loading
        _getDeviceCustomizationSettingsResponse.value = homeRepository.getDeviceCustomizationSettings(deviceId)
    }

    fun getDeviceFeatures(deviceId: String) = viewModelScope.launch {
        _getDeviceFeatureSettingsResponse.value = Resource.Loading
        _getDeviceFeatureSettingsResponse.value = homeRepository.getDeviceFeaturesSettings(deviceId)
    }

    fun deleteDevice(deviceId: String) = viewModelScope.launch {
        _deleteDeviceResponse.value = Resource.Loading
        _deleteDeviceResponse.value = homeRepository.deleteDevice(deviceId)
    }

    fun updateDeviceName(bodyUpdateDeviceName: BodyUpdateDeviceName) = viewModelScope.launch {
        _updateDeviceNameResponse.value = Resource.Loading
        _updateDeviceNameResponse.value = homeRepository.updateDeviceName(bodyUpdateDeviceName)
    }

    fun updateSwitchName(bodyUpdateSwitchName: BodyUpdateSwitchName) = viewModelScope.launch {
        _updateSwitchNameResponse.value = Resource.Loading
        _updateSwitchNameResponse.value = homeRepository.updateSwitchName(bodyUpdateSwitchName)
    }

    fun customizationLock(bodyCustomizationLock: BodyCustomizationLock) = viewModelScope.launch {
        _customizationLockResponse.value = Resource.Loading
        _customizationLockResponse.value = homeRepository.customizationLock(bodyCustomizationLock)
    }

    fun iconList() = viewModelScope.launch {
        _iconListResponse.value = Resource.Loading
        _iconListResponse.value = homeRepository.getIconList()
    }

    fun updateSwitchIcon(bodyUpdateSwitchIcon: BodyUpdateSwitchIcon) = viewModelScope.launch {
        _updateSwitchIconResponse.value = Resource.Loading
        _updateSwitchIconResponse.value = homeRepository.updateSwitchIcon(bodyUpdateSwitchIcon)
    }

    fun getControl() = viewModelScope.launch {
        _getControlResponse.value = Resource.Loading
        _getControlResponse.value = homeRepository.getControl()
    }

    fun imageUpload(deviceId: RequestBody, image: MutableList<MultipartBody.Part>) = viewModelScope.launch {
        _imageUploadResponse.value = Resource.Loading
        _imageUploadResponse.value = homeRepository.imageUpload(deviceId,image)
    }

    fun deleteImage(deviceId: String) = viewModelScope.launch {
        _deleteImageResponse.value = Resource.Loading
        _deleteImageResponse.value = homeRepository.deleteImage(deviceId)
    }

    fun getScene(bodyGetScene: BodyGetScene) = viewModelScope.launch {
        _getSceneResponse.value = Resource.Loading
        _getSceneResponse.value = homeRepository.getScene(bodyGetScene)
    }

    fun addScene(bodyAddScene: BodyAddScene) = viewModelScope.launch {
        _addSceneResponse.value = Resource.Loading
        _addSceneResponse.value = homeRepository.addScene(bodyAddScene)
    }

    //
    //endregion
    //


    //
    //region ownership transfer
    //

    private val _getOwnershipResponse: MutableLiveData<Resource<OwnershipResponse>> = MutableLiveData()
    val getOwnershipResponse: LiveData<Resource<OwnershipResponse>>
        get() = _getOwnershipResponse

    private val _transferOwnershipResponse: MutableLiveData<Resource<OwnershipResponse>> = MutableLiveData()
    val transferOwnershipResponse: LiveData<Resource<OwnershipResponse>>
        get() = _transferOwnershipResponse

    fun getOwnership() = viewModelScope.launch {
        _getOwnershipResponse.value = Resource.Loading
        _getOwnershipResponse.value = homeRepository.getOwnership()
    }

    fun transferOwnership(bodyOwnership: BodyOwnership) = viewModelScope.launch {
        _transferOwnershipResponse.value = Resource.Loading
        _transferOwnershipResponse.value = homeRepository.transferOwnership(bodyOwnership)
    }

    //
    //endregion
    //
}