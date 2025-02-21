package fpoly.anhntph36936.bamdiem_app.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fpoly.anhntph36936.bamdiem_app.R;

public class MainActivity extends AppCompatActivity {

    Button btn_gio, btn_diem, btn_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_gio = findViewById(R.id.btn_bamgio);
        btn_diem = findViewById(R.id.btn_diem);
        btn_view = findViewById(R.id.btn_view);

        btn_diem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main_ViTri.class));
            }
        });
        btn_gio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main_DSTD.class));
            }
        });
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main_View.class));
            }
        });
    }
}