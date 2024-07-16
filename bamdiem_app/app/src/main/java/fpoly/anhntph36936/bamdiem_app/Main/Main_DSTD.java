package fpoly.anhntph36936.bamdiem_app.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Adapter.Dstd_ADT;
import fpoly.anhntph36936.bamdiem_app.Adapter.Vitri_ADT;
import fpoly.anhntph36936.bamdiem_app.Model.bdiemModel;
import fpoly.anhntph36936.bamdiem_app.Model.thidauModel;
import fpoly.anhntph36936.bamdiem_app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main_DSTD extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rc_dstt;
    ArrayList<thidauModel> list;
    Dstd_ADT dstdAdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dstt);
        swipeRefreshLayout = findViewById(R.id.load_td);
        rc_dstt = findViewById(R.id.rc_dstd);

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
        Call<ArrayList<thidauModel>> call = api.getDSTD();
        call.enqueue(new Callback<ArrayList<thidauModel>>() {
            @Override
            public void onResponse(Call<ArrayList<thidauModel>> call, Response<ArrayList<thidauModel>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    list = response.body();
                    dstdAdt = new Dstd_ADT(list, Main_DSTD.this);
                    rc_dstt.setLayoutManager(new LinearLayoutManager(Main_DSTD.this));
                    rc_dstt.setAdapter(dstdAdt);
                } else {
                    Toast.makeText(Main_DSTD.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<thidauModel>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Main_DSTD.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}