package com.paulnikolaus.scoreboard.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.paulnikolaus.scoreboard.R

/**
 * Manages the playback of the buzzer sound effect using SoundPool.
 * SoundPool is ideal for short audio clips like buzzers as it has low latency.
 */
class BuzzerManager(context: Context) {

    private val soundPool: SoundPool
    private var soundId: Int = 0
    private var isLoaded = false

    init {
        // Configure audio attributes for the sound playback
        val audioAttributes = AudioAttributes.Builder()
            // USAGE_MEDIA is general-purpose, but USAGE_ASSISTANCE_SONIFICATION
            // could also be used for UI feedback sounds.
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        // Initialize SoundPool with a single stream (only one buzzer sound at a time)
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load the buzzer sound from the raw resources directory
        soundId = soundPool.load(context, R.raw.buzzer, 1)

        // Ensure the sound is fully loaded into memory before attempting to play it
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) { // Status 0 indicates success
                isLoaded = true
            }
        }
    }

    /**
     * Triggers the buzzer sound effect.
     */
    fun play() {
        // Prevent crashes or errors if the sound hasn't finished loading yet
        if (!isLoaded) return

        soundPool.play(
            soundId,
            1f,      // Left volume (0.0 to 1.0)
            1f,      // Right volume (0.0 to 1.0)
            1,       // Priority (0 = lowest)
            0,       // Loop (0 = no loop, -1 = loop forever)
            1f       // Playback rate (1.0 = normal speed)
        )
    }

    /**
     * Cleans up resources used by SoundPool when the manager is no longer needed.
     * Should be called in onCleared or a similar lifecycle cleanup method.
     */
    fun release() {
        soundPool.release()
    }
}