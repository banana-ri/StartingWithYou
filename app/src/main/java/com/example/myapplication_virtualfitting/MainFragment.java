package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment {

    private LinearLayout btnAddContext, btnTry, btnMyCloset;
    private FloatingActionButton fabSetting;
    private ImageView ivCodiImage;
    private String receivedEmail = ""; // 넘어온 이메일을 담을 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //레이아웃 불러오기
        return inflater.inflate(R.layout.main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //이메일 받기
        if (getArguments() != null) { //전달받은 번들이 있는지 (이메일이 넘어왔는지) 확인
            receivedEmail = getArguments().getString("userEmail");}

        // 뷰 연결
        btnAddContext = view.findViewById(R.id.button_addcontext);
        btnTry = view.findViewById(R.id.button_try);
        btnMyCloset = view.findViewById(R.id.button_mycloset);
        fabSetting = view.findViewById(R.id.fab_setting_button);
        ivCodiImage = view.findViewById(R.id.generated_codi_image);

        //이미지 업로드
        ivCodiImage.setImageResource(R.drawable.img_main_codi);

        // 클릭 리스너
        // 상황 추가
        btnAddContext.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_situationFragment);
        });
        // 가상 피팅
        btnTry.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_tryFragment);
        });
        // 나의 옷장
        btnMyCloset.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_closetFragment);
        });
        //설정/정보
        fabSetting.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("userEmail", receivedEmail); //이메일이 DB의 기본키라서 넘겨줌
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_userInfoFragment, bundle);
        });

    }
}
