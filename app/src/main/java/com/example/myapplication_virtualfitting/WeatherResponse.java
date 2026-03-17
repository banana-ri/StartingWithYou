package com.example.myapplication_virtualfitting;
import java.util.List;

public class WeatherResponse {
    public Response response;

    public class Response{
        public Body body;
    }

    public class Body{
        public Items items;
    }

    public class Items{
        //실제 데이터 리스트
        public List<WeatherItem> item;
    }

    //기상청JSON 결과값이 중첩구조이기 때문에 같은 구조로 클래스를 만듦
    public class WeatherItem{
        public String category; //자료 구분 코드
        @com.google.gson.annotations.SerializedName("fcstValue")
        public String fcstValue; //예보 값

        @com.google.gson.annotations.SerializedName("fcstDate")
        public String fcstDate; //예보 일자

        @com.google.gson.annotations.SerializedName("fcstTime")
        public String fcstTime; //예보 시각
    }
}
