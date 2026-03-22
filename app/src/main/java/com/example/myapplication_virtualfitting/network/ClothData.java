package com.example.myapplication_virtualfitting.network;

public class ClothData {
    public String user_email;
    public String season;
    public String part;
    public String thickness;
    public String length;
    public String image_url;
    public String mapped_asset_id;

    // 데이터를 한 번에 담기 위한 생성자
    public ClothData(String user_email, String season, String part, String thickness,
                     String length, String image_url, String mapped_asset_id) {
        this.user_email = user_email;
        this.season = season;
        this.part = part;
        this.thickness = thickness;
        this.length = length;
        this.image_url = image_url;
        this.mapped_asset_id = mapped_asset_id;
    }
}