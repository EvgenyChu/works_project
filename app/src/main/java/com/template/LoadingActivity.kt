package com.template

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
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

        val savedHost = PrefManager.getUrl()
        if (savedHost == null && Utils.isNetworkAvailable()) {
            fetchConfig()
        } else if(savedHost != null && Utils.isNetworkAvailable()){
            openCustomTab()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun fetchConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val host: String = remoteConfig.getString("check_link")
                    val url =
                        "$host/?packageid=${this.packageName}&usserid=${UUID.randomUUID()}&getz=${TimeZone.getDefault().id}&getr=utm_source=google-play&utm_medium=organic"

                    PrefManager.setUrl(url)
                    openCustomTab()

                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
    }

    private fun openCustomTab(){
        val url = PrefManager.getUrl()
        val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}