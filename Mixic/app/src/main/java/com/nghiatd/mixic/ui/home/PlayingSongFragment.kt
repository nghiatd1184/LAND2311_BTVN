package com.nghiatd.mixic.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentPlayingSongBinding
import com.nghiatd.mixic.service.MusicService
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch

class PlayingSongFragment : Fragment() {

    private lateinit var binding: FragmentPlayingSongBinding
    private var service: MusicService? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        val currentPlaying = service?.currentPlaying?.value?.second
        val isPlaying = service?.isPlayingFlow?.value
        if (currentPlaying != null) {
            binding.apply {
                tvName.text = currentPlaying.name
                tvArtist.text = currentPlaying.artist
                val artUri = Uri.parse(currentPlaying.image)
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
            }
        }
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
            }

            btnPlayPause.setOnClickListener {
                val song = service?.currentPlaying?.value?.second
                val imgRes = if (service?.isPlayingFlow?.value == true) R.drawable.icon_play_reverse else R.drawable.icon_pause_reverse
                Glide.with(btnPlayPause)
                    .load(imgRes)
                    .into(btnPlayPause)
                service?.playPause(song)
                minimizedSetViewOnCommand()
                val btnPlayPause = (parentFragment as HomeFragment).view?.findViewById<ImageView>(R.id.btn_play_pause)?.findViewById<ImageView>(R.id.btn_play_pause)
                val imgResMinimized = if (service?.isPlayingFlow?.value == true) R.drawable.icon_play else R.drawable.icon_pause
                Glide.with(btnPlayPause!!)
                    .load(imgResMinimized)
                    .into(btnPlayPause)
            }

            btnNext.setOnClickListener {
                service?.playNext()
                minimizedSetViewOnCommand()
                initView()
            }

            btnPrevious.setOnClickListener {
                service?.playPrev()
                minimizedSetViewOnCommand()
                initView()
            }

            btnMute.setOnClickListener {
                val btnMuteRes = if (service?.isMute?.value == true) R.drawable.icon_mute_off else R.drawable.icon_mute
                Glide.with(btnMute)
                    .load(btnMuteRes)
                    .into(btnMute)
                service?.toggleMute()
            }

            btnRepeat.setOnClickListener {

            }

            btnShuffle.setOnClickListener {

            }

        }
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}