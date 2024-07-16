package fpoly.anhntph36936.bamdiem_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Main.Main_BamDiem;
import fpoly.anhntph36936.bamdiem_app.Main.Main_BamGio;
import fpoly.anhntph36936.bamdiem_app.Model.thidauModel;
import fpoly.anhntph36936.bamdiem_app.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dstd_ADT extends RecyclerView.Adapter<Dstd_ADT.DstdHolder>{
    ArrayList<thidauModel> list;
    Context context;

    public Dstd_ADT(ArrayList<thidauModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public DstdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dstd,parent, false);
        return new Dstd_ADT.DstdHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DstdHolder holder, @SuppressLint("RecyclerView") int position) {
        thidauModel model = list.get(position);
        holder.tv_stt.setText(String.valueOf(position+1));
        holder.tv_namen1.setText(model.getName_n1());
        holder.tv_namen2.setText(model.getName_n2());
        holder.tv_tt.setText(model.getSex()+ " - " +model.getWeight());

        holder.imv_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Main_BamGio.class);
                intent.putExtra("stt", position+1);
                intent.putExtra("diem_n1", model.getDiem_n1());
                intent.putExtra("diem_n2", model.getDiem_n2());
                intent.putExtra("thidauModel", model);
                sendDataToServer(model);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DstdHolder extends RecyclerView.ViewHolder {
        TextView tv_stt, tv_namen1, tv_namen2, tv_tt;
        ImageView imv_clock;
        public DstdHolder(@NonNull View itemView) {
            super(itemView);
            tv_stt = itemView.findViewById(R.id.tv_stt);
            tv_namen1 = itemView.findViewById(R.id.tv_namen1);
            tv_namen2 = itemView.findViewById(R.id.tv_namen2);
            tv_tt = itemView.findViewById(R.id.tv_tt);
            imv_clock = itemView.findViewById(R.id.imv_clock);
        }
    }
    private void sendDataToServer(thidauModel model) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOSTAPI.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HOSTAPI hostapi = retrofit.create(HOSTAPI.class);
        Call<ResponseBody> call = hostapi.sendData(model);
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
