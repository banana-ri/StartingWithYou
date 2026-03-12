package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.chip.ChipGroup;

public class SituationFragment extends Fragment {

    private TextView tvCautionClothingDesc, tvCautionColorDesc, tvCautionClothingFooter, tvCautionColorFooter;
    private ImageView ivSuggestImage;
    private static final String TAG = "SituationFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //레이아웃 연결
        Log.d(TAG, "화면 전환됨");
        return inflater.inflate(R.layout.add_context_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 뷰 찾기
        tvCautionClothingDesc = view.findViewById(R.id.caution_clothing_desc);
        tvCautionClothingFooter = view.findViewById(R.id.caution_clothing_footer);
        tvCautionColorDesc = view.findViewById(R.id.caution_color_desc);
        tvCautionColorFooter = view.findViewById(R.id.caution_color_footer);
        ivSuggestImage = view.findViewById(R.id.generated_codi_image_contents);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroup_contents);
        ImageButton btnBack = view.findViewById(R.id.arrow_left_circle);
        LinearLayout btnTry = view.findViewById(R.id.button_try);

        // 뒤로가기 버튼
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "페이지 이동: 뒤로가기");
                Navigation.findNavController(v).popBackStack();
            });
        }

        // 입어보기 버튼
        if (btnTry != null) {
            btnTry.setOnClickListener(v -> {
                Log.d(TAG, "페이지 이동: 입어보기");
                Navigation.findNavController(v).navigate(R.id.action_situationFragment_to_tryFragment);
            });
        }

        // 칩 선택 리스너
        if (chipGroup != null) {
            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) return;
                int checkedId = checkedIds.get(0);

                if (checkedId == R.id.chip_contents_wedding_guest) {
                    Log.d(TAG, "칩 선택됨: 결혼식 하객룩");
                    updateUI("· 슬리퍼, 트레이닝복 등의 편한 의상\n· 짧거나 노출이 있는 의상",
                            "다양한 지인이 모이는 자리이니 주의해야 해요.",
                            "· 올블랙 착장\n· 넓은 면적의 화이트",
                            "화이트는 신부의 색으로, 피하는 것이 좋아요.",
                            R.drawable.img_wedding_codi);
                } else if (checkedId == R.id.chip_contents_funeral) {
                    Log.d(TAG, "칩 선택됨: 장례식장 조문");
                    updateUI("· 화려한 장식이나 노출이 있는 의상\n· 샌들, 슬리퍼 등의 신발",
                            "단정한 옷을 기본으로, 양말은 반드시 착용해야 해요.",
                            "· 밝고 화사한 색상\n· 화려한 무늬",
                            "장례식장에서는 검은색 정장을 입는 게 일반적이에요.",
                            R.drawable.img_funeral_codi);
                } else if (checkedId == R.id.chip_contents_interview) {
                    Log.d(TAG, "칩 선택됨: 면접");
                    updateUI("· 너무 튀는 액세서리나 무늬\n· 구겨진 옷",
                            "깔끔한 첫인상이 가장 중요해요.",
                            "· 원색 위주의 너무 밝은 색",
                            "신뢰감을 주는 네이비/그레이가 좋아요.",
                            R.drawable.img_interview_codi);
                }
            });
        }
    }

    // UI 업데이트 보조 메서드
    private void updateUI(String cautionD, String cautionF, String colorD, String colorF, int imgRes) {
        tvCautionClothingDesc.setText(cautionD);
        tvCautionClothingFooter.setText(cautionF);
        tvCautionColorDesc.setText(colorD);
        tvCautionColorFooter.setText(colorF);
        ivSuggestImage.setImageResource(imgRes);
        Log.d(TAG, "컨텐츠 업데이트 완료");
    }
}
