package com.nghiatd.mixic.ui.home

import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.nghiatd.mixic.MyApplication
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.api.RetrofitExtension
import com.nghiatd.mixic.data.model.LyricsResponse
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.databinding.FragmentPlayingSongBinding
import com.nghiatd.mixic.receiver.BroadcastReceiver
import com.nghiatd.mixic.service.MusicService
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.*
import java.io.File

class PlayingSongFragment : Fragment(), BroadcastReceiver.SongListener {

    private lateinit var binding: FragmentPlayingSongBinding
    private var service: MusicService? = null
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var updateJobs: Job? = null
    private val isAtLeast13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        service = (parentFragment as HomeFragment).getMusicService()
        broadcastReceiver = BroadcastReceiver(this)
        val intentFilter = IntentFilter(MyApplication.ACTION_SONG_START)
        if (isAtLeast13) {
            requireActivity().registerReceiver(
                broadcastReceiver,
                intentFilter,
                Context.RECEIVER_EXPORTED
            )
        } else {
            requireActivity().registerReceiver(broadcastReceiver, intentFilter)
        }
        binding = FragmentPlayingSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initView() {
        binding.root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up))
        setLyric()
        initShuffleUi()
        initMuteUi()
        initRepeatUi()
        initDownloadUi()
        updateUiOnChangeSong()
        updateSeekBarProgress()
    }

    private fun initMuteUi() {
        val isMute = service?.isMute?.value
        val btnMuteRes = if (isMute == true) R.drawable.icon_mute else R.drawable.icon_mute_off
        Glide.with(binding.btnMute)
            .load(btnMuteRes)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(binding.btnMute)
    }

    private fun initDownloadUi() {
        val song = service?.currentPlaying?.value
        try {
            song?.id?.toInt()
            binding.imgDownload.visibility = View.GONE
        } catch (e: Exception) {
            binding.imgDownload.visibility = View.VISIBLE
        }
    }

    private fun initRepeatUi() {
        val repeatMode = service?.repeatMode?.value
        val btnRepeatRes = when (repeatMode) {
            MusicService.REPEAT_MODE_OFF -> R.drawable.icon_repeat_off
            MusicService.REPEAT_MODE_ONE -> R.drawable.icon_repeat_one
            else -> R.drawable.icon_repeat_all
        }
        Glide.with(binding.btnRepeat)
            .load(btnRepeatRes)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(binding.btnRepeat)
    }

    private fun initShuffleUi() {
        val isShuffle = service?.isShuffle?.value
        val btnShuffleRes =
            if (isShuffle == true) R.drawable.icon_shuffle_on else R.drawable.icon_shuffle_off
        Glide.with(binding.btnShuffle)
            .load(btnShuffleRes)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(binding.btnShuffle)
    }

    private fun initClick() {
        binding.apply {
            imgDownCollapse.setOnClickListener {
                val container = (parentFragment as HomeFragment).view?.findViewById<View>(R.id.container)
                container?.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up))
                val minimizedLayout =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)
                minimizedLayout?.visibility = View.VISIBLE
                val bottomNav =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.bottom_nav)
                bottomNav?.visibility = View.VISIBLE
                val slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
                val animationSet = AnimationSet(true).apply {
                    addAnimation(slideUpAnimation)
                }
                minimizedLayout?.startAnimation(animationSet)
                bottomNav?.startAnimation(animationSet)

                updateJobs?.cancel()
                parentFragmentManager.popBackStack()
            }

            imgEqualizer.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, EqualizerFragment())
                    .addToBackStack(null)
                    .commit()
            }

            imgDownload.setOnClickListener {
                val song = service?.currentPlaying?.value
                downloadSong(song!!)
            }

            btnPlayPause.setOnClickListener {
                val song = service?.currentPlaying?.value
                val imgRes =
                    if (service?.isPlayingFlow?.value == true) R.drawable.icon_play_reverse else R.drawable.icon_pause_reverse
                Glide.with(btnPlayPause)
                    .load(imgRes)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(btnPlayPause)
                if (service?.isPlayingFlow?.value == true) updateJobs?.cancel() else updateSeekBarProgress()
                service?.playPause(song)
            }

            btnNext.setOnClickListener {
                service?.playNext()
                binding.seekBar.progress = 0
            }

            btnPrevious.setOnClickListener {
                service?.playPrev()
                binding.seekBar.progress = 0
            }

            btnMute.setOnClickListener {
                val btnMuteRes =
                    if (service?.isMute?.value == true) R.drawable.icon_mute_off else R.drawable.icon_mute
                Glide.with(btnMute)
                    .load(btnMuteRes)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(btnMute)
                service?.toggleMute()
            }

            btnRepeat.setOnClickListener {
                val btnRepeatRes = when (service?.repeatMode?.value) {
                    MusicService.REPEAT_MODE_OFF -> R.drawable.icon_repeat_one
                    MusicService.REPEAT_MODE_ONE -> R.drawable.icon_repeat_all
                    else -> R.drawable.icon_repeat_off
                }
                Glide.with(btnRepeat).load(btnRepeatRes).into(btnRepeat)
                val textNotify = when (service?.repeatMode?.value) {
                    MusicService.REPEAT_MODE_OFF -> "Repeat ONE!"
                    MusicService.REPEAT_MODE_ONE -> "Repeat ALL!"
                    else -> "Repeat OFF!"
                }
                service?.setRepeatMode(
                    when (service?.repeatMode?.value) {
                        MusicService.REPEAT_MODE_OFF -> MusicService.REPEAT_MODE_ONE
                        MusicService.REPEAT_MODE_ONE -> MusicService.REPEAT_MODE_ALL
                        else -> MusicService.REPEAT_MODE_OFF
                    }
                )
                Toast.makeText(requireContext(), textNotify, Toast.LENGTH_SHORT).show()
            }

            btnShuffle.setOnClickListener {
                val btnShuffleRes =
                    if (service?.isShuffle?.value == true) R.drawable.icon_shuffle_off else R.drawable.icon_shuffle_on
                val textNotify =
                    if (service?.isShuffle?.value == true) "Shuffle OFF!" else "Shuffle ON!"
                Glide.with(btnShuffle)
                    .load(btnShuffleRes)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
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
        val currentPlaying = service?.currentPlaying?.value
        val isPlaying = service?.isPlayingFlow?.value
        val totalTime = convertMillisToTime(service?.getDuration()?.toLong() ?: 0)
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
                .apply(RequestOptions().transform(RoundedCorners(15)))
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(imgArt)
            Glide.with(imgArtBackground).load(artUri).apply(
                RequestOptions
                    .bitmapTransform(BlurTransformation(15))
            )
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(imgArtBackground)
            Glide.with(btnPlayPause)
                .load(btnPlayPauseRes)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(btnPlayPause)
            seekBar.apply {
                max = service?.getDuration() ?: 0
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
        return "%02d:%02d".format(minutes, seconds)
    }

    private fun setLyric() {
        lifecycleScope.launch {
            try {
                binding.loading.visibility = View.VISIBLE
                val song = service?.currentPlaying?.value
                val response = withContext(Dispatchers.IO) {
                    RetrofitExtension.OVH_LYRICS.getLyrics(song!!.artist, song.name).execute()
                        .body() ?: LyricsResponse("")
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
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

    override fun onSongStart() {
        updateUiOnChangeSong()
    }

    private fun downloadSong(song: Song) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.data)
        val localFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            "${song.name} - ${song.artist}.mp3"
        )

        storageRef.getFile(localFile).addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.download_success), Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.download_error), Toast.LENGTH_SHORT
            ).show()
        }

    }

}