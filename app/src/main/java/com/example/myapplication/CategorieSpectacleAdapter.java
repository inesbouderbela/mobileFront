package com.example.myapplication;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.categorie;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CategorieSpectacleAdapter extends RecyclerView.Adapter<CategorieSpectacleAdapter.ViewHolder> {

    private List<categorie> categorieList;
    private Context context;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;


    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }

    public CategorieSpectacleAdapter(List<categorie> categorieList, Context context, OnCategoryClickListener listener) {
        this.categorieList = categorieList;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageView;
        TextView textView;
        GradientDrawable backgroundDrawable;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.catImg);
            textView = itemView.findViewById(R.id.catTxt);

            backgroundDrawable = (GradientDrawable) imageView.getBackground();
            if (backgroundDrawable == null) {
                backgroundDrawable = new GradientDrawable();
                backgroundDrawable.setShape(GradientDrawable.OVAL);
                imageView.setBackground(backgroundDrawable);
            }
        }

        public void bind(categorie categorie, boolean isSelected) {

            Glide.with(itemView.getContext())
                    .load(categorie.getImage())
                    .override(70, 70)
                    .centerInside()
                    .into(imageView);


            int backgroundColor = isSelected ?
                    ContextCompat.getColor(itemView.getContext(), R.color.white):
                    ContextCompat.getColor(itemView.getContext(), R.color.black2) ;


            int textColor = isSelected ?
                    ContextCompat.getColor(itemView.getContext(), R.color.white) :
                    ContextCompat.getColor(itemView.getContext(), R.color.black2);

            backgroundDrawable.setColor(backgroundColor);
            /*textView.setTextColor(textColor);*/
            textView.setText(categorie.getNom());


            textView.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categorieList.get(position), position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);


            if (listener != null) {
                listener.onCategoryClick(categorieList.get(selectedPosition).getNom());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorieList.size();
    }

    public categorie getSelectedItem() {
        return categorieList.get(selectedPosition);
    }
}