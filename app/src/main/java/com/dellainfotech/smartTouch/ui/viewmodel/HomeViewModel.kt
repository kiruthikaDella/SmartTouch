package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.*
import com.dellainfotech.smartTouch.api.model.*
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _logoutResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val logoutResponse = _logoutResponse.asSharedFlow()

    private val _roomTypeResponse = MutableSharedFlow<Resource<RoomTypeResponse>>()
    val roomTypeResponse = _roomTypeResponse.asSharedFlow()

    private val _getRoomResponse = MutableSharedFlow<Resource<GetRoomResponse>>()
    val getRoomResponse = _getRoomResponse.asSharedFlow()

    private val _addRoomResponse = MutableSharedFlow<Resource<AddRoomResponse>>()
    val addRoomResponse = _addRoomResponse.asSharedFlow()

    private val _retainStateResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val retainStateResponse = _retainStateResponse.asSharedFlow()

    private val _updateRoomResponse = MutableSharedFlow<Resource<UpdateRoomResponse>>()
    val updateRoomResponse = _updateRoomResponse.asSharedFlow()

    private val _deleteRoomResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val deleteRoomResponse = _deleteRoomResponse.asSharedFlow()

    private val _faqResponse = MutableSharedFlow<Resource<FAQResponse>>()
    val faqResponse = _faqResponse.asSharedFlow()

    private val _getUserProfileResponse = MutableSharedFlow<Resource<GetProfileResponse>>()
    val getUserProfileResponse = _getUserProfileResponse.asSharedFlow()

    private val _updateUserProfileResponse = MutableSharedFlow<Resource<GetProfileResponse>>()
    val updateUserProfileResponse = _updateUserProfileResponse.asSharedFlow()

    private val _changePasswordResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val changePasswordResponse = _changePasswordResponse.asSharedFlow()

    private val _updatePinStatusResponse = MutableSharedFlow<Resource<PinResponse>>()
    val updatePinStatusResponse = _updatePinStatusResponse.asSharedFlow()

    fun logout(
        bodyLogout: BodyLogout
    ) = viewModelScope.launch {
        _logoutResponse.emit(Resource.Loading)
        _logoutResponse.emit(homeRepository.logout(bodyLogout))
    }

    fun roomType() = viewModelScope.launch {
        _roomTypeResponse.emit(Resource.Loading)
        _roomTypeResponse.emit(homeRepository.roomType())
    }

    fun getRoom() = viewModelScope.launch {
        _getRoomResponse.emit(Resource.Loading)
        _getRoomResponse.emit(homeRepository.getRoom())
    }

    fun addRoom(bodyAddRoom: BodyAddRoom) = viewModelScope.launch {
        _addRoomResponse.emit(Resource.Loading)
        _addRoomResponse.emit(homeRepository.addRoom(bodyAddRoom))
    }

    fun updateRoom(bodyUpdateRoom: BodyUpdateRoom) = viewModelScope.launch {
        _updateRoomResponse.emit(Resource.Loading)
        _updateRoomResponse.emit(homeRepository.updateRoom(bodyUpdateRoom))
    }

    fun deleteRoom(roomId: String) = viewModelScope.launch {
        _deleteRoomResponse.emit(Resource.Loading)
        _deleteRoomResponse.emit(homeRepository.deleteRoom(roomId))
    }

    fun getFAQ() = viewModelScope.launch {
        _faqResponse.emit(Resource.Loading)
        _faqResponse.emit(homeRepository.getFAQ())
    }

    fun getUserProfile() = viewModelScope.launch {
        _getUserProfileResponse.emit(Resource.Loading)
        _getUserProfileResponse.emit(homeRepository.getUserProfile())
    }

    fun updateUserProfile(bodyUpdateUserProfile: BodyUpdateUserProfile) = viewModelScope.launch {
        _updateUserProfileResponse.emit(Resource.Loading)
        _updateUserProfileResponse.emit(homeRepository.updateUserProfile(bodyUpdateUserProfile))
    }

    fun changePassword(bodyChangePassword: BodyChangePassword) = viewModelScope.launch {
        _changePasswordResponse.emit(Resource.Loading)
        _changePasswordResponse.emit(homeRepository.changePassword(bodyChangePassword))
    }

    fun retainState(bodyRetainState: BodyRetainState) = viewModelScope.launch {
        _retainStateResponse.emit(Resource.Loading)
        _retainStateResponse.emit(homeRepository.retainState(bodyRetainState))
    }

    fun updatePinStatus(bodyPinStatus: BodyPinStatus) = viewModelScope.launch {
        _updatePinStatusResponse.emit(Resource.Loading)
        _updatePinStatusResponse.emit(homeRepository.updatePinStatus(bodyPinStatus))
    }


    //
    //region Device
    //

    private val _addDeviceResponse = MutableSharedFlow<Resource<AddDeviceResponse>>()
    val addDeviceResponse = _addDeviceResponse.asSharedFlow()

    private val _getDeviceResponse = MutableSharedFlow<Resource<GetDeviceResponse>>()
    val getDeviceResponse = _getDeviceResponse.asSharedFlow()

    private val _getDeviceCustomizationSettingsResponse =
        MutableSharedFlow<Resource<DeviceCustomizationResponse>>()
    val getDeviceCustomizationSettingsResponse =
        _getDeviceCustomizationSettingsResponse.asSharedFlow()

    private val _getDeviceFeatureSettingsResponse =
        MutableSharedFlow<Resource<DeviceFeatureResponse>>()
    val getDeviceFeatureSettingsResponse = _getDeviceFeatureSettingsResponse.asSharedFlow()

    private val _deleteDeviceResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val deleteDeviceResponse = _deleteDeviceResponse.asSharedFlow()

    private val _updateDeviceNameResponse = MutableSharedFlow<Resource<AddDeviceResponse>>()
    val updateDeviceNameResponse = _updateDeviceNameResponse.asSharedFlow()

    private val _updateSwitchNameResponse = MutableSharedFlow<Resource<UpdateSwitchNameResponse>>()
    val updateSwitchNameResponse = _updateSwitchNameResponse.asSharedFlow()

    private val _customizationLockResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val customizationLockResponse = _customizationLockResponse.asSharedFlow()

    private val _iconListResponse = MutableSharedFlow<Resource<IconListResponse>>()
    val iconListResponse = _iconListResponse.asSharedFlow()

    private val _updateSwitchIconResponse = MutableSharedFlow<Resource<UpdateSwitchIconResponse>>()
    val updateSwitchIconResponse = _updateSwitchIconResponse.asSharedFlow()

    private val _getControlResponse = MutableSharedFlow<Resource<ControlModeResponse>>()
    val getControlResponse = _getControlResponse.asSharedFlow()

    private val _imageUploadResponse = MutableSharedFlow<Resource<ImageUploadResponse>>()
    val imageUploadResponse = _imageUploadResponse.asSharedFlow()

    private val _deleteImageResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val deleteImageResponse = _deleteImageResponse.asSharedFlow()

    private val _getSceneResponse = MutableSharedFlow<Resource<GetSceneResponse>>()
    val getSceneResponse = _getSceneResponse.asSharedFlow()

    private val _addSceneResponse = MutableSharedFlow<Resource<AddSceneResponse>>()
    val addSceneResponse = _addSceneResponse.asSharedFlow()

    private val _updateSceneResponse = MutableSharedFlow<Resource<AddSceneResponse>>()
    val updateSceneResponse = _updateSceneResponse.asSharedFlow()

    private val _deleteSceneResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val deleteSceneResponse = _deleteSceneResponse.asSharedFlow()

    private val _deleteSceneDetailResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val deleteSceneDetailResponse = _deleteSceneDetailResponse.asSharedFlow()

    private val _factoryResetResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val factoryResetResponse = _factoryResetResponse.asSharedFlow()

    private val _factoryResetAllDeviceResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val factoryResetAllDeviceResponse = _factoryResetAllDeviceResponse.asSharedFlow()

    private val _updateSceneStatusResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val updateSceneStatusResponse = _updateSceneStatusResponse.asSharedFlow()

    fun addDevice(bodyAddDevice: BodyAddDevice) = viewModelScope.launch {
        _addDeviceResponse.emit(Resource.Loading)
        _addDeviceResponse.emit(homeRepository.addDevice(bodyAddDevice))
    }

    fun getDevice(roomId: String) = viewModelScope.launch {
        _getDeviceResponse.emit(Resource.Loading)
        _getDeviceResponse.emit(homeRepository.getDeviceData(roomId))
    }

    fun getDeviceCustomization(deviceId: String) = viewModelScope.launch {
        _getDeviceCustomizationSettingsResponse.emit(Resource.Loading)
        _getDeviceCustomizationSettingsResponse.emit(
            homeRepository.getDeviceCustomizationSettings(
                deviceId
            )
        )
    }

    fun getDeviceFeatures(deviceId: String) = viewModelScope.launch {
        _getDeviceFeatureSettingsResponse.emit(Resource.Loading)
        _getDeviceFeatureSettingsResponse.emit(homeRepository.getDeviceFeaturesSettings(deviceId))
    }

    fun deleteDevice(productGroup: String, roomId: String, deviceId: String) =
        viewModelScope.launch {
            _deleteDeviceResponse.emit(Resource.Loading)
            _deleteDeviceResponse.emit(homeRepository.deleteDevice(productGroup, roomId, deviceId))
        }

    fun updateDeviceName(bodyUpdateDeviceName: BodyUpdateDeviceName) = viewModelScope.launch {
        _updateDeviceNameResponse.emit(Resource.Loading)
        _updateDeviceNameResponse.emit(homeRepository.updateDeviceName(bodyUpdateDeviceName))
    }

    fun updateSwitchName(bodyUpdateSwitchName: BodyUpdateSwitchName) = viewModelScope.launch {
        _updateSwitchNameResponse.emit(Resource.Loading)
        _updateSwitchNameResponse.emit(homeRepository.updateSwitchName(bodyUpdateSwitchName))
    }

    fun customizationLock(bodyCustomizationLock: BodyCustomizationLock) = viewModelScope.launch {
        _customizationLockResponse.emit(Resource.Loading)
        _customizationLockResponse.emit(homeRepository.customizationLock(bodyCustomizationLock))
    }

    fun iconList() = viewModelScope.launch {
        _iconListResponse.emit(Resource.Loading)
        _iconListResponse.emit(homeRepository.getIconList())
    }

    fun updateSwitchIcon(bodyUpdateSwitchIcon: BodyUpdateSwitchIcon) = viewModelScope.launch {
        _updateSwitchIconResponse.emit(Resource.Loading)
        _updateSwitchIconResponse.emit(homeRepository.updateSwitchIcon(bodyUpdateSwitchIcon))
    }

    fun getControl() = viewModelScope.launch {
        _getControlResponse.emit(Resource.Loading)
        _getControlResponse.emit(homeRepository.getControl())
    }

    fun imageUpload(deviceId: RequestBody, image: MutableList<MultipartBody.Part>) =
        viewModelScope.launch {
            _imageUploadResponse.emit(Resource.Loading)
            _imageUploadResponse.emit(homeRepository.imageUpload(deviceId, image))
        }

    fun deleteImage(deviceId: String) = viewModelScope.launch {
        _deleteImageResponse.emit(Resource.Loading)
        _deleteImageResponse.emit(homeRepository.deleteImage(deviceId))
    }

    fun getScene(bodyGetScene: BodyGetScene) = viewModelScope.launch {
        _getSceneResponse.emit(Resource.Loading)
        _getSceneResponse.emit(homeRepository.getScene(bodyGetScene))
    }

    fun addScene(bodyAddScene: BodyAddScene) = viewModelScope.launch {
        _addSceneResponse.emit(Resource.Loading)
        _addSceneResponse.emit(homeRepository.addScene(bodyAddScene))
    }

    fun updateScene(bodyUpdateScene: BodyUpdateScene) = viewModelScope.launch {
        _updateSceneResponse.emit(Resource.Loading)
        _updateSceneResponse.emit(homeRepository.updateScene(bodyUpdateScene))
    }

    fun deleteScene(sceneId: String) = viewModelScope.launch {
        _deleteSceneResponse.emit(Resource.Loading)
        _deleteSceneResponse.emit(homeRepository.deleteScene(sceneId))
    }

    fun deleteSceneDetail(sceneId: String, sceneDetailId: String) = viewModelScope.launch {
        _deleteSceneDetailResponse.emit(Resource.Loading)
        _deleteSceneDetailResponse.emit(homeRepository.deleteSceneDetail(sceneId, sceneDetailId))
    }

    fun factoryReset(bodyFactoryReset: BodyFactoryReset) = viewModelScope.launch {
        _factoryResetResponse.emit(Resource.Loading)
        _factoryResetResponse.emit(homeRepository.factoryReset(bodyFactoryReset))
    }

    fun factoryResetAllDevice() = viewModelScope.launch {
        _factoryResetAllDeviceResponse.emit(Resource.Loading)
        _factoryResetAllDeviceResponse.emit(homeRepository.factoryResetAllDevice())
    }

    fun updateSceneStatus(sceneId: String, bodyUpdateSceneStatus: BodyUpdateSceneStatus) =
        viewModelScope.launch {
            _updateSceneStatusResponse.emit(Resource.Loading)
            _updateSceneStatusResponse.emit(homeRepository.updateSceneStatus(sceneId, bodyUpdateSceneStatus))
        }

    //
    //endregion
    //


    //
    //region ownership transfer
    //

    private val _getOwnershipResponse = MutableSharedFlow<Resource<OwnershipResponse>>()
    val getOwnershipResponse = _getOwnershipResponse.asSharedFlow()

    private val _transferOwnershipResponse = MutableSharedFlow<Resource<OwnershipResponse>>()
    val transferOwnershipResponse = _transferOwnershipResponse.asSharedFlow()

    private val _cancelOwnershipResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val cancelOwnershipResponse = _cancelOwnershipResponse.asSharedFlow()

    fun getOwnership() = viewModelScope.launch {
        _getOwnershipResponse.emit(Resource.Loading)
        _getOwnershipResponse.emit(homeRepository.getOwnership())
    }

    fun transferOwnership(bodyOwnership: BodyOwnership) = viewModelScope.launch {
        _transferOwnershipResponse.emit(Resource.Loading)
        _transferOwnershipResponse.emit(homeRepository.transferOwnership(bodyOwnership))
    }

    fun cancelOwnership(ownershipId: String) = viewModelScope.launch {
        _cancelOwnershipResponse.emit(Resource.Loading)
        _cancelOwnershipResponse.emit(homeRepository.cancelTransferOwnership(ownershipId))
    }


    //
    //endregion
    //
}