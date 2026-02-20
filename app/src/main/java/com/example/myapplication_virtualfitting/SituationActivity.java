package com.example.myapplication_virtualfitting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.ChipGroup;
import java.util.List;

public class SituationActivity extends AppCompatActivity {

    private TextView tvCautionClothingDesc, tvCautionColorDesc, tvCautionClothingFooter, tvCautionColorFooter;
    private ImageView ivSuggestImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_context_page); // 특수 상황 레이아웃 파일명

        // 뷰 초기화
        tvCautionClothingDesc = findViewById(R.id.caution_clothing_desc);
        tvCautionClothingFooter = findViewById(R.id.caution_clothing_footer);
        tvCautionColorDesc = findViewById(R.id.caution_color_desc);
        tvCautionColorFooter = findViewById(R.id.caution_color_footer);
        ivSuggestImage = findViewById(R.id.generated_codi_image_contents); // 추천 이미지 뷰

        ChipGroup chipGroup = findViewById(R.id.chipGroup_contents);
        ImageButton btnBack = findViewById(R.id.arrow_left_circle);

        // 상단 뒤로가기 버튼
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

        // 칩 선택 리스너 (Material 3 기준)
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0); // 단일 선택 모드이므로 첫 번째 ID 사용

            if (checkedId == R.id.chip_contents_wedding_guest) {
                updateUI("· 슬리퍼, 트레이닝복 등의 편한 의상\n· 짧거나 노출이 있는 의상",
                        "다양한 지인이 모이는 자리이니 주의해야 해요."
                        "· 올블랙 착장\n· 넓은 면적의 화이트",
                        "화이트는 신부의 색으로, 피하는 것이 좋아요.",
                        R.drawable.img_wedding_codi); // 이미지 리소스 이름 확인

            } else if (checkedId == R.id.chip_contents_funeral) {
                updateUI("· 화려한 장식이나 노출이 있는 의상\n· 샌들, 슬리퍼 등의 신발",
                        "단정한 옷을 기본으로, 양말은 반드시 착용해야 해요.",
                        "· 밝고 화사한 색상\n· 화려한 무늬",
                        "· 장례식장에서는 검은색 정장을 입는 게 일반적이에요.",
                        R.drawable.img_funeral_codi);

            } else if (checkedId == R.id.chip_contents_interview) {
                updateUI("· 너무 튀는 액세서리나 무늬\n· 구겨진 옷",
                        "깔끔한 첫인상이 가장 중요해요.",
                        "· 원색 위주의 너무 밝은 색",
                        "신뢰감을 주는 네이비/그레이가 좋아요.\n",
                        R.drawable.img_interview_codi);
            }
        });
    }

    // UI 텍스트와 이미지를 한 번에 업데이트하는 보조 메서드
    private void updateUI(String cautionD, String cautionF, String colorD, String colorF, int imgRes) {
        tvCautionClothingDesc.setText(cautionD);
        tvCautionClothingFooter.setText(cautionF);
        tvCautionColorDesc.setText(colorD);
        tvCautionColorFooter.setText(colorF);
        ivSuggestImage.setImageResource(imgRes);
    }
}
