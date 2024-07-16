package fpoly.anhntph36936.bamdiem_app.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Adapter.Vitri_ADT;
import fpoly.anhntph36936.bamdiem_app.Model.bdiemModel;
import fpoly.anhntph36936.bamdiem_app.Model.thidauModel;
import fpoly.anhntph36936.bamdiem_app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main_BamDiem extends AppCompatActivity {

    LinearLayout view_d, view_x;
    TextView tv_d, tv_x, tv_vt;
    Button btn_trud, btn_trux;
    private SwipeRefreshLayout swipeRefreshLayout;
    int diemdo = 0;
    int diemxanh = 0;
    String vitri;
    String id;
    bdiemModel item;
    Handler handler = new Handler();
    boolean isRunning = false;
    int tempDiemdo = 0;
    int tempDiemxanh = 0;
    ArrayList<bdiemModel> list;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bam_diem);
        view_d = findViewById(R.id.view_d);
        view_x = findViewById(R.id.view_x);
        tv_d = findViewById(R.id.tv_do);
        tv_x = findViewById(R.id.tv_x);
        tv_vt = findViewById(R.id.tv_diem_vt);
        btn_trud = findViewById(R.id.btn_trud);
        btn_trux = findViewById(R.id.btn_trux);
        swipeRefreshLayout = findViewById(R.id.load_diem);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            vitri = intent.getStringExtra("vitri");
            diemdo = intent.getIntExtra("diemdo", 0);
            diemxanh = intent.getIntExtra("diemxanh", 0);

            item = (bdiemModel) intent.getSerializableExtra("bdiemModel");
            tv_vt.setText(vitri);
            tv_d.setText(String.valueOf(diemdo));
            tv_x.setText(String.valueOf(diemxanh));
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRS(id, item);
            }
        });


        view_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diemdo++;
                tv_d.setText(String.valueOf(diemdo));
                handler.removeCallbacksAndMessages(null);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        diemdo += tempDiemdo;
                        updateDo(id, diemdo);
                        tempDiemdo = 0;
                    }
                }, 150);
            }
        });

        view_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diemxanh++;
                tv_x.setText(String.valueOf(diemxanh));
                handler.removeCallbacksAndMessages(null);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        diemxanh += tempDiemxanh;
                        updateDiemXanh(id, diemxanh);
                        tempDiemxanh = 0;
                    }
                }, 150);
            }
        });


        btn_trud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diemdo > 0) {
                    diemdo--;
                    tv_d.setText(String.valueOf(diemdo));
                    handler.removeCallbacksAndMessages(null);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            diemdo -= tempDiemdo;
                            updateDo(id, diemdo);
                            tempDiemdo = 0;
                        }
                    }, 150);
                }
            }
        });
        btn_trux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diemxanh > 0) {
                    diemxanh--;
                    tv_x.setText(String.valueOf(diemxanh));
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            diemxanh -= tempDiemxanh;
                            updateDiemXanh(id, diemxanh);
                            tempDiemxanh = 0;
                        }
                    }, 150);
                }
            }
        });

    }

    private void updateDo(String id, int diemdo) {
        if (id != null) {
            bdiemModel model = new bdiemModel();
            model.setDiemdo(diemdo);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HOSTAPI.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HOSTAPI api = retrofit.create(HOSTAPI.class);
            Call<ArrayList<bdiemModel>> call = api.updateDiemdo(id, model);
            call.enqueue(new Callback<ArrayList<bdiemModel>>() {
                @Override
                public void onResponse(Call<ArrayList<bdiemModel>> call, Response<ArrayList<bdiemModel>> response) {
                    if (response.isSuccessful()) {

                    }
                }

                @Override
                public void onFailure(Call<ArrayList<bdiemModel>> call, Throwable t) {
                    // Xử lý khi có lỗi xảy ra
                }
            });
        }
    }

    private void updateDiemXanh(String id, int diemxanh) {
        if (id != null) {
            bdiemModel model = new bdiemModel();
            model.setDiemxanh(diemxanh);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HOSTAPI.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HOSTAPI api = retrofit.create(HOSTAPI.class);

            Call<ArrayList<bdiemModel>> call = api.updateDiemXanh(id, model);
            call.enqueue(new Callback<ArrayList<bdiemModel>>() {
                @Override
                public void onResponse(Call<ArrayList<bdiemModel>> call, Response<ArrayList<bdiemModel>> response) {
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<bdiemModel>> call, Throwable t) {
                    // Xử lý khi có lỗi xảy ra
                }
            });
        }
    }
    private void loadRS(String id, bdiemModel model) {
        if (id!=null){
            diemdo = 0;
            diemxanh = 0;
            model.set_id(id);
            model.setDiemdo(diemdo);
            model.setDiemxanh(diemxanh);
            tv_d.setText(String.valueOf(0));
            tv_x.setText(String.valueOf(0));
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HOSTAPI.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HOSTAPI api = retrofit.create(HOSTAPI.class);
            Call<ArrayList<bdiemModel>> call = api.reset(id, model);
            call.enqueue(new Callback<ArrayList<bdiemModel>>() {
                @Override
                public void onResponse(Call<ArrayList<bdiemModel>> call, Response<ArrayList<bdiemModel>> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.isSuccessful()) {
                    } else {
                        Log.e("Main_BamDiem", "Không tìm thấy dữ liệu để reset");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<bdiemModel>> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        }

}
