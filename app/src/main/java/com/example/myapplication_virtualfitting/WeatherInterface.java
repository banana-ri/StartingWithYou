package com.example.myapplication_virtualfitting;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherInterface {
    @GET("getVilageFcst") //단기예보 조회 서비스
    Call<WeatherResponse> getWeather(
          @Query("serviceKey") String serviceKey,
          @Query("pageNo") int pageNo,
          @Query("numOfRows") int numOfRows,
          @Query("dataType") String dataType,
          @Query("base_date") String baseDate,
          @Query("base_time") String baseTime,
          @Query("nx") int nx,
          @Query("ny") int ny
    );
}
