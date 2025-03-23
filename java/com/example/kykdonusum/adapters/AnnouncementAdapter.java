package com.example.kykdonusum.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kykdonusum.R;
import com.example.kykdonusum.models.Announcement;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private List<Announcement> announcements;

    public AnnouncementAdapter(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        
        holder.titleTextView.setText(announcement.getTitle());
        holder.contentTextView.setText(announcement.getContent());
        holder.dateTextView.setText(announcement.getDate());
        
        // Duyuru tipine göre kart rengini ayarla
        int cardColor;
        switch (announcement.getType()) {
            case "URGENT":
                cardColor = holder.itemView.getContext().getColor(android.R.color.holo_red_light);
                break;
            case "IMPORTANT":
                cardColor = holder.itemView.getContext().getColor(android.R.color.holo_orange_light);
                break;
            default:
                cardColor = holder.itemView.getContext().getColor(android.R.color.white);
                break;
        }
        holder.cardView.setCardBackgroundColor(cardColor);
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView;
        TextView contentTextView;
        TextView dateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
} 