package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ClosetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_closet_page); // 나의 옷장 레이아웃 파일명

        // 상단 뒤로가기 버튼
        ImageButton btnBack = findViewById(R.id.arrow_left_circle);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 우측 하단 플러스(+) 버튼
        FloatingActionButton fabAdd = findViewById(R.id.fab_icon_button);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아직 이동할 페이지가 없다면 토스트 메시지로 알림
                Toast.makeText(ClosetActivity.this, "옷 등록 화면 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
