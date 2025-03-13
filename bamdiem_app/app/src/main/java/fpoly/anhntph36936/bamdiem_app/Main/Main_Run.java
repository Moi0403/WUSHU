package fpoly.anhntph36936.bamdiem_app.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Model.thidauModel;
import fpoly.anhntph36936.bamdiem_app.R;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main_Run extends AppCompatActivity {
    TextView tv_round, tv_run;
    Button btn_huy, btn_run, btn_10s, btn_huys;
    private CountDownTimer countDownTimer;
    private CountDownTimer temporaryCountDownTimer;
    private boolean isRunning = false;
    private boolean isTemporaryTimerRunning = false;
    private long timeLeftInMillis;
    private long previousTimeInMillis;
    private long initialTimeInMillis;
    private long time10s;
    thidauModel item;
    int minutes, seconds, diem_n1, diem_n2;
    String round = "";
    String id;
    WebSocket webSocket;
    boolean soundPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_run);
        tv_round = findViewById(R.id.tv_round);
        tv_run = findViewById(R.id.tv_run);
        btn_huy = findViewById(R.id.btn_huy);
        btn_huys = findViewById(R.id.btn_huys);
        btn_run = findViewById(R.id.btn_run);
        btn_10s = findViewById(R.id.btn_10s);
        btn_huys.setVisibility(View.GONE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            round = intent.getStringExtra("round");
            diem_n1 = intent.getIntExtra("diem_n1", 0);
            diem_n2 = intent.getIntExtra("diem_n2", 0);
            minutes = intent.getIntExtra("minutes", 0);
            seconds = intent.getIntExtra("seconds", 0);
            item = (thidauModel) intent.getSerializableExtra("thidauModel");

            tv_round.setText(round);
            initialTimeInMillis = (minutes * 60 + (seconds + 1)) * 1000;
            timeLeftInMillis = initialTimeInMillis;
            updateTimerDisplay(timeLeftInMillis);
        }

        initWebSocket();

        btn_huy.setOnClickListener(v -> {
            if (Main_BamDiem.isOnOff()) {
                Toast.makeText(Main_Run.this, "Không thể tắt khi onoff đang bật", Toast.LENGTH_SHORT).show();
            } else {
                // Thực hiện hành động tắt
                pauseTimer();
                stopTemporaryTimer();
                minutes = 0;
                seconds = 0;
                round = "KẾT THÚC -  " + round;
                updateClock(id);
                finish();
            }
        });


        btn_10s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Main_BamDiem.isOnOff()) {
                    Toast.makeText(Main_Run.this, "Không thể tắt khi onoff đang bật", Toast.LENGTH_SHORT).show();
                } else {
                    pauseTimer();
                    btn_huy.setVisibility(View.GONE);
                    btn_huys.setVisibility(View.VISIBLE);
                    stopTemporaryTimer();
                    startTemporaryTimer(11000);
                }
            }
        });



        btn_huys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Main_BamDiem.isOnOff()) {
                    Toast.makeText(Main_Run.this, "Không thể tắt khi onoff đang bật", Toast.LENGTH_SHORT).show();
                } else {
                    stopTemporaryTimer();
                    btn_huy.setVisibility(View.VISIBLE);
                    btn_huys.setVisibility(View.GONE);
                    startTimer();
                }

            }
        });

        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main_Run", "Checking isOnOff before btn_run click: " + Main_BamDiem.isOnOff());

                if (Main_BamDiem.isOnOff()) {
                    Toast.makeText(Main_Run.this, "Không thể thao tác khi cấm dùng đang bật", Toast.LENGTH_SHORT).show();
                    return;
                }

                    if (isTemporaryTimerRunning) {
                        if (isRunning) {
                            pauseTemporaryTimer();
                            btn_run.setText("Tiếp tục");
                        } else {
                            startTemporaryTimer(previousTimeInMillis);
                            btn_run.setText("Dừng");
                        }
                    } else {
                        if (isRunning) {
                            pauseTimer();
                            btn_run.setText("Tiếp tục");
                        } else {
                            startTimer();
                            btn_run.setText("Dừng");
                        }
                    }
                }

        });

        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay(timeLeftInMillis);
                updateClock(id);
//                if (millisUntilFinished <= 10000 && millisUntilFinished == 10000) {
//                    sendSoundControlMessage("playSound");
//                }
            }
            @Override
            public void onFinish() {
                isRunning = false;
                tv_run.setText("00:00");
                btn_run.setText("Tiếp tục");
                updateClock(id);
            }
        }.start();
        isRunning = true;
        btn_run.setText("Dừng");
    }
    private void startTemporaryTimer(long duration) {
        previousTimeInMillis = duration;
        temporaryCountDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                previousTimeInMillis = millisUntilFinished;
                updateTimerDisplay(previousTimeInMillis);
                updateClock(id);
            }

            @Override
            public void onFinish() {
                isTemporaryTimerRunning = false;
                updateTimerDisplay(timeLeftInMillis);
                btn_huy.setVisibility(View.VISIBLE);
                btn_huys.setVisibility(View.GONE);
                updateClock(id);
                startTimer();
            }
        }.start();

        isRunning = true;
        isTemporaryTimerRunning = true;
        btn_run.setText("Dừng");
    }


    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            previousTimeInMillis = timeLeftInMillis;
        }
        isRunning = false;
        btn_run.setText("Tiếp tục");
    }

    private void pauseTemporaryTimer() {
        if (temporaryCountDownTimer != null) {
            temporaryCountDownTimer.cancel();
        }
        isRunning = false;
        btn_run.setText("Tiếp tục");
    }
    private void stopTemporaryTimer() {
        if (temporaryCountDownTimer != null) {
            temporaryCountDownTimer.cancel();
        }
        isTemporaryTimerRunning = false;
//        sendWebSocketMessage("off");
    }

    private void updateTimerDisplay(long millisUntilFinished) {
        minutes = (int) (millisUntilFinished / 1000) / 60;
        seconds = (int) (millisUntilFinished / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        tv_run.setText(timeLeftFormatted);
    }
    private void updateClock(String id) {
        if (id != null) {
            thidauModel model = new thidauModel();
            model.set_id(id);
            model.setRound(round);
            model.setDiem_n1(diem_n1);
            model.setDiem_n2(diem_n2);
            model.setMinute(minutes);
            model.setSecond(seconds);
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
    private void sendSoundControlMessage(String soundAction) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "soundControl");
            jsonObject.put("soundAction", soundAction);
            if (webSocket != null) {
                webSocket.send(jsonObject.toString());
                Log.d("WebSocket", "Sent_Main_Run: " + jsonObject.toString());
            } else {
                Log.d("WebSocket", "WebSocket is null. Cannot send message.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Request request = new Request.Builder()
                .url("ws://" + HOSTAPI.HOST + "/ws")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket_Main_Run", "Connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String action = jsonObject.optString("action");

                    if ("soundControl".equals(action)) {
                        String soundAction = jsonObject.optString("soundAction");
                        if ("playSound".equals(soundAction)) {

                        } else if ("stopSound".equals(soundAction)) {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(code, reason);
                Log.d("WebSocket", "Closing: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d("WebSocket", "Closed: " + reason);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "App closed");
        }
    }


}
