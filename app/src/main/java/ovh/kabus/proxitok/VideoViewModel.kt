package ovh.kabus.proxitok

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext



class VideoViewModel(
    val player: Player,
    val dataStore: DataStore<Preferences>,
    val downloadManager: DownloadManager
): ViewModel() {

    init {
        player.prepare()
//        player.playWhenReady = true
    }

    var proxitokInstance = MutableStateFlow(runBlocking { getStringPref("instance_url") ?: "https://proxitok.kabus.ovh/" })
    var autoplay = MutableStateFlow(runBlocking { getBoolPref("autoplay") ?: true })

    var isVideoLoaded = MutableStateFlow(false)
    var isLoading = MutableStateFlow(false)
    var videoTitle = MutableStateFlow("Title")
    var videoAuthor = MutableStateFlow("Author")
    var videoViews = MutableStateFlow("0")
    var videoLikes = MutableStateFlow("0")
    var videoComments = MutableStateFlow("0")
    var videoShared = MutableStateFlow("0")
    private lateinit var videoUrl: Uri

    lateinit var helper: TiktokHelper

    @OptIn(DelicateCoroutinesApi::class)
    fun playVideo(videoUrl: Uri) {
        this.videoUrl = videoUrl
        GlobalScope.launch(Dispatchers.IO) {
            isLoading.value = true
            try {
                helper = TiktokHelper(proxitokInstance.value, videoUrl)
            }catch (e: Error) {
                setStringPref("instance_url", "https://proxitok.kabus.ovh/")
                playVideo(videoUrl)
                return@launch
            }
            withContext(Dispatchers.Main) {
                player.setMediaItem(MediaItem.fromUri(helper.getStreamUrl()))
                videoTitle.value = helper.getVideoTitle()
                videoAuthor.value = helper.getVideoAuthor()
                videoViews.value = helper.getVideoViews()
                videoLikes.value = helper.getVideoLikes()
                videoComments.value = helper.getVideoComments()
                videoShared.value = helper.getVideoShares()
                if(getBoolPref("autoplay") != false) player.play()
                isVideoLoaded.value = true
                isLoading.value = false
            }
        }
    }

    var videoComponentAspectRatio = MutableStateFlow(9/16f)

    fun setVideoComponentAspectRatio(aspectRatio: Float) {
        videoComponentAspectRatio.value = aspectRatio
    }

    var shareMenuExpanded = MutableStateFlow(false)
    var downloadMenuExpanded = MutableStateFlow(false)

    fun getCurrentVideoUrl(): Uri {
        return helper.tiktokUrl
    }



    suspend fun setStringPref(pref: String, value: String) {
        val instanceUrl = stringPreferencesKey(pref)
        dataStore.edit {
            it[instanceUrl] = value
        }
//        proxitokInstance.value = getInstance() ?: "https://proxitok.kabus.ovh/"
//        playVideo(videoUrl)
    }

    suspend fun setBoolPref(pref: String, value: Boolean) {
        val instanceUrl = booleanPreferencesKey(pref)
        dataStore.edit {
            it[instanceUrl] = value
        }
//        proxitokInstance.value = getInstance() ?: "https://proxitok.kabus.ovh/"
//        playVideo(videoUrl)
    }

    suspend fun getStringPref(pref: String): String? {
        val instanceUrl = stringPreferencesKey(pref)
        val prefs = dataStore.data.first();
        return prefs[instanceUrl]
    }

    suspend fun getBoolPref(pref: String): Boolean? {
        val instanceUrl = booleanPreferencesKey(pref)
        val prefs = dataStore.data.first();
        return prefs[instanceUrl]
    }

    fun downloadWithWatermark() {
        val url = helper.getVideoDownloadUrlWithWatermark()
        downloadUrl(url, helper.getVideoId()+"-watermark.mp4")
    }

    fun download() {
        val url = helper.getVideoDownloadUrl()
        downloadUrl(url, helper.getVideoId()+".mp4")
    }

    private fun downloadUrl(url: String, filename: String) {
        Log.e("laweta",url)
        val dmRequest = DownloadManager.Request(url.toUri())
            .setMimeType("video/mp4")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        Log.e("laweta","saasda")
        val dlId = downloadManager.enqueue(dmRequest)
        Log.e("laweta","hrtg")
    }


}