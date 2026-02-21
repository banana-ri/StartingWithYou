package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {

    // Fragment는 onCreateView에서 레이아웃을 연결합니다.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. 레이아웃 인플레이트 (부풀리기)
        View view = inflater.inflate(R.layout.sign_in_page, container, false);

        // 2. 뷰 찾기
        Button btnContinue = view.findViewById(R.id.button_continue);
        LinearLayout btnGoogle = view.findViewById(R.id.button_google);
        LinearLayout btnNaver = view.findViewById(R.id.naver_button);

        // 3. 클릭 리스너 (Navigation 방식)
        // '계속' 버튼: 사용자 정보 입력 화면으로 이동
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                /*String email = emailEditText.getText().toString(); // 이메일 입력창에서 텍스트 가져오기

                if (isExistingUser(email)) {
                    // 기존 사용자라면? -> 메인 화면으로
                    Toast.makeText(getContext(), "환영합니다!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainFragment);
                } else {
                    // 신규 사용자라면? -> 정보 입력 화면으로
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_userInfoFragment);
                }*/
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_userInfoFragment);
            });
        }
        //구글 로그인 버튼
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> {
                Toast.makeText(getContext(), "구글 로그인 기능 구현 전.", Toast.LENGTH_SHORT).show();
                // 실제 구글 로그인 SDK 호출 로직이 들어갈 자리입니다.
            });
        }
        //네이버 로그인 버튼
        if (btnNaver != null) {
            btnNaver.setOnClickListener(v -> {
                Toast.makeText(getContext(), "네이버 로그인 기능 구현 전.", Toast.LENGTH_SHORT).show();
                // 실제 네이버 로그인 SDK 호출 로직이 들어갈 자리입니다.
            });
        }

        return view;
    }

    // 기존 사용자인지 확인하는 임시 메서드
    private boolean isExistingUser(String email) {
        // 실제로는 여기서 DB를 조회합니다.
        // 지금은 테스트를 위해 특정 이메일만 기존 사용자로 처리해볼게요.
        return email.equals("test@email.com");
    }
}
