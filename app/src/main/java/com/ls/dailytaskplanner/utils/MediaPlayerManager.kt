package com.ls.dailytaskplanner.utils

import android.media.MediaPlayer
import com.ls.dailytaskplanner.App

object MediaPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun playRawFile(rawFileId: Int) {
        // Create a new MediaPlayer instance if it doesn't exist
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(App.mInstance, rawFileId)
        } else {
            // Reset the media player if it's already created
            mediaPlayer?.reset()
        }
        mediaPlayer?.start()
    }

    fun stopPlaying() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}