package com.nghiatd.mixic.ui.home

import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.api.RetrofitExtension
import com.nghiatd.mixic.data.model.LyricsResponse
import com.nghiatd.mixic.databinding.FragmentPlayingSongBinding
import com.nghiatd.mixic.receiver.OnSongCompletionReceiver
import com.nghiatd.mixic.service.MusicService
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PlayingSongFragment : Fragment(), OnSongCompletionReceiver.SongCompletionListener {

    private lateinit var binding: FragmentPlayingSongBinding
    private var service: MusicService? = null
    private lateinit var textNotify: String
    private var updateJobs: Job? = null
    private lateinit var songCompletionReceiver: OnSongCompletionReceiver
    private val isApi33OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        songCompletionReceiver = OnSongCompletionReceiver(this)
        val intentFilter = IntentFilter("com.nghiatd.mixic.SONG_COMPLETED")
        if (isApi33OrHigher) {
            requireActivity().registerReceiver(songCompletionReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireActivity().registerReceiver(songCompletionReceiver, intentFilter)
        }
        service = (parentFragment as HomeFragment).getMusicService()
        binding = FragmentPlayingSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initView() {
        setLyric()
        initShuffleUi()
        initMuteUi()
        initRepeatUi()
        updateUiOnChangeSong()
        updateSeekBarProgress()
    }

    private fun initMuteUi() {
        val isMute = service?.isMute?.value
        val btnMuteRes = if (isMute == true) R.drawable.icon_mute else R.drawable.icon_mute_off
        Glide.with(binding.btnMute)
            .load(btnMuteRes)
            .into(binding.btnMute)
    }

    private fun initRepeatUi() {
        val repeatMode = service?.repeatMode?.value
        when (repeatMode) {
            MusicService.REPEAT_MODE_OFF -> {
                val btnRepeatRes = R.drawable.icon_repeat_off
                Glide.with(binding.btnRepeat)
                    .load(btnRepeatRes)
                    .into(binding.btnRepeat)
            }

            MusicService.REPEAT_MODE_ONE -> {
                val btnRepeatRes = R.drawable.icon_repeat_one
                Glide.with(binding.btnRepeat)
                    .load(btnRepeatRes)
                    .into(binding.btnRepeat)
            }

            else -> {
                val btnRepeatRes = R.drawable.icon_repeat_all
                Glide.with(binding.btnRepeat)
                    .load(btnRepeatRes)
                    .into(binding.btnRepeat)
            }
        }
    }

    private fun initShuffleUi() {
        val isShuffle = service?.isShuffle?.value
        val btnShuffleRes =
            if (isShuffle == true) R.drawable.icon_shuffle_on else R.drawable.icon_shuffle_off
        Glide.with(binding.btnShuffle)
            .load(btnShuffleRes)
            .into(binding.btnShuffle)
    }

    private fun initClick() {
        binding.apply {
            imgDownCollapse.setOnClickListener {
                parentFragmentManager.popBackStack()
                val minimizedLayout =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)
                minimizedLayout?.visibility = View.VISIBLE
                val bottomNav =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.bottom_nav)
                bottomNav?.visibility = View.VISIBLE
                updateJobs?.cancel()
            }

            btnPlayPause.setOnClickListener {
                val song = service?.currentPlaying?.value?.second
                val imgRes =
                    if (service?.isPlayingFlow?.value == true) R.drawable.icon_play_reverse else R.drawable.icon_pause_reverse
                Glide.with(btnPlayPause)
                    .load(imgRes)
                    .into(btnPlayPause)
                val btnPlayPause =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)
                        ?.findViewById<ImageView>(R.id.btn_play_pause)
                val imgResMinimized =
                    if (service?.isPlayingFlow?.value == true) R.drawable.icon_play else R.drawable.icon_pause
                Glide.with(btnPlayPause!!)
                    .load(imgResMinimized)
                    .into(btnPlayPause)

                if (service?.isPlayingFlow?.value == true) updateJobs?.cancel() else updateSeekBarProgress()
                service?.playPause(song)
                minimizedSetViewOnCommand()
            }

            btnNext.setOnClickListener {
                service?.playNext()
                minimizedSetViewOnCommand()
                updateUiOnChangeSong()
                binding.seekBar.progress = 0
            }

            btnPrevious.setOnClickListener {
                service?.playPrev()
                minimizedSetViewOnCommand()
                updateUiOnChangeSong()
                binding.seekBar.progress = 0
            }

            btnMute.setOnClickListener {
                val btnMuteRes =
                    if (service?.isMute?.value == true) R.drawable.icon_mute_off else R.drawable.icon_mute
                Glide.with(btnMute)
                    .load(btnMuteRes)
                    .into(btnMute)
                service?.toggleMute()
            }

            btnRepeat.setOnClickListener {
                when (service?.repeatMode?.value) {
                    MusicService.REPEAT_MODE_OFF -> {
                        val btnRepeatRes = R.drawable.icon_repeat_one
                        Glide.with(btnRepeat)
                            .load(btnRepeatRes)
                            .into(btnRepeat)
                        service?.setRepeatMode(MusicService.REPEAT_MODE_ONE)
                        textNotify = "Repeat ONE!"
                    }

                    MusicService.REPEAT_MODE_ONE -> {
                        val btnRepeatRes = R.drawable.icon_repeat_all
                        Glide.with(btnRepeat)
                            .load(btnRepeatRes)
                            .into(btnRepeat)
                        service?.setRepeatMode(MusicService.REPEAT_MODE_ALL)
                        textNotify = "Repeat ALL!"
                    }

                    else -> {
                        val btnRepeatRes = R.drawable.icon_repeat_off
                        Glide.with(btnRepeat)
                            .load(btnRepeatRes)
                            .into(btnRepeat)
                        service?.setRepeatMode(MusicService.REPEAT_MODE_OFF)
                        textNotify = "Repeat OFF!"
                    }
                }
                Toast.makeText(requireContext(), textNotify, Toast.LENGTH_SHORT).show()
            }

            btnShuffle.setOnClickListener {
                val btnShuffleRes =
                    if (service?.isShuffle?.value == true) R.drawable.icon_shuffle_off else R.drawable.icon_shuffle_on
                textNotify =
                    if (service?.isShuffle?.value == true) "Shuffle OFF!" else "Shuffle ON!"
                Glide.with(btnShuffle)
                    .load(btnShuffleRes)
                    .into(btnShuffle)
                service?.setShuffleMode(!service!!.isShuffle.value)
                Toast.makeText(requireContext(), textNotify, Toast.LENGTH_SHORT).show()
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        service?.seekTo(progress.toLong())
                        binding.tvCurrentTime.text = convertMillisToTime(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    service?.pause()
                    updateJobs?.cancel()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (service?.isPlayingFlow?.value == true) {
                        service?.resume()
                        updateSeekBarProgress()
                    }
                }

            })

            tvShowLyrics.setOnClickListener {
                it.visibility = View.GONE
                scrollLyrics.visibility = View.VISIBLE
            }

            tvLyrics.setOnClickListener {
                scrollLyrics.visibility = View.GONE
                tvShowLyrics.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUiOnChangeSong() {
        val currentPlaying = service?.currentPlaying?.value?.second
        val isPlaying = service?.isPlayingFlow?.value
        val totalTime = convertMillisToTime(currentPlaying?.duration ?: 0)
        binding.tvLyrics.setText(R.string.blank)
        setLyric()
        binding.apply {
            scrollLyrics.visibility = View.GONE
            tvShowLyrics.visibility = View.VISIBLE
            tvTotalTime.text = totalTime
            tvName.text = currentPlaying?.name
            tvName.isSelected = true
            tvArtist.text = currentPlaying?.artist
            val artUri = Uri.parse(currentPlaying?.image)
            val btnPlayPauseRes =
                if (isPlaying == true) R.drawable.icon_pause_reverse else R.drawable.icon_play_reverse
            Glide.with(imgArt)
                .load(artUri)
                .into(imgArt)
            Glide.with(imgArtBackground)
                .load(artUri)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(30)))
                .into(imgArtBackground)
            Glide.with(btnPlayPause)
                .load(btnPlayPauseRes)
                .into(btnPlayPause)
            seekBar.apply {
                max = currentPlaying!!.duration.toInt()
                progress = service?.getCurrentPosition() ?: 0
            }
        }
    }

    private fun updateSeekBarProgress() {
        updateJobs = CoroutineScope(Dispatchers.Main).launch {
            while (service?.isPlayingFlow?.value == true) {
                binding.seekBar.progress = service?.getCurrentPosition() ?: 0
                binding.tvCurrentTime.text = convertMillisToTime(binding.seekBar.progress.toLong())
                delay(1000)
            }
        }
    }

    private fun convertMillisToTime(millis: Long): String {
        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    private fun minimizedSetViewOnCommand() {
        lifecycleScope.launch {
            service?.currentPlaying?.collect { currentPlaying ->
                val song = currentPlaying?.second
                val minimizedLayout =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)
                val tvName = minimizedLayout?.findViewById<TextView>(R.id.tv_name)
                val tvArtist = minimizedLayout?.findViewById<TextView>(R.id.tv_artist)
                val imgThumb = minimizedLayout?.findViewById<ImageView>(R.id.img_thumb)

                tvName?.text = song?.name
                tvArtist?.text = song?.artist
                val uri = Uri.parse(song?.image)
                Glide.with(imgThumb!!)
                    .load(uri)
                    .apply(RequestOptions().transform(RoundedCorners(15)))
                    .into(imgThumb)

            }
        }
    }

    private fun setLyric() {
        lifecycleScope.launch {
            try {
                binding.loading.visibility = View.VISIBLE
                val song = service?.currentPlaying?.value?.second
                val response: LyricsResponse = withContext(Dispatchers.IO) {
                    RetrofitExtension.OVH_LYRICS.getLyrics(song!!.artist, song.name).execute().body() ?: LyricsResponse("")
                }
                binding.loading.visibility = View.GONE
                binding.tvLyrics.visibility = View.VISIBLE
                binding.tvLyrics.text = response.lyrics
            } catch (e: Exception) {
                binding.loading.visibility = View.GONE
                binding.tvLyrics.visibility = View.VISIBLE
                binding.tvLyrics.setText(R.string.error_loading_lyrics)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateJobs?.cancel()
        requireActivity().unregisterReceiver(songCompletionReceiver)
    }

    override fun onSongCompletion() {
        updateUiOnChangeSong()
        minimizedSetViewOnCommand()
    }
}