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
    private ImageView ivCodiImage, ivWeatherimage;
    private TextView tvTemperature, tvWeatherOverview, tvWeatherRainWind;
    private String receivedEmail = ""; // 넘어온 이메일을 담을 변수
    private static final String TAG = "MainFragment";
    private final String API_KEY = "1b41c49bf89b2e1d34df357a5b662799400d05bd36af281268deac2c16f12e19"; //기상청API키
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient; //위치 서비스 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //레이아웃 불러오기
        Log.d(TAG, "화면 전환됨");
        return inflater.inflate(R.layout.main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 위치 서비스 초기화
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(requireActivity());

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
        ivWeatherimage = view.findViewById(R.id.weather_image);

        //이미지 업로드
        ivCodiImage.setImageResource(R.drawable.img_main_codi);
        // 권한 확인 및 위치 가져오기 + 날씨 데이터 갱신
        requestLocationPermission();

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


    //날씨 데이터 요청 및 로드 함수
    private void getWeatherData(int nx, int ny){
        //오늘 날짜 가져오기
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());

        //어제 날짜 구하기
        cal.add(java.util.Calendar.DATE, -1);
        String baseDate = sdf.format(cal.getTime());
        String baseTime = "2300"; // 전날 23시로 고정(최고/최저 데이터 포함됨)

        Log.d(TAG, "호출 날짜: " + baseDate + "| 시간: " + baseTime);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherInterface weatherInterface = retrofit.create(WeatherInterface.class);


        Call<WeatherResponse> call = weatherInterface.getWeather(
                API_KEY, 1, 290, "JSON", baseDate, baseTime, nx, ny
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (!isAdded()) return;

                // 프래그먼트가 화면에서 사라졌을 때(isAdded)를 체크
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();

                    // 오늘 날짜 구하기 (TMN, TMX 필터링용)
                    java.util.Calendar todayCal = java.util.Calendar.getInstance();
                    String todayDate = sdf.format(todayCal.getTime());

                    String minTemp = "";
                    String maxTemp = "";
                    String wind = "";
                    String rain = "";
                    String sky_state = "";
                    String rain_state = "";

                    if (weatherData.response != null && weatherData.response.body != null &&
                            weatherData.response.body.items != null) {

                        Log.d(TAG, "성공: 데이터를 정상적으로 수신했습니다.");
                        Log.d(TAG, "실제 JSON 데이터: " + new Gson().toJson(response.body()));
                        Log.d(TAG, "==== 날씨 데이터 분석 시작 ====");

                        for (WeatherResponse.WeatherItem item : weatherData.response.body.items.item) {
                            if (item == null || item.category == null) continue;

                            if (!todayDate.equals(item.fcstDate)) continue;

                            String val = (item.fcstValue == null) ? "0" : item.fcstValue;

                            //온도 소수점 제거
                            try {
                                double tempDouble = Double.parseDouble(val);
                                int tempInt = (int) Math.round(tempDouble);
                                val = String.valueOf(tempInt);
                            } catch (Exception e) {
                                // 숫자가 아닌 값이 들어올 경우
                                Log.d(TAG, "온도 변환 오류(숫자가 아님): " + e.getMessage());
                            }

                            switch (item.category) {
                                case "TMN":
                                    minTemp = val;
                                    Log.d(TAG, "[최저기온] 저장: " + val);
                                    break;
                                case "TMX":
                                    maxTemp = val;
                                    Log.d(TAG, "[최고기온] 저장: " + val);
                                    break;
                                case "POP":
                                    rain = val;
                                    Log.d(TAG, "[강수확률]: " + val + "%");
                                    break;
                                case "WSD":
                                    wind = val;
                                    Log.d(TAG, "[풍속]: " + val + "m/s");
                                    break;
                                case "SKY":
                                    switch (val){
                                        case("1"):
                                            sky_state = "맑음";
                                            break;
                                            case("3"):
                                            sky_state = "구름 많음";
                                            break;
                                            case("4"):
                                            sky_state = "흐림";
                                            break;
                                    }
                                    Log.d(TAG, "[하늘상태]: " + sky_state);
                                    break;
                                case "PTY":
                                    switch (val) {
                                        case ("0"):
                                            rain_state = "없음";
                                            break;
                                        case ("1"):
                                            rain_state = "비";
                                            break;
                                        case ("2"):
                                            rain_state = "비/눈";
                                            break;
                                        case ("3"):
                                            rain_state = "눈";
                                            break;
                                        case ("4"):
                                            rain_state = "소나기";
                                            break;
                                    }
                                    Log.d(TAG, "[강수형태]: " + rain_state);
                                    break;
                            }
                        }
                        Log.d(TAG, "==== 날씨 데이터 분석 완료 ====");
                        if (!maxTemp.isEmpty() && !minTemp.isEmpty()) {
                            tvTemperature.setText(maxTemp + "도 / " + minTemp + "도");
                        } else if (!maxTemp.isEmpty()) {
                            tvTemperature.setText(maxTemp + "도 / -도");
                        } else if (!minTemp.isEmpty()) {
                            tvTemperature.setText(" -도 / " + minTemp + "도");
                        }
                        if(rain_state=="없음"){
                            switch (sky_state) {
                                case ("맑음"):
                                    tvWeatherOverview.setText("하루 종일 맑은 하루예요.");
                                    ivWeatherimage.setImageResource(R.drawable.weather_sunny);
                                    break;
                                case ("구름 많음"):
                                    tvWeatherOverview.setText("가끔 흐린 하루예요");
                                    ivWeatherimage.setImageResource(R.drawable.weather_little_cloudy);
                                    break;
                                case ("흐림"):
                                    tvWeatherOverview.setText("비 없이 흐린 하루예요");
                                    ivWeatherimage.setImageResource(R.drawable.weather_cloudy);
                                    break;
                            }
                        } else {
                            if (rain_state=="눈"){
                                tvWeatherOverview.setText("눈 소식이 있어요.");
                                ivWeatherimage.setImageResource(R.drawable.weather_snowing);
                            } else {
                                tvWeatherOverview.setText("비 소식이 있어요.");
                                ivWeatherimage.setImageResource(R.drawable.weather_rainy);
                            }
                        }
                        tvWeatherRainWind.setText("강수 확률 " + rain + "% | 풍속 " + wind +"m/s");

                    } else {
                        // 응답은 성공(200 OK)이나, 기상청 서버에서 에러 내용을 보낸 경우
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

    //위치 정보 업데이트 함수 (날씨 호출 포함)
    private void requestLocationPermission() {
        // 권한이 이미 있는지 확인
        if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            // 권한이 없다면 사용자에게 요청창 띄우기
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }

        // 현재 위치 가져오기
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                // 위경도를 격자(nx, ny)로 변환
                GpsTransfer transfer = new GpsTransfer(lat, lon);
                int nx = transfer.getX();
                int ny = transfer.getY();

                Log.d(TAG, "현재 위치 격자: nx=" + nx + ", ny=" + ny);

                Log.d(TAG, "날씨 데이터 요청 시작");
                // 날씨 호출
                getWeatherData(nx, ny);
            } else {
                // 위치가 null이면 기본값으로 호출
                getWeatherData(55, 127);
            }
        });
    }
}


