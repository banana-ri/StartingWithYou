package com.example.myapplication_virtualfitting.network;

public class UserData {
    public String email;
    public String password;
    public String name;
    public String age;
    public String height;
    public String weight;
    public String gender;

    // 7개의 데이터를 모두 담을 수 있게 생성자 업데이트
    public UserData(String email, String password, String name, String age, String height, String weight, String gender) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
    }
}