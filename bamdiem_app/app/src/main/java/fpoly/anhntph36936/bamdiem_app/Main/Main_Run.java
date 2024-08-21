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
            pauseTimer();
            stopTemporaryTimer();
            minutes = 0;
            seconds = 0;
            round = "Kết thúc -  " + round;
            updateClock(id);
            finish();
        });

        btn_10s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                btn_huy.setVisibility(View.GONE);
                btn_huys.setVisibility(View.VISIBLE);
                stopTemporaryTimer();
                startTemporaryTimer(11000);
            }
        });



        btn_huys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTemporaryTimer();
                btn_huy.setVisibility(View.VISIBLE);
                btn_huys.setVisibility(View.GONE);
                startTimer();
            }
        });

        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTemporaryTimerRunning) {
                    if (isRunning) {
                        pauseTemporaryTimer();
                        btn_run.setText("Tiếp tục");
                        sendWebSocketMessage("on");
                    } else {
                        startTemporaryTimer(previousTimeInMillis);
                        btn_run.setText("Dừng");
                        sendWebSocketMessage("off");
                    }
                } else {
                    if (isRunning) {
                        pauseTimer();
                        btn_run.setText("Tiếp tục");
                        sendWebSocketMessage("on");
                    } else {
                        startTimer();
                        btn_run.setText("Dừng");
                        sendWebSocketMessage("off");
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
                sendWebSocketTime("updateTime", minutes, seconds);
                Log.d("TimerCheck", "Minutes: " + minutes + " Seconds: " + seconds);

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
                sendWebSocketTime("updateTime", minutes, seconds);
                Log.d("TimerCheck", "Minutes: " + minutes + " Seconds: " + seconds);

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
        sendWebSocketMessage("off");
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
    private void sendWebSocketMessage(String action) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", action);
            if (webSocket != null) {
                webSocket.send(jsonObject.toString());
                Log.d("WebSocket", "Sent: " + jsonObject.toString());
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
                Log.d("WebSocket", "Connected");
                Log.d("WebSocket", "Response: " + response.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket", "Received: " + text);
                try {
                    JSONObject data = new JSONObject(text);
                    if (data.has("action")) {
                        String action = data.getString("action");
                        runOnUiThread(() -> {
                            switch (action) {
                                case "updateTime":
                                    if (data.has("minute") && data.has("second")) {
                                        int minute = data.optInt("minute", 0);
                                        int second = data.optInt("second", 0);
                                        updateTimerDisplay((minute * 60 + second) * 1000);
                                        Log.d("WebSocket", "Updated Minute: " + minute + " Second: " + second);
                                    }
                                    break;
                            }
                        });
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
    private void sendWebSocketTime(String action, int minutes, int seconds) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", action);
            jsonObject.put("minute", minutes);
            jsonObject.put("second", seconds);
            if (webSocket != null) {
                webSocket.send(jsonObject.toString());
                Log.d("WebSocket", "SentTime: " + jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
