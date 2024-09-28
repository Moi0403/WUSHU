package fpoly.anhntph36936.bamdiem_app.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    int maxLichSu = 2;
    String vitri;
    String id;
    bdiemModel item;
    Handler handler = new Handler();
    Handler handlerd = new Handler();
    Handler handlerx = new Handler();
    Runnable saveDiemDo;
    Runnable saveDiemXanh;
    int timed = 300;
    int timex = 300;
    boolean isRunning = false;
    private boolean isResetNotificationShown = false;
    private boolean isOnNotificationShown = false;
    private boolean isOffNotificationShown = false;
    int tempDiemdo = 0;
    int tempDiemxanh = 0;
    int count_d = 0;
    int count_x = 0;
    int MAX_COUNT = 2;
    private AlertDialog currentDialog;
    private static boolean isOnOffOn = false;


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


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

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
                if (count_d < MAX_COUNT) {
                    tempDiemdo++;
                    count_d++;
                    tv_d.setText(String.valueOf(diemdo + tempDiemdo));
                }
                handlerd.removeCallbacks(saveDiemDo);
                saveDiemDo = () -> {
                    diemdo += tempDiemdo;
                    updateDo(id, diemdo);
                    tempDiemdo = 0;
                    count_d = 0;
                    tv_d.setText(String.valueOf(diemdo));
                };
                handlerd.postDelayed(saveDiemDo, timed);
            }
        });

        view_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count_x < MAX_COUNT) {
                    tempDiemxanh++;
                    count_x++;
                    tv_x.setText(String.valueOf(diemxanh + tempDiemxanh));
                }
                handlerx.removeCallbacks(saveDiemXanh);
                saveDiemXanh = new Runnable() {
                    @Override
                    public void run() {
                        diemxanh += tempDiemxanh;
                        updateDiemXanh(id, diemxanh);
                        tempDiemxanh = 0;
                        count_x = 0;
                        tv_x.setText(String.valueOf(diemxanh));
                    }
                };
                handlerx.postDelayed(saveDiemXanh, timex);
            }
        });


        btn_trud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diemdo > 0 && count_d < MAX_COUNT) {
                    if (diemdo + tempDiemdo > 0) {
                        tempDiemdo--;
                        count_d++;
                        tv_d.setText(String.valueOf(diemdo + tempDiemdo));
                    } else {
                        tempDiemdo = -diemdo;
                        count_d++;
                        tv_d.setText("0");
                    }
                }
                handlerd.removeCallbacksAndMessages(null);
                handlerd.removeCallbacks(saveDiemDo);
                saveDiemDo = () -> {
                    if (diemdo >0){
                        diemdo += tempDiemdo;
                        updateDo(id, diemdo);
                        tempDiemdo = 0;
                        count_d = 0;
                        tv_d.setText(String.valueOf(diemdo));
                    }
                };
                handlerd.postDelayed(saveDiemDo, timed);
            }
        });

        btn_trux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diemxanh > 0 && count_x < MAX_COUNT) {
                    if (diemxanh + tempDiemxanh >0){
                        tempDiemxanh--;
                        count_x++;
                        tv_x.setText(String.valueOf(diemxanh + tempDiemxanh));
                    } else {
                        tempDiemxanh = -diemxanh;
                        count_x++;
                        tv_x.setText("0");
                    }
                }

                handlerx.removeCallbacksAndMessages(null);
                handlerx.removeCallbacks(saveDiemXanh);
                saveDiemXanh = new Runnable() {
                    @Override
                    public void run() {
                        if (diemxanh >0){
                            diemxanh += tempDiemxanh;
                            updateDiemXanh(id, diemxanh);
                            tempDiemxanh = 0;
                            count_x = 0;
                            tv_x.setText(String.valueOf(diemxanh));
                        }
                    }
                };
                handlerx.postDelayed(saveDiemXanh, timex);
            }
        });
    }

    private void updateDo(String id, int diemdo) {
        if (id != null && diemdo >= 0) {
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
                        Log.e("dddd","fffff");
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
        if (id != null && diemxanh >= 0) {
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

    private void load(String id) {
        if (id != null) {
            swipeRefreshLayout.setRefreshing(true);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HOSTAPI.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HOSTAPI api = retrofit.create(HOSTAPI.class);
            Call<ArrayList<bdiemModel>> call = api.getVtriId(id);
            call.enqueue(new Callback<ArrayList<bdiemModel>>() {
                @Override
                public void onResponse(Call<ArrayList<bdiemModel>> call, Response<ArrayList<bdiemModel>> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        bdiemModel data = response.body().get(0);
                        tv_d.setText(String.valueOf(diemdo));
                        tv_x.setText(String.valueOf(diemxanh));
                    } else {
                        Log.e("Main_BamDiem", "Không tìm thấy dữ liệu để reset");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<bdiemModel>> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);  // Dừng loading nếu thất bại
                    Log.e("Main_BamDiem", "Lỗi khi tải dữ liệu: " + t.getMessage());
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
                                            isResetNotificationShown = true;
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
                                        isOnOffOn = jsonObject.optBoolean("isOn", false);
//                                        Toast.makeText(Main_BamDiem.this, "isOnOffOn: " + isOnOffOn, Toast.LENGTH_SHORT).show();
                                        if (switchOnOff != null) {
                                            switchOnOff.setChecked(isOnOffOn);
                                        }
                                        if (isOnOffOn) {
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
                                case "updateScores":
                                    try {
                                        if (jsonObject.has("vitri") && jsonObject.has("diemdo") && jsonObject.has("diemxanh")) {
                                            String vtri = jsonObject.getString("vitri");
                                            if (vtri != null && vtri.equals(vitri)){
                                                int newRedScore = jsonObject.getInt("diemdo");
                                                int newBlueScore = jsonObject.getInt("diemxanh");
                                                Log.d("Debug", "New Red Score: " + newRedScore);
                                                Log.d("Debug", "New Blue Score: " + newBlueScore);
//                                                Log.d("Debug", "Received Data: " + jsonObject.toString());
                                                diemdo = newRedScore;
                                                diemxanh = newBlueScore;
                                                tv_d.setText(String.valueOf(newRedScore));
                                                tv_x.setText(String.valueOf(newBlueScore));
//                                                load(id);
//                                                Log.d("Debug", "ID: " +id);
//                                                Log.d("Debug", "Vtri: " +vtri);
                                            } else {
                                                Log.d("Debug", "Vtri: " +vtri);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
                t.printStackTrace();
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
    private void update() {
        swipeRefreshLayout.setRefreshing(true);
        bdiemModel model = new bdiemModel();
        int dd = model.getDiemdo();
        int dx = model.getDiemxanh();
        tv_d.setText(String.valueOf(dd));
        tv_x.setText(String.valueOf(dx));
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
        Window window = currentDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.TOP;
            layoutParams.y = 20;
            window.setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setAttributes(layoutParams);
        }
    }
    public static boolean isOnOff() {
        return isOnOffOn;
    }


}
