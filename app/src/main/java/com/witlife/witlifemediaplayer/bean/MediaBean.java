package com.witlife.witlifemediaplayer.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bruce on 15/08/2017.
 */

public class MediaBean implements Serializable{
    private List<TrailersBean> trailers;

    public List<TrailersBean> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<TrailersBean> trailers) {
        this.trailers = trailers;
    }

    public static class TrailersBean implements Serializable{
        /**
         * id : 66916
         * movieName : "请以你的名字呼唤我"预告
         * coverImg : http://img5.mtime.cn/mg/2017/08/04/165113.24946335.jpg
         * movieId : 234474
         * url : http://vfx.mtime.cn/Video/2017/08/02/mp4/170802074323236656.mp4
         * hightUrl : http://vfx.mtime.cn/Video/2017/08/02/mp4/170802074323236656.mp4
         * videoTitle : 请以你的名字呼唤我 剧场版预告
         * videoLength : 129
         * rating : -1
         * type : ["剧情","爱情"]
         * summary : 男孩与房客的暧昧故事
         */

        private int id;
        private String movieName;
        private String coverImg;
        private int movieId;
        private String url;
        private String hightUrl;
        private String videoTitle;
        private long videoLength;
        private String rating;
        private String summary;
        private List<String> type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public String getCoverImg() {
            return coverImg;
        }

        public void setCoverImg(String coverImg) {
            this.coverImg = coverImg;
        }

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHightUrl() {
            return hightUrl;
        }

        public void setHightUrl(String hightUrl) {
            this.hightUrl = hightUrl;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public long getVideoLength() {
            return videoLength;
        }

        public void setVideoLength(long videoLength) {
            this.videoLength = videoLength;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getType() {
            return type;
        }

        public void setType(List<String> type) {
            this.type = type;
        }
    }
}
