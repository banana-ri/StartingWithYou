package com.example.myapplication_virtualfitting;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 시스템 UI 제어
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true); // 상태표시줄 글자색 검정
        controller.hide(WindowInsetsCompat.Type.navigationBars()); // 하단바 숨김
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        // NavHostFragment 찾기
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // NavController 가져오기 (화면 전환 제어자)
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            //메인 화면에서 뒤로가기를 눌렀을 때, 로그인 화면으로 넘어가는 것을 막음
            @Override
            public void handleOnBackPressed() {
                // 현재 위치가 메인 화면인지 확인
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == R.id.mainFragment) {
                    finish(); // 메인 화면이라면 앱을 종료
                } else {
                    // 메인 화면이 아니라면 원래대로 뒤로가기 수행
                    setEnabled(false); // 콜백 비활성화 (무한루프 방지)
                    getOnBackPressedDispatcher().onBackPressed(); // 시스템 뒤로가기 실행
                    setEnabled(true); // 다시 활성화
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);


    }

    // 뒤로 가기 버튼 지원 (물리버튼 X)
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}