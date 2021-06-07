package com.dellainfotech.smartTouch.mqtt

import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.*
import com.amazonaws.regions.Region
import com.amazonaws.services.iot.AWSIotClient
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult
import com.dellainfotech.smartTouch.AppDelegate.Companion.instance
import com.dellainfotech.smartTouch.common.utils.Constants
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.*
import javax.inject.Singleton

@Singleton
object AwsMqttSingleton {

    val logTag = this::class.java.simpleName
    var clientId: String? = null
    var certificateId: String? = null

    var clientKeyStore: KeyStore? = null
    var credentialsProvider: CognitoCachingCredentialsProvider? = null
    var mqttManager: AWSIotMqttManager? = null
    var mqttStatus = MQTTConnectionStatus.DISCONNECTED

    var mIotAndroidClient: AWSIotClient? = null

    var keystorePath: String? = null
    var keystoreName: String? = null
    var keystorePassword: String? = null

    private fun connectAWS() {
        Log.d("Aws connection", "clientId = $clientId")

        if (clientKeyStore == null) {
            initializeMQTT()
        } else {
            try {

                mqttManager?.connect(credentialsProvider
                ) { status, throwable ->
                    when (status) {
                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting -> {
                            Log.e(logTag, "Connecting.", throwable)
                            mqttStatus = MQTTConnectionStatus.CONNECTING
                        }
                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected -> {
                            Log.e(logTag, "Connected.", throwable)
                            mqttStatus = MQTTConnectionStatus.CONNECTED

                            subscribe("test")
                        }

                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting -> {
                            Log.e(logTag, "Reconnecting error.", throwable)
                            mqttStatus = MQTTConnectionStatus.RECONNECTING
                        }

                        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost -> {
                            Log.e(logTag, "ConnectionLost error.", throwable)
                            mqttStatus = MQTTConnectionStatus.CONNECTION_LOST
                        }
                        else -> {
                            Log.d(
                                logTag,
                                "Status = Disconnected "
                            )
                            mqttStatus = MQTTConnectionStatus.DISCONNECTED
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(logTag, "Connection error.", e)
            }
        }


    }

    fun initializeMQTT() {
        clientId = UUID.randomUUID().toString()

        credentialsProvider = CognitoCachingCredentialsProvider(
            instance, Constants.COGNITO_POOL_ID, Constants.MY_REGION
        )

        mqttManager = AWSIotMqttManager(clientId, Constants.CUSTOMER_SPECIFIC_ENDPOINT)
        mqttManager!!.keepAlive = 10

        mqttManager!!.mqttLastWillAndTestament = AWSIotMqttLastWillAndTestament(
            "my/lwt/topic",
            "Android client lost connection", AWSIotMqttQos.QOS0
        )

        mIotAndroidClient = AWSIotClient(credentialsProvider)
        mIotAndroidClient!!.setRegion(Region.getRegion(Constants.MY_REGION))

        keystorePath = instance.filesDir.path
        keystoreName = Constants.KEYSTORE_NAME
        keystorePassword = Constants.KEYSTORE_PASSWORD
        certificateId = Constants.CERTIFICATE_ID

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

                    val createKeysAndCertificateResult: CreateKeysAndCertificateResult =
                        mIotAndroidClient!!.createKeysAndCertificate(
                            createKeysAndCertificateRequest
                        )

                    Log.i(
                        logTag,
                        "Cert ID: " +
                                createKeysAndCertificateResult.certificateId +
                                " created."
                    )

                    AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                        certificateId,
                        createKeysAndCertificateResult.certificatePem,
                        createKeysAndCertificateResult.keyPair.privateKey,
                        keystorePath, keystoreName, keystorePassword
                    )

                    val policyAttachRequest =
                        AttachPrincipalPolicyRequest()
                    policyAttachRequest.policyName = Constants.AWS_IOT_POLICY_NAME
                    policyAttachRequest.principal = createKeysAndCertificateResult
                        .certificateArn
                    mIotAndroidClient!!.attachPrincipalPolicy(policyAttachRequest)

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
            mqttManager!!.subscribeToTopic(
                topic, AWSIotMqttQos.QOS0
            ) { topic, data ->
                val message = String(data, StandardCharsets.UTF_8)
                Log.d("ReceivedData", "$topic    $message")
            }
        } catch (e: Exception) {
            Log.e(logTag, "Subscription error.", e)
        }
    }

    fun publish(topic: String, msg: String) {
        try {
            if (isConnected()) mqttManager!!.publishString(msg, topic, AWSIotMqttQos.QOS0)
        } catch (e: Exception) {
            Log.e(logTag, "Publish error.", e)
        }
    }

    fun disconnectAws() {
        mqttManager!!.disconnect()
    }

    fun isConnected(): Boolean {
        return mqttStatus == MQTTConnectionStatus.CONNECTED
    }
}