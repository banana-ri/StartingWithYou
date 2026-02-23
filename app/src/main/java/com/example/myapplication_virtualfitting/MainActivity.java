package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. NavHostFragment 찾기
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // 2. NavController 가져오기 (화면 전환 제어자)
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // (옵션) 액션바가 있다면 내비게이션과 연결해 제목을 자동으로 바꿔줍니다.
            // NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    // 뒤로 가기 버튼 지원 (액션바의 뒤로 가기 버튼 클릭 시)
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}