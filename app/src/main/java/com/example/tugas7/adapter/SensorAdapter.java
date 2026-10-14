package com.example.tugas7.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugas7.R;
import com.example.tugas7.model.SensorData;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    private final List<SensorData> sensorDataList;

    public SensorAdapter(List<SensorData> sensorDataList) {
        this.sensorDataList = sensorDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SensorData item = sensorDataList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvSensorTitle.setText(item.getTitle());
        holder.tvStatusBadge.setText(item.getStatusBadgeText());

        // Status Dot Tint
        Drawable dotDrawable = holder.viewStatusDot.getBackground();
        if (dotDrawable != null) {
            Drawable wrappedDrawable = DrawableCompat.wrap(dotDrawable.mutate());
            int color = ContextCompat.getColor(context, item.getStatusDotColorRes());
            DrawableCompat.setTint(wrappedDrawable, color);
        }

        int onSurfaceColor = MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorOnSurface);

        if (!item.isAvailable()) {
            // Unavailable styling
            holder.cardSensor.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_bg_unavailable));
            holder.tvPrimaryValue.setText(context.getString(R.string.sensor_unavailable));
            holder.tvPrimaryValue.setTextColor(ContextCompat.getColor(context, R.color.text_unavailable));

            holder.tvSubDetails.setVisibility(View.GONE);
            holder.progressBarSensor.setVisibility(View.GONE);
            holder.tvMinMax.setVisibility(View.GONE);
            holder.tvStatusDescription.setText(context.getString(R.string.sensor_unavailable_desc));
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_grey));
        } else {
            // Available styling
            holder.cardSensor.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_bg_available));

            holder.tvPrimaryValue.setText(item.getFormattedMainValue());
            holder.tvPrimaryValue.setTextColor(onSurfaceColor);

            holder.tvSubDetails.setVisibility(View.VISIBLE);
            holder.tvSubDetails.setText(item.getFormattedSubDetails());

            holder.progressBarSensor.setVisibility(View.VISIBLE);
            holder.progressBarSensor.setProgress(item.getProgressPercent());

            holder.tvMinMax.setVisibility(View.VISIBLE);
            holder.tvMinMax.setText(item.getFormattedMinMax());

            holder.tvStatusDescription.setText(item.getStatusDescriptionText());
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, item.getStatusDotColorRes()));
        }
    }

    @Override
    public int getItemCount() {
        return sensorDataList != null ? sensorDataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardSensor;
        View viewStatusDot;
        TextView tvSensorTitle;
        TextView tvStatusBadge;
        TextView tvPrimaryValue;
        TextView tvSubDetails;
        ProgressBar progressBarSensor;
        TextView tvMinMax;
        TextView tvStatusDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSensor = itemView.findViewById(R.id.cardSensor);
            viewStatusDot = itemView.findViewById(R.id.viewStatusDot);
            tvSensorTitle = itemView.findViewById(R.id.tvSensorTitle);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvPrimaryValue = itemView.findViewById(R.id.tvPrimaryValue);
            tvSubDetails = itemView.findViewById(R.id.tvSubDetails);
            progressBarSensor = itemView.findViewById(R.id.progressBarSensor);
            tvMinMax = itemView.findViewById(R.id.tvMinMax);
            tvStatusDescription = itemView.findViewById(R.id.tvStatusDescription);
        }
    }
}