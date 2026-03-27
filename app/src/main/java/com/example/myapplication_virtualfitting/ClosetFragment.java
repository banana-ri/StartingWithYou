package com.example.myapplication_virtualfitting;

// 🌟 메모장 기능을 쓰기 위해 추가된 2줄

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication_virtualfitting.network.ApiService;
import com.example.myapplication_virtualfitting.network.ClothListResponse;
import com.example.myapplication_virtualfitting.network.RetrofitClient;
import com.example.myapplication_virtualfitting.network.UploadResponse;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClosetFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClothAdapter adapter;
    private List<Cloth> allClothes = new ArrayList<>();
    private List<Cloth> filteredList = new ArrayList<>();
    private static final String TAG = "ClosetFragment";

    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 갤러리 결과 처리기 등록
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                Toast.makeText(getContext(), "사진을 서버로 전송합니다. AI 분석 중...", Toast.LENGTH_LONG).show();
                uploadImageToServer(uri);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_closet_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(requireContext(), "옷장 화면 준비 완료!", Toast.LENGTH_SHORT).show();

        // 뒤로가기 버튼
        ImageButton btnBack = view.findViewById(R.id.arrow_left_circle);
        if (btnBack != null) btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // + 버튼 설정
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_icon_button);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                String[] options = {"갤러리에서 사진 가져오기", "카메라로 촬영하기"};
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("옷장에 옷 추가하기")
                        .setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                galleryLauncher.launch("image/*");
                            } else if (which == 1) {
                                Toast.makeText(getContext(), "카메라 연동은 다음 단계에서 진행합니다!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            });
        }

        // 리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.recycler_view_closet);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new ClothAdapter(filteredList, cloth -> {
            Bundle bundle = new Bundle();
            bundle.putString("clothId", cloth.id);
            bundle.putString("imageUrl", cloth.image_url);
            bundle.putString("season", cloth.season);
            bundle.putString("part", cloth.part);
            bundle.putString("thickness", cloth.thickness);
            bundle.putString("length", cloth.length);
            Navigation.findNavController(view).navigate(R.id.action_closetFragment_to_closetDetailFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        // 🌟 1. 처음 옷장 화면 켤 때 내 이메일로 데이터 요청하기
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String myEmail = sharedPreferences.getString("USER_EMAIL", "test@naver.com");
        fetchClothesFromServer(myEmail);

        // 칩 그룹 필터 설정
        ChipGroup chipGroup = view.findViewById(R.id.chipGroup_category);
        if (chipGroup != null) {
            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) return;
                int checkedId = checkedIds.get(0);
                String category = "전체";
                if (checkedId == R.id.chip_category_top) category = "상의";
                else if (checkedId == R.id.chip_category_bottoms) category = "하의";
                else if (checkedId == R.id.chip_category_acc) category = "악세사리";
                else if (checkedId == R.id.chip_category_bag) category = "가방";
                else if (checkedId == R.id.chip_category_shoes) category = "신발";
                filterClothes(category);
            });
        }
    }

    // 서버로 이미지 전송 로직
    private void uploadImageToServer(Uri uri) {
        try {
            File file = new File(requireContext().getCacheDir(), "upload_temp.jpg");
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.uploadClothImage(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UploadResponse res = response.body();
                        if ("success".equals(res.status)) {
                            saveClothDataToDB(res.file_name, res.aiAnalysis);
                        }
                    } else {
                        Toast.makeText(getContext(), "AI 분석 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "서버 전송 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // AI 분석 결과를 DB에 저장
    private void saveClothDataToDB(String fileName, UploadResponse.AiAnalysis aiAnalysis) {
        Cloth newCloth = new Cloth(aiAnalysis.season, aiAnalysis.part, aiAnalysis.thickness, aiAnalysis.length, fileName);

        // 🌟 2. 새 옷을 업로드할 때 가짜 이메일 대신 내 진짜 이메일 넣기!
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String myEmail = sharedPreferences.getString("USER_EMAIL", "test@naver.com");
        newCloth.user_email = myEmail;

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.saveClothToDB(newCloth).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "옷장에 옷이 추가되었습니다!", Toast.LENGTH_SHORT).show();
                    // 🌟 3. 업로드 성공 후 새로고침할 때도 내 진짜 이메일로 요청!
                    fetchClothesFromServer(myEmail);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "DB 저장 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchClothesFromServer(String email) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getMyClothes(email).enqueue(new Callback<ClothListResponse>() {
            @Override
            public void onResponse(Call<ClothListResponse> call, Response<ClothListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allClothes.clear();
                    if (response.body().data != null) {
                        allClothes.addAll(response.body().data);
                    }
                    filterClothes("전체");
                }
            }

            @Override
            public void onFailure(Call<ClothListResponse> call, Throwable t) {
                Log.e(TAG, "목록 로드 실패", t);
            }
        });
    }

    private void filterClothes(String category) {
        filteredList.clear();
        for (Cloth cloth : allClothes) {
            if (category.equals("전체") || cloth.part.equals(category)) {
                filteredList.add(cloth);
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}