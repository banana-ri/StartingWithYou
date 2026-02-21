package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1. 레이아웃 연결
        View view = inflater.inflate(R.layout.main_page, container, false);

        // 2. 메인 페이지에 있던 버튼들 연결
        LinearLayout btnAddContext = view.findViewById(R.id.button_addcontext);
        LinearLayout btnTry = view.findViewById(R.id.button_try);
        LinearLayout btnMyCloset = view.findViewById(R.id.button_mycloset);
        FloatingActionButton fabSetting = view.findViewById(R.id.fab_setting_button);
        ImageView ivCodiImage = view.findViewById(R.id.generated_codi_image);

        // 3. Navigation을 이용해 이동
        // 상황 추가(add_context_page)로 이동
        btnAddContext.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_situationFragment);
        });
        // 가상 피팅(try_page)으로 이동
        btnTry.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_tryFragment);
        });
        // 나의 옷장(my_closet_page)으로 이동
        btnMyCloset.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_closetFragment);
        });
        //설정/정보(user_info_page)로 이동
        fabSetting.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_userInfoFragment);
        });
        //이미지 업로드
        ivCodiImage.setImageResource(R.drawable.img_main_codi);

        return view;
    }
}
