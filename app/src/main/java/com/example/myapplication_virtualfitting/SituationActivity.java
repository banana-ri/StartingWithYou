package com.example.myapplication_virtualfitting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class SituationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_context_page); // 특수 상황 레이아웃 파일명

        // 상단 뒤로가기 버튼
        ImageButton btnBack = findViewById(R.id.arrow_left_circle);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면(메인)으로 돌아감
            }
        });

        // 하단 '입어보기' 버튼 클릭 시 (예: 옷장으로 이동하거나 특정 동작)
        LinearLayout btnTry = findViewById(R.id.button_try);
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SituationActivity.this, ClosetActivity.class);
                startActivity(intent);
            }
        });
    }
}
