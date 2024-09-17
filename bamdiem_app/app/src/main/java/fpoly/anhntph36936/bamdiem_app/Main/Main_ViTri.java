package fpoly.anhntph36936.bamdiem_app.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Adapter.Vitri_ADT;
import fpoly.anhntph36936.bamdiem_app.Model.bdiemModel;
import fpoly.anhntph36936.bamdiem_app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main_ViTri extends AppCompatActivity {

    RecyclerView rc_vitri;
    ArrayList<bdiemModel> list;
    Vitri_ADT vitriAdt;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_diem);

        rc_vitri = findViewById(R.id.rc_vitri);
        swipeRefreshLayout = findViewById(R.id.load_vt);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        load();
    }

    private void load() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOSTAPI.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HOSTAPI api = retrofit.create(HOSTAPI.class);
        Call<ArrayList<bdiemModel>> call = api.getBdiems();
        call.enqueue(new Callback<ArrayList<bdiemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<bdiemModel>> call, Response<ArrayList<bdiemModel>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    list = response.body();
                    vitriAdt = new Vitri_ADT(list, Main_ViTri.this);
                    rc_vitri.setLayoutManager(new LinearLayoutManager(Main_ViTri.this));
                    rc_vitri.setAdapter(vitriAdt);
                } else {
                    Toast.makeText(Main_ViTri.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<bdiemModel>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Main_ViTri.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        load();
    }
}
