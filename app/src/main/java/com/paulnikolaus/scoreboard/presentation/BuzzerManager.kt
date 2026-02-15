package com.paulnikolaus.scoreboard.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.paulnikolaus.scoreboard.R

class BuzzerManager(context: Context) {

    private val soundPool: SoundPool
    private var soundId: Int = 0
    private var isLoaded = false

    init {

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool.load(context, R.raw.buzzer, 1)

        // VERY IMPORTANT
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }
    }

    fun play() {
        if (!isLoaded) return

        soundPool.play(
            soundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    fun release() {
        soundPool.release()
    }
}
