package com.example.myapplication_virtualfitting.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 🌟 핵심 포인트: 에뮬레이터(가상 스마트폰) 테스트용 주소입니다!
    // 만약 나중에 현우 님의 진짜 스마트폰을 꽂아서 테스트할 때는
    // 이 부분을 맥북의 실제 와이파이 IP 주소(예: 192.168.x.x)로 바꿔주셔야 합니다.
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}