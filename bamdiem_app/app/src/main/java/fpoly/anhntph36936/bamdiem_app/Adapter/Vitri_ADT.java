package fpoly.anhntph36936.bamdiem_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpoly.anhntph36936.bamdiem_app.API.HOSTAPI;
import fpoly.anhntph36936.bamdiem_app.Main.Main_BamDiem;
import fpoly.anhntph36936.bamdiem_app.Model.bdiemModel;
import fpoly.anhntph36936.bamdiem_app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Vitri_ADT extends RecyclerView.Adapter<Vitri_ADT.VitriHolder> {
    ArrayList<bdiemModel> list_vt;
    Context context;

    public Vitri_ADT(ArrayList<bdiemModel> list_vt, Context context) {
        this.list_vt = list_vt;
        this.context = context;
    }

    @NonNull
    @Override
    public VitriHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vitri,parent, false);
        return new VitriHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VitriHolder holder, @SuppressLint("RecyclerView") int position) {
        bdiemModel model = list_vt.get(position);
        holder.tv_vitri.setText(model.getVitri());

        holder.imv_vitri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Main_BamDiem.class);
                intent.putExtra("id", model.get_id());
                intent.putExtra("vitri", model.getVitri());
                intent.putExtra("diemdo", model.getDiemdo());
                intent.putExtra("diemxanh", model.getDiemxanh());
                intent.putExtra("bdiemModel", model);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list_vt.size();
    }

    public static class VitriHolder extends RecyclerView.ViewHolder{
        TextView tv_vitri;
        LinearLayout imv_vitri;
        public VitriHolder(@NonNull View itemView) {
            super(itemView);
            tv_vitri = itemView.findViewById(R.id.tv_vitr);
            imv_vitri = itemView.findViewById(R.id.item_vtr);
        }
    }
}
