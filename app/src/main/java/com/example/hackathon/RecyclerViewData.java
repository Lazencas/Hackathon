package com.example.hackathon;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class RecyclerViewData {

    private Bitmap userImage;
    private String userNickName;
    private Bitmap youTubeThumbnail;
    private String countLike;
    private String contents;
    private String youtubeUrl;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    private int likeBtn;

    public int getLikeBtn() {
        return likeBtn;
    }

    public void setLikeBtn(int likeBtn) {
        this.likeBtn = likeBtn;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public Bitmap getYouTubeThumbnail() {
        return youTubeThumbnail;
    }

    public void setYouTubeThumbnail(Bitmap youTubeThumbnail) {
        this.youTubeThumbnail = youTubeThumbnail;
    }

    public String getCountLike() {
        return countLike;
    }

    public void setCountLike(String countLike) {
        this.countLike = countLike;
    }


    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
