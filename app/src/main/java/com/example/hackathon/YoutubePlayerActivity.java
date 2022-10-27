package com.example.hackathon;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity {
    YouTubePlayerView view_youtubePlayer;
    YouTubePlayer.OnInitializedListener listener;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_youtube_player);

        view_youtubePlayer = findViewById(R.id.youtubePlayer);

        final String id = getIntent().getStringExtra("video");
//        Log.d("아이디확인",id);

        //리스너 등록부분
        listener = new YouTubePlayer.OnInitializedListener() {

            //초기화 성공시
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                if(youTubePlayer.isPlaying()){
//                    youTubePlayer.release();
//                    System.out.println(youTubePlayer.isPlaying()+" 확인");
//                }
                youTubePlayer.setShowFullscreenButton(true);
                youTubePlayer.loadVideo(id);//url의 맨 뒷부분 ID값만 넣으면 됨
                youTubePlayer.play();
            }


            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                //실패시
                Log.e("YouTubePlayer", "onInitializationFailure");
            }


        };


        view_youtubePlayer.initialize("AIzaSyDbSVd5uPDIG_1G6xgmZX-qL0UHnvAanBE", listener);


    }

}