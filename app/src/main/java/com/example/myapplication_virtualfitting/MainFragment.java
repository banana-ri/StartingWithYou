package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment {

    private LinearLayout btnAddContext, btnTry, btnMyCloset;
    private FloatingActionButton fabSetting;
    private ImageView ivCodiImage;
    private TextView tvTemperature, tvWeatherOverview, tvWeatherRainWind;
    private String receivedEmail = ""; // 넘어온 이메일을 담을 변수
    private static final String TAG = "MainFragment";
    private final String API_KEY = "1b41c49bf89b2e1d34df357a5b662799400d05bd36af281268deac2c16f12e19"; //기상청API키

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //레이아웃 불러오기
        Log.d(TAG, "화면 전환됨");
        return inflater.inflate(R.layout.main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //이메일 받기
        if (getArguments() != null) { //전달받은 번들이 있는지 (이메일이 넘어왔는지) 확인
            receivedEmail = getArguments().getString("userEmail");
            Log.d(TAG, "전달받은 이메일: " + receivedEmail);}

        // 뷰 연결
        btnAddContext = view.findViewById(R.id.button_addcontext);
        btnTry = view.findViewById(R.id.button_try);
        btnMyCloset = view.findViewById(R.id.button_mycloset);
        fabSetting = view.findViewById(R.id.fab_setting_button);
        ivCodiImage = view.findViewById(R.id.generated_codi_image);
        tvTemperature = view.findViewById(R.id.heading_weather);
        tvWeatherOverview = view.findViewById(R.id.weather_overview);
        tvWeatherRainWind = view.findViewById(R.id.weather_rain_wind);

        //이미지 업로드
        ivCodiImage.setImageResource(R.drawable.img_main_codi);
        //날씨 데이터 갱신
        Log.d(TAG, "날씨 데이터 요청 시작");
        getWeatherData();

        // 클릭 리스너
        // 상황 추가
        btnAddContext.setOnClickListener(v -> {
            Log.d(TAG, "페이지 이동: 상황 더하기");
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_situationFragment);
        });
        // 가상 피팅
        btnTry.setOnClickListener(v -> {
            Log.d(TAG, "페이지 이동: 입어보기");
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_tryFragment);
        });
        // 나의 옷장
        btnMyCloset.setOnClickListener(v -> {
            Log.d(TAG, "페이지 이동: 나의 옷장");
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_closetFragment);
        });
        //설정/정보
        fabSetting.setOnClickListener(v -> {
            Log.d(TAG, "페이지 이동: 정보 수정");
            Bundle bundle = new Bundle();
            bundle.putString("userEmail", receivedEmail); //이메일이 DB의 기본키라서 넘겨줌
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_userInfoFragment, bundle);
        });

    }


    private void getWeatherData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherInterface weatherInterface = retrofit.create(WeatherInterface.class);

        //테스트용 고정값 사용
        Call<WeatherResponse> call = weatherInterface.getWeather(
                API_KEY, 1, 10, "JSON", "20260314", "0500", 55, 127
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                // 프래그먼트가 화면에서 사라졌을 때(isAdded)를 체크
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();

                    for (WeatherResponse.WeatherItem item : weatherData.response.body.items.item) {
                        switch (item.category) {
                            case "TMN":
                                Log.d(TAG, "오늘 최저기온: " + item.Value + "°C");
                                break;
                            case "TMX":
                                Log.d(TAG, "오늘 최고기온: " + item.Value + "°C");
                                break;
                            case "POP":
                                Log.d(TAG, "강수확률: " + item.Value + "%");
                                break;
                            case "WSD":
                                Log.d(TAG, "풍속: " + item.Value + "m/s");
                                break;
                            case "SKY":
                                String sky_state = item.Value.equals("1") ? "맑음" : "흐림";
                                Log.d(TAG, "하늘상태: " + sky_state);
                                break;
                            case "PTY":
                                String rain_state = item.Value.equals("1") ? "맑음" : "흐림";
                                Log.d(TAG, "강수형태: " + rain_state);
                                break;
                            case "TH1":
                                Log.d(TAG, "현재 기온 수신: " + item.Value + "도");
                                break;
                            // UI 업데이트
                            // tvTemperature.setText(item.fcstValue + "°C");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                if (isAdded()) {
                    Log.e(TAG, "날씨 호출 실패: " + t.getMessage());
                }
            }
        });
    }
}
