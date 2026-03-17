package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

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
                API_KEY, 1, 100, "JSON", "20260317", "0200", 55, 127
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (!isAdded()) return;

                // 프래그먼트가 화면에서 사라졌을 때(isAdded)를 체크
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();

                    String minTemp = "";
                    String maxTemp = "";

                    if (weatherData.response != null && weatherData.response.body != null &&
                            weatherData.response.body.items != null) {

                        Log.d(TAG, "성공: 데이터를 정상적으로 수신했습니다.");
                        Log.d(TAG, "실제 JSON 데이터: " + new Gson().toJson(response.body()));
                        Log.d(TAG, "==== 날씨 데이터 분석 시작 ====");

                        for (WeatherResponse.WeatherItem item : weatherData.response.body.items.item) {
                            if (item == null || item.category == null) continue;

                            if (!item.category.equals("TMN") && !item.category.equals("TMX")) {
                                if (item.fcstTime != null && !item.fcstTime.equals("0600")) continue; //0600 시각 데이터만 필터링
                            }

                            String val = (item.fcstValue == null) ? "0" : item.fcstValue;

                            switch (item.category) {
                                case "TMN":
                                    minTemp = val;
                                    Log.d(TAG, "[최저기온] 저장: " + val);
                                    break;
                                case "TMX":
                                    maxTemp = val;
                                    Log.d(TAG, "[최고기온] 저장: " + val);
                                    break;
                                case "TMP":
                                    Log.d(TAG, "[현재기온]: " + val+ "도");
                                    break;
                                case "POP":
                                    Log.d(TAG, "[강수확률]: " + val + "%");
                                    break;
                                case "WSD":
                                    Log.d(TAG, "[풍속]: " + val + "m/s");
                                    break;
                                case "SKY":
                                    String sky_state = "1".equals(val) ? "맑음" : "흐림";
                                    Log.d(TAG, "[하늘상태]: " + sky_state);
                                    break;
                                case "PTY":
                                    String rain_state = "0".equals(val) ? "없음" : "비/눈";
                                    Log.d(TAG, "[강수형태]: " + rain_state);
                                    break;

                                // UI 업데이트
                                // tvTemperature.setText(item.fcstValue + "°C");
                            }
                        }
                        Log.d(TAG, "==== 날씨 데이터 분석 완료 ====");
                        if (!maxTemp.isEmpty() && !minTemp.isEmpty()) {
                            tvTemperature.setText(maxTemp + "°C / " + minTemp + "°C");
                        } else if (!maxTemp.isEmpty()) {
                            tvTemperature.setText(maxTemp + "°C");
                        } else if (!minTemp.isEmpty()) {
                            tvTemperature.setText(minTemp + "°C");
                        }

                    } else {
                        // 2. 응답은 성공(200 OK)이나, 기상청 서버에서 '에러 내용'을 보낸 경우
                        Log.e(TAG, "실패: 서버 응답은 왔으나 내용이 없습니다.");
                        Log.e(TAG, "서버에서 보낸 원본: " + response.toString());

                        // 토스트나 UI로 에러 알림
                        Toast.makeText(getContext(), "날씨 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    //HTTP 응답 자체가 실패한 경우
                    Log.d(TAG, "실패: 통신 실패. 코드: " + response.code());
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
