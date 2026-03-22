package com.example.myapplication_virtualfitting.network;

public class LoginRequest {
    public String email;
    public String password; // 🌟 비밀번호 칸 추가됨!

    // 데이터를 담을 때 사용하는 생성자
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}