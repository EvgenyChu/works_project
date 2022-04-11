package com.template

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.template.ui.theme.Server_v1Theme
import com.template.ui.theme.StartScreen
import java.util.*


class LoadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            Server_v1Theme {
                StartScreen()
            }
        }
        val savedHost = PrefManager.getHost()
        val loading = PrefManager.getLoading()
        if (savedHost == null && !loading) {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            Log.e("LoadingActivity", "startFetch")
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this) { task ->
                    Log.e("LoadingActivity", "Config params updated: ${task.result}")
                    if (task.isSuccessful) {
                        val host: String = remoteConfig.getString("check_link")
                        PrefManager.setLoading(loading = true)
                        PrefManager.setHost(host)
                        Log.e("LoadingActivity", "Config params: $host")
                        val url = "$host/?packageid=${this.packageName}&usserid=${UUID.randomUUID()}&getz=TimeZone.getDefault().id&getr=utm_source=google-play&utm_medium=organic"
                            val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
                            customTabsIntent.launchUrl(this, Uri.parse(url))

                    } else { startActivity(Intent(this, MainActivity::class.java)) }
                }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}