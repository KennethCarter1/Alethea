package com.example.alethea

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var volumen: Float = 0.5f

    fun iniciar(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context.resources.openRawResourceFd(R.raw.fondo_musica))
                isLooping = true
                setVolume(volumen, volumen)
                prepare()
            }
        }
        mediaPlayer?.start()
    }

    fun pausar() {
        mediaPlayer?.pause()
    }

    fun reanudar() {
        mediaPlayer?.start()
    }

    fun estaReproduciendo(): Boolean = mediaPlayer?.isPlaying ?: false

    fun setVolumen(vol: Float) {
        volumen = vol.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(volumen, volumen)
    }

    fun getVolumen(): Float = volumen

    fun detener() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
