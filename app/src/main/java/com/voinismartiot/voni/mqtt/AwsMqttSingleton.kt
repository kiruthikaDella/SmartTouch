package com.voinismartiot.voni.mqtt

import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.*
import com.amazonaws.regions.Region
import com.amazonaws.services.iot.AWSIotClient
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult
import com.voinismartiot.voni.AppDelegate.Companion.instance
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.*
import javax.inject.Singleton

@Singleton
object AwsMqttSingleton {

    private val logTag = this::class.java.simpleName
    private var clientId: String? = null
    private var certificateId: String? = null

    var clientKeyStore: KeyStore? = null
    var credentialsProvider: CognitoCachingCredentialsProvider? = null
    var mqttManager: AWSIotMqttManager? = null
    private var mqttStatus = MQTTConnectionStatus.DISCONNECTED

    var mIotAndroidClient: AWSIotClient? = null

    private var keystorePath: String? = null
    private var keystoreName: String? = null
    private var keystorePassword: String? = null

    private var isInternetConnected = false

    fun connectAWS() {
        Log.d("Aws connection", "clientId = $clientId")

        if (clientKeyStore == null) {
            initializeMQTT()
        } else {
            try {

                mqttManager?.connect(
                    credentialsProvider
                ) { status, throwable ->
                    when (status) {
                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting -> {
                            Log.e(logTag, "Connecting.", throwable)
                            mqttStatus = MQTTConnectionStatus.CONNECTING
                            NotifyManager.getMQTTConnectionInfo()
                                .onNext(MQTTConnectionStatus.CONNECTING)
                        }
                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected -> {
                            Log.e(logTag, "Connected.", throwable)
                            mqttStatus = MQTTConnectionStatus.CONNECTED
                            updateObserver(true)
                            NotifyManager.getMQTTConnectionInfo()
                                .onNext(MQTTConnectionStatus.CONNECTED)
                        }

                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting -> {
                            Log.e(logTag, "Reconnecting error.", throwable)
                            mqttStatus = MQTTConnectionStatus.RECONNECTING
                            updateObserver(false)
                            NotifyManager.getMQTTConnectionInfo()
                                .onNext(MQTTConnectionStatus.RECONNECTING)
                        }

                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost -> {
                            Log.e(logTag, "ConnectionLost error.", throwable)
                            mqttStatus = MQTTConnectionStatus.CONNECTION_LOST
                            NotifyManager.getMQTTConnectionInfo()
                                .onNext(MQTTConnectionStatus.CONNECTION_LOST)
                            connectAWS()
                        }
                        else -> {
                            Log.d(
                                logTag,
                                "Status = Disconnected "
                            )
                            mqttStatus = MQTTConnectionStatus.DISCONNECTED
                            NotifyManager.getMQTTConnectionInfo()
                                .onNext(MQTTConnectionStatus.DISCONNECTED)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(logTag, "Connection error.", e)
            }
        }


    }

    private fun updateObserver(isConnected: Boolean) {
        Log.e(
            logTag,
            " updateObserver isConnected $isConnected isInternetConnected $isInternetConnected "
        )
        if (isInternetConnected != isConnected) {
            isInternetConnected = isConnected
            NotifyManager.internetInfo.postValue(isConnected)
        }
    }

    fun initializeMQTT() {
        clientId = UUID.randomUUID().toString()

        credentialsProvider = CognitoCachingCredentialsProvider(
            instance, MQTTConstants.COGNITO_POOL_ID, MQTTConstants.MY_REGION
        )

        mqttManager = AWSIotMqttManager(clientId, MQTTConstants.CUSTOMER_SPECIFIC_ENDPOINT)
        mqttManager?.keepAlive = 10

        mqttManager?.mqttLastWillAndTestament = AWSIotMqttLastWillAndTestament(
            "my/lwt/topic",
            "Android client lost connection", AWSIotMqttQos.QOS0
        )

        mIotAndroidClient = AWSIotClient(credentialsProvider)
        mIotAndroidClient?.setRegion(Region.getRegion(MQTTConstants.MY_REGION))

        keystorePath = instance.filesDir.path
        keystoreName = MQTTConstants.KEYSTORE_NAME
        keystorePassword = MQTTConstants.KEYSTORE_PASSWORD
        certificateId = MQTTConstants.CERTIFICATE_ID

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(
                        certificateId, keystorePath, keystoreName, keystorePassword
                    )
                ) {
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(
                        certificateId,
                        keystorePath,
                        keystoreName,
                        keystorePassword
                    )
                } else {
                    Log.i(
                        logTag,
                        "Key/cert $certificateId not found in keystore."
                    )
                }
            } else {
                Log.i(
                    logTag,
                    "Keystore $keystorePath/$keystoreName not found."
                )
            }
        } catch (e: Exception) {
            Log.e(
                logTag,
                "initializeMqtt:-> An error occurred retrieving cert/key from keystore.",
            )
        }

        if (clientKeyStore == null) {
            Log.i(
                logTag,
                "Cert/key was not found in keystore - creating new key and certificate."
            )

            Thread {
                try {
                    val createKeysAndCertificateRequest =
                        CreateKeysAndCertificateRequest()
                    createKeysAndCertificateRequest.isSetAsActive = true

                    val createKeysAndCertificateResult: CreateKeysAndCertificateResult? =
                        mIotAndroidClient?.createKeysAndCertificate(
                            createKeysAndCertificateRequest
                        )

                    Log.i(
                        logTag,
                        "Cert ID: " +
                                createKeysAndCertificateResult?.certificateId +
                                " created."
                    )

                    AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                        certificateId,
                        createKeysAndCertificateResult?.certificatePem,
                        createKeysAndCertificateResult?.keyPair?.privateKey,
                        keystorePath, keystoreName, keystorePassword
                    )

                    val policyAttachRequest =
                        AttachPrincipalPolicyRequest()
                    policyAttachRequest.policyName = MQTTConstants.AWS_IOT_POLICY_NAME
                    policyAttachRequest.principal = createKeysAndCertificateResult
                        ?.certificateArn
                    mIotAndroidClient?.attachPrincipalPolicy(policyAttachRequest)

                    connectAWS()
                } catch (e: Exception) {
                    Log.e(
                        logTag,
                        "Exception occurred when generating new private key and certificate.",
                        e
                    )
                }
            }.start()
        } else {
            connectAWS()
        }
    }

    fun subscribe(topic: String) {
        Log.d(logTag, "topic = $topic")
        try {
            mqttManager?.subscribeToTopic(
                topic, AWSIotMqttQos.QOS0
            ) { topicName, data ->
                val message = String(data, StandardCharsets.UTF_8)
                Log.d("ReceivedData", "$topicName    $message")
            }
        } catch (e: Exception) {
            Log.e(logTag, "Subscription error.", e)
        }
    }

    fun unsubscribe(topic: String) {
        Log.d(logTag, "topic = $topic")
        try {
            mqttManager?.unsubscribeTopic(topic)
        } catch (e: Exception) {
            Log.e(logTag, "Subscription error.", e)
        }
    }

    fun publish(topic: String, msg: String) {
        try {
            if (isConnected()) {
                Log.i(logTag, " topic $topic msg $msg")
                mqttManager?.publishString(msg, topic, AWSIotMqttQos.QOS0)
            }
        } catch (e: Exception) {
            Log.e(logTag, "Publish error.", e)
        }
    }

    fun disconnectAws() {
        mqttManager?.disconnect()
    }

    fun isConnected(): Boolean {
        return mqttStatus == MQTTConnectionStatus.CONNECTED
    }
}