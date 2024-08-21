package fpoly.anhntph36936.bamdiem_app.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Model.bdiemModel;
import fpoly.anhntph36936.bamdiem_app.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;  // Retrofit's Response
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main_BamDiem extends AppCompatActivity {

    LinearLayout view_d, view_x;
    TextView tv_d, tv_x, tv_vt;
    Button btn_trud, btn_trux;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchOnOff;
    private SwipeRefreshLayout swipeRefreshLayout;
    WebSocket webSocketListener;
    int diemdo = 0;
    int diemxanh = 0;
    String vitri;
    String id;
    bdiemModel item;
    Handler handler = new Handler();
    boolean isRunning = false;
    private boolean isResetNotificationShown = false;
    private boolean isOnNotificationShown = false;
    private boolean isOffNotificationShown = false;
    int tempDiemdo = 0;
    int tempDiemxanh = 0;
    int secondlast = 130;
    private AlertDialog currentDialog;
    boolean isOn;


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

        swipeRefreshLayout.setEnabled(false);

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

        initWebSocket();

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
                }, secondlast);
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
                }, secondlast);
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
                    }, secondlast);
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
                    }, secondlast);
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
                        // Xử lý phản hồi thành công
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
                        // Xử lý phản hồi thành công
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
        if (id != null) {
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
                        // Xử lý phản hồi thành công
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

    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Request request = new Request.Builder()
                .url("ws://" + HOSTAPI.HOST + "/ws")
                .build();

        webSocketListener = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket", "Connected");
                Log.d("WebSocket", "Response: " + response.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket", "Message received: " + text);
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    Log.d("WebSocket", "Parsed JSON: " + jsonObject.toString());

                    if (jsonObject.has("action")) {
                        String action = jsonObject.getString("action");
                        runOnUiThread(() -> {
                            switch (action) {
                                case "resetAll":
                                    if (!isResetNotificationShown) {
                                        runOnUiThread(() -> {
                                            resetUI();
                                            Toast.makeText(Main_BamDiem.this, "Server reset", Toast.LENGTH_SHORT).show();
                                            isResetNotificationShown = true; // Đặt cờ thông báo
                                        });
                                        resetNotificationFlags();
                                    }
                                    break;
                                case "on":
                                    if (!isOnNotificationShown) {
                                        Toast.makeText(Main_BamDiem.this, "Switch is turned on", Toast.LENGTH_SHORT).show();
                                        isOnNotificationShown = true;
                                        resetNotificationFlags();
                                    }
                                    break;

                                case "off":
                                    if (!isOffNotificationShown) {
                                        Toast.makeText(Main_BamDiem.this, "Switch is turned off", Toast.LENGTH_SHORT).show();
                                        isOffNotificationShown = true;
                                        resetNotificationFlags();
                                    }
                                    break;

                                case "statusUpdate":
                                    if (jsonObject.has("isOn")) {
                                        isOn = jsonObject.optBoolean("isOn", false);
                                        if (switchOnOff != null) {
                                            switchOnOff.setChecked(isOn);
                                        }
                                        if (isOn) {
                                            showDialog("Cấm dùng");
                                        } else {
                                            if (currentDialog != null && currentDialog.isShowing()) {
                                                currentDialog.dismiss();
                                                currentDialog = null;
                                            }
                                        }
                                        resetNotificationFlags();
                                    }
                                    break;
                                default:
                                    Log.w("WebSocket", "Unknown action: " + action);
                                    break;
                            }
                        });
                    } else {
                        Log.e("WebSocket", "No action found in message: " + text);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("WebSocket", "Invalid JSON received: " + text);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e("WebSocket", "Error: " + t.getMessage());
                t.printStackTrace(); // In chi tiết lỗi
                if (response != null) {
                    Log.e("WebSocket", "Response: " + response.message());
                }
            }
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d("WebSocket", "Closed: " + reason);
            }
        });
    }
    private void resetUI() {
        swipeRefreshLayout.setRefreshing(true);
        diemdo = 0;
        diemxanh = 0;
        tv_d.setText(String.valueOf(diemdo));
        tv_x.setText(String.valueOf(diemxanh));
        handler.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketListener != null) {
            webSocketListener.close(1000, "App closed");
        }
    }
    private void resetNotificationFlags() {
        handler.postDelayed(() -> {
            isResetNotificationShown = false;
            isOnNotificationShown = false;
            isOffNotificationShown = false;
        }, 1000);
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Thông báo !")
                .setIcon(R.drawable.thongbao)
                .setMessage(message)
                .setCancelable(false);
        currentDialog = builder.create();
        currentDialog.show();
    }

}
