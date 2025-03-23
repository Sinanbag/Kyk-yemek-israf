package com.example.kykdonusum.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kykdonusum.MealRatingActivity;
import com.example.kykdonusum.R;
import com.example.kykdonusum.models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {
    private List<Meal> meals;
    private Context context;
    private String userId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private OnMealCancelListener onMealCancelListener;

    public MealAdapter(Context context, List<Meal> meals, String userId) {
        this.context = context;
        this.meals = meals;
        this.userId = userId;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.dateText.setText(dateFormat.format(meal.getDate()));
        holder.menuText.setText(meal.getMenu());
        holder.priceText.setText(String.format(Locale.getDefault(), "%.2f TL", meal.getPrice()));
        holder.mealTypeText.setText(meal.getType());

        // Set today's date to noon for comparison
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        // Set meal date to noon
        cal.setTime(meal.getDate());
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date mealDate = cal.getTime();

        if (meal.isCancelled()) {
            holder.cancelButton.setEnabled(false);
            holder.cancelButton.setText("İptal Edildi");
            holder.statusText.setText("İptal Edildi");
            holder.statusText.setVisibility(View.VISIBLE);
        } else if (mealDate.before(today)) {
            holder.cancelButton.setEnabled(false);
            holder.statusText.setText("Süresi doldu");
            holder.statusText.setVisibility(View.VISIBLE);
            holder.rateButton.setVisibility(View.VISIBLE); // Show rate button for past meals
        } else {
            holder.cancelButton.setEnabled(true);
            holder.statusText.setVisibility(View.GONE);
            holder.rateButton.setVisibility(View.GONE); // Hide rate button for future meals
        }

        holder.cancelButton.setOnClickListener(v -> {
            if (!meal.isCancelled()) {
                meal.setCancelled(true);
                mDatabase.child("users").child(userId).child("meals").child(meal.getId()).setValue(meal)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Yemek başarıyla iptal edildi", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        })
                        .addOnFailureListener(e -> {
                            meal.setCancelled(false);
                            Toast.makeText(context, "Yemek iptal edilemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        holder.rateButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealRatingActivity.class);
            intent.putExtra("mealId", meal.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setOnMealCancelListener(OnMealCancelListener listener) {
        this.onMealCancelListener = listener;
    }

    public interface OnMealCancelListener {
        void onMealCancel(String mealId, double price);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView menuText;
        TextView priceText;
        TextView mealTypeText;
        TextView statusText;
        Button cancelButton;
        Button rateButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            menuText = itemView.findViewById(R.id.menuText);
            priceText = itemView.findViewById(R.id.priceText);
            mealTypeText = itemView.findViewById(R.id.mealTypeText);
            statusText = itemView.findViewById(R.id.statusText);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            rateButton = itemView.findViewById(R.id.rateButton);
        }
    }
} 