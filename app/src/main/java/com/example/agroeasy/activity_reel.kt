package com.example.agroeasy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class ReelActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private val videoList = listOf(
        VideoModel("JLBW2hSD3D0"),  // https://youtube.com/shorts/JLBW2hSD3D0
        VideoModel("Z5qLx_NeOXY"),  // https://youtube.com/shorts/Z5qLx_NeOXY
        VideoModel("PyHVsxW0mek"),  // https://youtube.com/shorts/PyHVsxW0mek
        VideoModel("eqUP3_FkyYA"),  // https://youtube.com/shorts/eqUP3_FkyYA
        VideoModel("jdDXptYsWBc"),  // https://youtube.com/shorts/jdDXptYsWBc
        VideoModel("Hzv9ahzWd6Y"),  // https://youtube.com/shorts/Hzv9ahzWd6Y
        VideoModel("JRqbdy3zTt0"),  // https://youtube.com/shorts/JRqbdy3zTt0
        VideoModel("BrwIPcL76sA"),  // https://youtube.com/shorts/BrwIPcL76sA
        VideoModel("dlHA7OZHO08"),  // https://youtube.com/shorts/dlHA7OZHO08
        VideoModel("GObzx8tksKE"),  // https://youtube.com/shorts/GObzx8tksKE
        VideoModel("d2mt1N8fpc0"),  // https://youtube.com/shorts/d2mt1N8fpc0
        VideoModel("VLTM-lSwDDQ"),  // https://youtube.com/shorts/VLTM-lSwDDQ
        VideoModel("PHXBQbaTRuE"),  // https://youtube.com/shorts/PHXBQbaTRuE
        VideoModel("EEuZJqgkJkA"),  // https://youtube.com/shorts/EEuZJqgkJkA
        VideoModel("aNhnAs8eyhI"),  // https://youtube.com/shorts/aNhnAs8eyhI
        VideoModel("Wi59Fdte6Vg"),  // https://youtube.com/shorts/Wi59Fdte6Vg
        VideoModel("l9HcTx8Oh0s"),  // https://youtube.com/shorts/l9HcTx8Oh0s
        VideoModel("3ObGGfus4Uc"),  // https://youtube.com/shorts/3ObGGfus4Uc
        VideoModel("203gFqq-GWU"),  // https://youtube.com/shorts/203gFqq-GWU
        VideoModel("vQarct6JIaQ"),  // https://youtube.com/shorts/vQarct6JIaQ
        VideoModel("ZfRFOtvtxmc"),  // https://youtube.com/shorts/ZfRFOtvtxmc
        VideoModel("UfdGe94LQiI"),  // https://youtube.com/shorts/UfdGe94LQiI
        VideoModel("8yTfQh3YcSQ")   // https://youtube.com/shorts/8yTfQh3YcSQ
    )

    private lateinit var youTubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reel)

        recyclerView = findViewById(R.id.recyclerView)

        // Set layout manager for vertical scrolling
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        // Shuffle the video list to show videos in random order
        val shuffledVideoList = videoList.shuffled()

        // Attach the adapter with the shuffled list
        videoAdapter = VideoAdapter(shuffledVideoList)
        recyclerView.adapter = videoAdapter

        // Add a SnapHelper to snap to each video one by one
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }

    override fun onStart() {
        super.onStart()
        youTubePlayerView = YouTubePlayerView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
    }
}
