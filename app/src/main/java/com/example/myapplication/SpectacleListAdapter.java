package com.example.myapplication;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Model.Spectacle;
import com.example.myapplication.Model.Spectacle_Detail;
import com.example.myapplication.Model.TopSpectacle;

import java.util.ArrayList;

public class SpectacleListAdapter extends RecyclerView.Adapter<SpectacleListAdapter.ViewHolder> {
    ArrayList<TopSpectacle> items;
    Context context;

    public SpectacleListAdapter(ArrayList<TopSpectacle> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflote = LayoutInflater.from(parent.getContext()).inflate(R.layout.spectacle_viewholder, parent, false);
        return new ViewHolder(inflote);
    }

    @Override

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopSpectacle topSpectacle  = items.get(position);
        holder.titleTxt.setText(topSpectacle.getTitre());
        String fullUrl = Constants.BASE_URL + topSpectacle.getSecondaryImage();

        Log.d("SpectacleListAdapter", "Image URL top: " + topSpectacle.getSecondaryImage());
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .load(fullUrl)
                .apply(requestOptions)
                .placeholder(R.drawable.intro_logo)
                .into(holder.pic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                Intent intent=new Intent(context,DetailActivity.class);
                intent.putExtra("seanceId", items.get(currentPosition).getSeanceId());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.nametxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}

