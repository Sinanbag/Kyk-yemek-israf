package com.example.kykdonusum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kykdonusum.models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {
    private List<Meal> mealList;
    private Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public MealAdapter(Context context, List<Meal> mealList, DatabaseReference databaseReference) {
        this.context = context;
        this.mealList = mealList;
        this.mDatabase = databaseReference;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        Date mealDate = meal.getDate();
        
        // If meal date is null, use current date
        if (mealDate == null) {
            mealDate = new Date();
        }

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(mealDate);

        // Set the date at midnight for comparison
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        // Set meal date to midnight
        cal.setTime(mealDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date normalizedMealDate = cal.getTime();

        holder.dateText.setText(formattedDate);
        holder.typeText.setText(meal.getType());
        holder.menuText.setText(meal.getMenu());
        holder.priceText.setText(String.format(Locale.getDefault(), "%.2f ₺", meal.getPrice()));

        // Check if meal is cancelled
        if (meal.isCancelled()) {
            holder.cancelButton.setEnabled(false);
            holder.cancelButton.setText("İptal Edildi");
            holder.cancelButton.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
        } else if (normalizedMealDate.before(today)) {
            // Meal date has passed
            holder.cancelButton.setEnabled(false);
            holder.cancelButton.setText("Süresi Doldu");
            holder.cancelButton.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
        } else {
            holder.cancelButton.setEnabled(true);
            holder.cancelButton.setText("İptal Et");
            holder.cancelButton.setBackgroundTintList(context.getColorStateList(android.R.color.holo_red_light));

            holder.cancelButton.setOnClickListener(v -> {
                // Update the cancelled status in Firebase
                String mealId = meal.getId();
                if (mealId != null) {
                    mDatabase.child(mealId).child("cancelled").setValue(true)
                            .addOnSuccessListener(aVoid -> {
                                meal.setCancelled(true);
                                notifyItemChanged(holder.getAdapterPosition());
                                Toast.makeText(context, "Yemek başarıyla iptal edildi", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mealList != null ? mealList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView typeText;
        TextView menuText;
        TextView priceText;
        Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateTextView);
            typeText = itemView.findViewById(R.id.typeTextView);
            menuText = itemView.findViewById(R.id.menuTextView);
            priceText = itemView.findViewById(R.id.priceTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}