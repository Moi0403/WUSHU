package fpoly.anhntph36936.bamdiem_app.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Model.thidauModel;
import fpoly.anhntph36936.bamdiem_app.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main_BamGio extends AppCompatActivity {
    Button btn_r1, btn_r2, btn_r3, btn_batdau, btn_huy,  btn_nghi, btn_1p30s, btn_2p;
    ImageView btn_ts_td, btn_ts_gd, btn_ts_tx, btn_ts_gx;
    TextView tv_sotran, tv_tsd, tv_tsx;
    Button selectedButton = null;
    Button selectedButtonTime = null;
    String selectedRound = "";
    int minutes = 0;
    int seconds = 0;
    thidauModel item;
    int diem_n1;
    int diem_n2;
    String itemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bam_gio);
        tv_sotran = findViewById(R.id.tv_sotran);
        btn_r1 = findViewById(R.id.btn_r1);
        btn_r2 = findViewById(R.id.btn_r2);
        btn_r3 = findViewById(R.id.btn_r3);
        btn_batdau = findViewById(R.id.btn_batdau);
        btn_huy = findViewById(R.id.btn_exit);
        btn_nghi = findViewById(R.id.btn_nghi);
        btn_1p30s = findViewById(R.id.btn_1p30s);
        btn_2p = findViewById(R.id.btn_2p);
        btn_ts_td = findViewById(R.id.btn_ts_td);
        btn_ts_gd = findViewById(R.id.btn_ts_gd);
        btn_ts_tx = findViewById(R.id.btn_ts_tx);
        btn_ts_gx = findViewById(R.id.btn_ts_gx);
        tv_tsd = findViewById(R.id.tv_tsd);
        tv_tsx = findViewById(R.id.tv_tsx);

        Intent intent = getIntent();
        if (intent != null) {
            int stt = intent.getIntExtra("stt", 0); // Lấy giá trị stt từ Intent, mặc định là 0 nếu không tìm thấy
            int dn1 = intent.getIntExtra("diem_n1", 0);
            int dn2 = intent.getIntExtra("diem_n2", 0);
            tv_sotran.setText(String.valueOf(stt));
            diem_n1 = dn1;
            diem_n2 = dn2;
            item = (thidauModel) intent.getSerializableExtra("thidauModel");
            if (item != null) {
                itemId = item.get_id();
                diem_n1 = item.getDiem_n1();
                diem_n2 = item.getDiem_n2();
                tv_tsd.setText(String.valueOf(diem_n1));
                tv_tsx.setText(String.valueOf(diem_n2));
            }
        }
        btn_batdau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_BamGio.this, Main_Run.class);
                intent.putExtra("id", itemId);
                intent.putExtra("round", selectedRound);
                intent.putExtra("minutes", minutes);
                intent.putExtra("seconds", seconds);
                intent.putExtra("diem_n1", diem_n1);
                intent.putExtra("diem_n2", diem_n2);
                if (item != null) {
                    intent.putExtra("thidauModel", item); // Truyền đối tượng thidauModel hiện tại
                } else {
                    Log.e("Main_BamGio", "thidauModel is null"); // Log lỗi nếu item là null
                }
                startActivity(intent);
            }
        });
        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thidauModel model = new thidauModel();
                model.setRound("Chờ");
                model.setName_n1("Giáp đỏ");
                model.setName_n2("Giáp xanh");
                model.setDiem_n1(0);
                model.setDiem_n2(0);
                model.setProvince_n1("Đơn vị");
                model.setProvince_n2("Đơn vị");
                sendDataToServer(itemId, model);
                startActivity(new Intent(Main_BamGio.this, Main_DSTD.class));
            }
        });
        btn_nghi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thidauModel model = new thidauModel();
                model.setRound("Nghỉ giữa hiệp");
                model.setMinute(1);
                model.setSecond(0);
                model.setDiem_n1(diem_n1);
                model.setDiem_n2(diem_n2);
                sendDataToServer(itemId, model);

                Intent intent = new Intent(Main_BamGio.this, Main_Run.class);
                intent.putExtra("id", itemId);
                intent.putExtra("round", "Nghỉ giữa hiệp");
                intent.putExtra("minutes", 1);
                intent.putExtra("seconds", 0);
                intent.putExtra("diem_n1", diem_n1);
                intent.putExtra("diem_n2", diem_n2);
                if (item != null) {
                    intent.putExtra("thidauModel", item);
                } else {
                    Log.e("Main_BamGio", "thidauModel is null");
                }

                startActivity(intent);
            }
            });

        btn_r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(btn_r1, "Round 1");
            }
        });

        btn_r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(btn_r2, "Round 2");
            }
        });

        btn_r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(btn_r3, "Round 3");
            }
        });
        btn_1p30s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClickTime(btn_1p30s, 01, 30);
            }
        });

        btn_2p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClickTime(btn_2p, 02, 00);
            }
        });

        btn_ts_td.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diem_n1 < 3){
                    diem_n1++;
                    tv_tsd.setText(String.valueOf(diem_n1));
                    updateDiem(itemId);
                }
            }
        });

        btn_ts_gd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diem_n1 > 0 ){
                    diem_n1--;
                    tv_tsd.setText(String.valueOf(diem_n1));
                    updateDiem(itemId);
                }
            }
        });

        btn_ts_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diem_n2 < 3){
                    diem_n2++;
                    tv_tsx.setText(String.valueOf(diem_n2));
                    updateDiem(itemId);
                }
            }
        });

        btn_ts_gx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diem_n2 > 0) {
                    diem_n2--;
                    tv_tsx.setText(String.valueOf(diem_n2));
                    updateDiem(itemId);
                }
            }
        });

    }

    private void handleButtonClick(Button button, String roundName) {
        if (button == selectedButton) {
            selectedButton.setBackgroundResource(R.drawable.button_selector);
            selectedButton = null;
            selectedRound = null;
        } else {
            if (selectedButton != null) {
                selectedButton.setBackgroundResource(R.drawable.button_selector);
            }
            selectedButton = button;
                button.setBackgroundResource(R.drawable.button_pressed);
            selectedRound = roundName;
        }
    }

    private void handleButtonClickTime(Button button, int phut, int giay) {
        if (button == selectedButtonTime) {
            selectedButtonTime.setBackgroundResource(R.drawable.button_selector);
            selectedButtonTime = null;
            minutes = 0;
            seconds = 0;
            return;
        }

        if (selectedButtonTime != null) {
            selectedButtonTime.setBackgroundResource(R.drawable.button_selector);
        }

        selectedButtonTime = button;
        button.setBackgroundResource(R.drawable.button_pressed);
        minutes = phut;
        seconds = giay;
    }
    private void updateDiem(String id) {
        if (id != null) {
            thidauModel model = new thidauModel();
            model.setDiem_n1(diem_n1);
            model.setDiem_n2(diem_n2);
            updateDT(id, model);
            sendDataToServer(id, model);
        }
    }
    private void updateDT(String id, thidauModel model) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HOSTAPI.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HOSTAPI apiService = retrofit.create(HOSTAPI.class);
            Call<ArrayList<thidauModel>> call = apiService.updateTD(id, model);
            call.enqueue(new Callback<ArrayList<thidauModel>>() {
                @Override
                public void onResponse(Call<ArrayList<thidauModel>> call, Response<ArrayList<thidauModel>> response) {
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<thidauModel>> call, Throwable t) {

                }
            });
    }
    private void sendDataToServer(String id, thidauModel model) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOSTAPI.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HOSTAPI hostapi = retrofit.create(HOSTAPI.class);
        Call<ResponseBody> call = hostapi.updateData(id, model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("SERVER_RESPONSE", "Success: " + response.body().toString());
                } else {
                    Log.e("SERVER_RESPONSE", "Error: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SERVER_RESPONSE", "Failure: " + t.getMessage());
            }
        });

    }

}