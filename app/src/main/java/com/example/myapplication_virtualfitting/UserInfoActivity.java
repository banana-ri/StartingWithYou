package com.example.myapplication_virtualfitting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UserInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_page); // 사용자 정보 레이아웃 파일명

        // '계속' 버튼 클릭 시 메인으로 이동
        Button btnContinue = findViewById(R.id.button_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                // 입력 정보 수정 후 돌아가는 것이므로, 기존 메인 액티비티를 새로 띄우거나 현재창을 닫음
                startActivity(intent);
                finish(); // 현재 입력창은 종료
            }
        });
    }
}