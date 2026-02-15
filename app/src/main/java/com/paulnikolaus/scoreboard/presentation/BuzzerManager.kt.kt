package com.paulnikolaus.scoreboard.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.paulnikolaus.scoreboard.R

class BuzzerManager(context: Context) {

    private val soundPool: SoundPool
    private val soundId: Int

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
    }

    fun play() {
        soundPool.play(
            soundId,
            1f, // left volume
            1f, // right volume
            1,
            0,
            1f
        )
    }

    fun release() {
        soundPool.release()
    }
}
