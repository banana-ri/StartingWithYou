package com.example.myapplication_virtualfitting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page); // 로그인 레이아웃 파일명 확인

        // 1. '계속' 버튼 클릭 시 사용자 정보 입력(UserInfoActivity) 화면으로 이동
        Button btnContinue = findViewById(R.id.button_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 실제 서비스라면 여기서 이메일 유효성 검사 등을 진행합니다.
                Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        // 2. 구글 로그인 버튼
        LinearLayout btnGoogle = findViewById(R.id.button_google);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "구글 로그인 기능 구현 전.", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. 네이버 로그인 버튼
        LinearLayout btnNaver = findViewById(R.id.naver_button);
        btnNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "네이버 로그인 기능 구현 전.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}