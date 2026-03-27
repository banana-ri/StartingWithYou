package com.example.myapplication_virtualfitting;

// 🌟 메모장 기능을 쓰기 위해 추가된 2줄
import android.content.Context;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication_virtualfitting.network.ApiService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.myapplication_virtualfitting.network.ClothData;
import com.example.myapplication_virtualfitting.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClosetDetailFragment extends Fragment {

    private ImageView detailImageView;
    private ChipGroup groupSeason, groupPart, groupThickness, groupLength;
    private static final String TAG = "ClosetDetailFragment";
    private String savedImageFileName = "temp_image.png";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_closet_details_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailImageView = view.findViewById(R.id.detail_image_view);
        groupSeason = view.findViewById(R.id.chipGroup_season);
        groupPart = view.findViewById(R.id.chipGroup_part);
        groupThickness = view.findViewById(R.id.chipGroup_thickness);
        groupLength = view.findViewById(R.id.chipGroup_length);

        setupMandatorySelection(groupPart, groupSeason, groupThickness, groupLength);

        if (getArguments() != null) {
            String imageFileName = getArguments().getString("imageUrl");
            if (imageFileName != null) {
                savedImageFileName = imageFileName;
                String fullImageUrl = "http://10.0.2.2:8000/clothes-image/" + imageFileName;
                Glide.with(this).load(fullImageUrl).placeholder(R.drawable.cloth_tshirt).into(detailImageView);
            }

            checkChipByText(groupPart, getArguments().getString("part"));
            checkChipByText(groupSeason, getArguments().getString("season"));
            checkChipByText(groupThickness, getArguments().getString("thickness"));
            checkChipByText(groupLength, getArguments().getString("length"));
        }

        // 1. '저장하기' 버튼 숨기기 (자동 저장되므로 필요 없음!)
        View saveButton = view.findViewById(R.id.button_save);
        if (saveButton != null) saveButton.setVisibility(View.GONE);

        // 2. 화면 내 '뒤로가기 화살표' 누를 때 자동 저장 실행!
        view.findViewById(R.id.arrow_left_circle).setOnClickListener(v -> saveClothesData(view));

        // 3. 스마트폰 자체 '뒤로가기(스와이프)' 할 때 자동 저장 실행!
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveClothesData(view);
            }
        });

        view.findViewById(R.id.button_recommend).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_closetDetailFragment_to_mainFragment);
        });

        View deleteButton = view.findViewById(R.id.button_delete);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> {
                String clothId = getArguments() != null ? getArguments().getString("clothId") : null;
                if (clothId != null) deleteClothFromServer(clothId, view);
            });
        }
    }

    private void checkChipByText(ChipGroup group, String text) {
        if (text == null) return;
        boolean found = false;
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.getText().toString().equals(text)){
                chip.setChecked(true);
                found = true;
                break;
            }
        }
        if (!found && group.getChildCount() > 0) ((Chip) group.getChildAt(0)).setChecked(true);
    }

    // 🌟 핵심: 정보 수정 후 DB에 내 진짜 이메일과 함께 저장하는 로직
    private void saveClothesData(View view) {
        String selectedPart = getSelectedChipText(groupPart);
        String selectedSeason = getSelectedChipText(groupSeason);
        String selectedThickness = getSelectedChipText(groupThickness);
        String selectedLength = getSelectedChipText(groupLength);

        String clothId = getArguments() != null ? getArguments().getString("clothId") : null;
        if (clothId == null) {
            Navigation.findNavController(view).popBackStack();
            return;
        }

        // 🌟 1. 로그인할 때 꾹 저장해둔 내 이메일을 메모장에서 꺼내오기!
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        // 혹시나 에러로 이메일을 못 찾으면 "test@naver.com"을 임시로 쓰도록 안전장치 마련
        String myEmail = sharedPreferences.getString("USER_EMAIL", "test@naver.com");

        Cloth updatedCloth = new Cloth(selectedSeason, selectedPart, selectedThickness, selectedLength, savedImageFileName);

        // 🌟 2. 가짜 이메일 자리에 방금 꺼낸 내 진짜 이메일(myEmail)을 세팅!
        updatedCloth.user_email = myEmail;

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.updateCloth(clothId, updatedCloth).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Navigation.findNavController(view).popBackStack();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "수정 내용을 서버에 저장하지 못했습니다.", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    private void deleteClothFromServer(String clothId, View view) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.deleteCloth(clothId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "옷장에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).popBackStack();
                } else {
                    Toast.makeText(getContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSelectedChipText(ChipGroup group) {
        int id = group.getCheckedChipId();
        if (id != View.NO_ID) {
            Chip chip = group.findViewById(id);
            return chip.getText().toString();
        }
        return "미선택";
    }

    private void setupMandatorySelection(ChipGroup... groups) {
        for (ChipGroup group : groups) {
            group.setOnCheckedStateChangeListener((parent, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    Chip first = (Chip) parent.getChildAt(0);
                    if (first != null) first.setChecked(true);
                }
            });
        }
    }
}