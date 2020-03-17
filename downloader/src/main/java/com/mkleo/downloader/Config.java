package com.mkleo.downloader;

public class Config {

    private String url;
    private String path;
    private String md5;

    private Config(String url, String path, String md5) {
        this.url = url;
        this.path = path;
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }

    public static class Builder {

        private final String url;
        private final String path;
        private String md5;

        public Builder(String url, String path) {
            this.url = url;
            this.path = path;
        }

        public Builder setMd5(String md5) {
            this.md5 = md5;
            return this;
        }

        public Config build() {
            return new Config(url, path, md5);
        }
    }

}
