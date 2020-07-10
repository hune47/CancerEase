package com.graduation.CancerEaseProj.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import java.util.ArrayList;
import java.util.List;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class EducationActivity extends AppCompatActivity implements YouTubeThumbnailView.OnInitializedListener, YouTubeThumbnailLoader.OnThumbnailLoadedListener, YouTubePlayer.OnInitializedListener {
    private SharedPref sharedPref;
    YouTubePlayerSupportFragmentX playerFragment;
    YouTubePlayer Player;
    YouTubeThumbnailView thumbnailView;
    YouTubeThumbnailLoader thumbnailLoader;
    RecyclerView VideoList;
    RecyclerView.Adapter adapter;
    List<Drawable> thumbnailViews;
    List<String> VideoId;
    String PLAYLIST_ID = "PLCYlCGq69d4U5bIC5J7_NOcn1kLZd8yhB";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        initializeItems();
        LoadVideosList();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        sharedPref = new SharedPref(this);
        if (sharedPref.getAccountType().equals(PATIENTS_COLLECTION)) {
            setTitle("     خدمات المرضى");
        }else {
            setTitle("     خدمات الطبيب");
        }
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void LoadVideosList(){
        thumbnailViews = new ArrayList<>();
        VideoList = findViewById(R.id.VideoList);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        VideoList.setLayoutManager(layoutManager);
        adapter = new VideoListAdapter();
        VideoList.setAdapter(adapter);
        VideoId = new ArrayList<>();
        thumbnailView = new YouTubeThumbnailView(this);
        thumbnailView.initialize(getResources().getString(R.string.google_api_key), this);
        playerFragment = (YouTubePlayerSupportFragmentX) getSupportFragmentManager().findFragmentById(R.id.VideoFragment);
        playerFragment.initialize(getResources().getString(R.string.google_api_key), this);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        thumbnailLoader = youTubeThumbnailLoader;
        youTubeThumbnailLoader.setOnThumbnailLoadedListener(EducationActivity.this);
        thumbnailLoader.setPlaylist(PLAYLIST_ID);
    }
    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Player=youTubePlayer;
        Player.cueVideo("IV08Z0szUgM");
        Player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                VideoList.setVisibility(b?View.GONE:View.VISIBLE);
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
        thumbnailViews.add(youTubeThumbnailView.getDrawable());
        VideoId.add(s);
        add();
    }

    @Override
    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void add() {
        adapter.notifyDataSetChanged();
        if (thumbnailLoader.hasNext())
            thumbnailLoader.next();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyView>{

        public class MyView extends RecyclerView.ViewHolder{
            ImageView imageView;
            public MyView(View itemView) {
                super(itemView);
                imageView= itemView.findViewById(R.id.thumbnailView);
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        public VideoListAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row, parent, false);
            return new MyView(itemView);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        public void onBindViewHolder(VideoListAdapter.MyView holder, final int position) {
            holder.imageView.setImageDrawable(thumbnailViews.get(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Player.cueVideo(VideoId.get(position));
                }
            });
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        public int getItemCount() {
            return thumbnailViews.size();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
