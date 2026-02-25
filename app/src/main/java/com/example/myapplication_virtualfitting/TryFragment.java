package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class TryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.try_page, container, false);

        // 뒤로가기 버튼
        ImageButton btnBack = view.findViewById(R.id.arrow_left_circle);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Navigation.findNavController(v).popBackStack();
            });
        }

        //이쯤에 유니티 연결 코드 추가

        return view;
    }
}
