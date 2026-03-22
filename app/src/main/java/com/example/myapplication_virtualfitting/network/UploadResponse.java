package com.example.myapplication_virtualfitting.network;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    public String status;
    public String message;
    public String file_name;

    @SerializedName("ai_analysis")
    public AiAnalysis aiAnalysis;

    // AI가 분석한 옷의 속성들을 담는 클래스
    public static class AiAnalysis {
        public String season;
        public String part;
        public String thickness;
        public String length;
    }
}