package ovh.kabus.proxitok

import android.util.Log
import androidx.media3.common.Player

class ExoEventListener(val viewModel: VideoViewModel, val player: Player): Player.Listener {
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.e("chuj",player.videoSize.width.toString())
        Log.e("chuj",player.videoSize.height.toString())
        Log.e("chuj",player.videoSize.pixelWidthHeightRatio.toString())
        val width = player.videoSize.width
        val height = player.videoSize.height
        val ratio = width.toFloat()/height.toFloat()
        viewModel.setVideoComponentAspectRatio(ratio)
    }
}