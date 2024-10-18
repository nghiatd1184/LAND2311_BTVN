package com.nghiatd.mixic.ui.home

import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.nghiatd.mixic.databinding.FragmentEqualizerBinding
import com.nghiatd.mixic.service.MusicService

class EqualizerFragment : Fragment() {

    private lateinit var binding: FragmentEqualizerBinding
    private lateinit var equalizer: Equalizer
    private lateinit var bassBoost: BassBoost
    private lateinit var virtualizer: Virtualizer
    private var service: MusicService? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        service = (parentFragment as HomeFragment).getMusicService()
        binding = FragmentEqualizerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEqualizer()
        setupBassBoost()
        setupSurroundSound()
        setupToggle()
        setupTemplates()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.imgBack.setOnClickListener {
            saveEqualizerSettings()
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveEqualizerSettings() {
        val sharedPreferences = requireContext().getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bands = arrayOf(binding.seekBar60Hz, binding.seekBar230Hz, binding.seekBar910Hz, binding.seekBar3kHz, binding.seekBar14kHz)
        for (i in bands.indices) {
            val bandIndex = i.toShort()
            val currentLevel = equalizer.getBandLevel(bandIndex)
            editor.putInt("BandLevel_$i", currentLevel.toInt())
        }
        editor.putInt("BassBoostStrength", bassBoost.roundedStrength.toInt())
        editor.putInt("SurroundSoundStrength", virtualizer.roundedStrength.toInt())
        editor.apply()
    }

    private fun setupEqualizer() {
        val audioSessionId = service?.getAudioSessionId() ?: return
        equalizer = Equalizer(0, audioSessionId)
        equalizer.enabled = true

        val minEQLevel = equalizer.bandLevelRange[0]
        val maxEQLevel = equalizer.bandLevelRange[1]

        val bands = arrayOf(binding.seekBar60Hz, binding.seekBar230Hz, binding.seekBar910Hz, binding.seekBar3kHz, binding.seekBar14kHz)
        for (i in bands.indices) {
            val bandIndex = i.toShort()
            bands[i].max = maxEQLevel - minEQLevel
            bands[i].progress = equalizer.getBandLevel(bandIndex) - minEQLevel

            bands[i].setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        equalizer.setBandLevel(bandIndex, (progress + minEQLevel).toShort())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun setupBassBoost() {
        val audioSessionId = service?.getAudioSessionId() ?: return
        bassBoost = BassBoost(0, audioSessionId).apply {
            enabled = true
        }

        binding.seekBarBassBoost.max = 1000
        binding.seekBarBassBoost.progress = bassBoost.roundedStrength.toInt()

        binding.seekBarBassBoost.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    bassBoost.setStrength(progress.toShort())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupSurroundSound() {
        val audioSessionId = service?.getAudioSessionId() ?: return
        virtualizer = Virtualizer(0, audioSessionId).apply {
            enabled = true
        }

        binding.seekBarSurroundSound.max = 1000
        binding.seekBarSurroundSound.progress = virtualizer.roundedStrength.toInt()

        binding.seekBarSurroundSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    virtualizer.setStrength(progress.toShort())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupToggle() {
        val sharedPreferences = requireContext().getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val isEnable = sharedPreferences.getBoolean("EqualizerState", false)
        binding.switchEqualizer.isChecked = isEnable
        equalizer.enabled = isEnable
        bassBoost.enabled = isEnable
        virtualizer.enabled = isEnable

        binding.switchEqualizer.setOnCheckedChangeListener { _, isChecked ->
            equalizer.enabled = isChecked
            bassBoost.enabled = isChecked
            virtualizer.enabled = isChecked

            editor.putBoolean("EqualizerState", isChecked)
            editor.apply()
        }
    }

    private fun setupTemplates() {
        binding.btnClassical.setOnClickListener { applyTemplate(intArrayOf(1500, 2100, 1800, 1500, 2100)) }
        binding.btnPop.setOnClickListener { applyTemplate(intArrayOf(2700, 2100, 2100, 1800, 2400)) }
        binding.btnEdm.setOnClickListener { applyTemplate(intArrayOf(3000, 2400, 1500, 2100, 2400)) }
        binding.btnJazz.setOnClickListener { applyTemplate(intArrayOf(1800, 2100, 1800, 1500, 2100)) }
    }

    private fun applyTemplate(levels: IntArray) {
        val minEQLevel = equalizer.bandLevelRange[0]
        val maxEQLevel = equalizer.bandLevelRange[1]
        val bands = arrayOf(binding.seekBar60Hz, binding.seekBar230Hz, binding.seekBar910Hz, binding.seekBar3kHz, binding.seekBar14kHz)

        for (i in bands.indices) {
            val bandIndex = i.toShort()
            val adjustedLevel = levels[i] + minEQLevel
            val clampedLevel = adjustedLevel.coerceIn(minEQLevel.toInt(), maxEQLevel.toInt())

            bands[i].progress = clampedLevel - minEQLevel
            equalizer.setBandLevel(bandIndex, clampedLevel.toShort())
        }
    }

}