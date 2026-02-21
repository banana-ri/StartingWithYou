package com.example.myapplication_virtualfitting;

public class Cloth {
    int imageResId; // 이미지 리소스 (예: R.drawable.my_shirt)
    String season, part, thickness, length; // 카테고리 (계절, 부분, 두께, 기장)

    public Cloth(int imageResId, String season, String part, String thickness, String length) {
        this.imageResId = imageResId; //이미지 리소스
        this.season = season; //계절
        this.part = part; //부분
        this.thickness = thickness; //두께
        this.length = length; //기장
    }
}
