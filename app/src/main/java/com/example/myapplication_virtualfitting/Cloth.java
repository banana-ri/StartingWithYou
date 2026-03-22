package com.example.myapplication_virtualfitting;

import com.google.gson.annotations.SerializedName;

public class Cloth {
    @SerializedName("_id") // MongoDB의 고유 ID
    public String id;

    public String user_email;
    public String season;
    public String part;
    public String thickness;
    public String length;
    public String image_url; // 🌟 핵심: 서버에 저장된 사진 이름 (예: uuid.png)

    public Cloth(String season, String part, String thickness, String length, String image_url) {
        this.season = season;
        this.part = part;
        this.thickness = thickness;
        this.length = length;
        this.image_url = image_url;
    }
}