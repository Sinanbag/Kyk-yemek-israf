package com.example.kykdonusum.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kykdonusum.R;
import com.example.kykdonusum.models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CancelledMealAdapter extends RecyclerView.Adapter<CancelledMealAdapter.ViewHolder> {
    private List<Meal> cancelledMeals;
    private Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private OnMealRestoreListener restoreListener;

    public interface OnMealRestoreListener {
        void onMealRestore(Meal meal);
    }

    public CancelledMealAdapter(Context context, List<Meal> cancelledMeals, OnMealRestoreListener listener) {
        this.context = context;
        this.cancelledMeals = cancelledMeals;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
        this.restoreListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancelled_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = cancelledMeals.get(position);
        Date mealDate = meal.getDate();
        
        // If meal date is null, use current date
        if (mealDate == null) {
            mealDate = new Date();
        }

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(mealDate);

        holder.dateText.setText(formattedDate);
        holder.typeText.setText(meal.getType());
        holder.menuText.setText(meal.getMenu());
        holder.priceText.setText(String.format(Locale.getDefault(), "%.2f TL", meal.getPrice()));
        holder.statusText.setText("İptal Edildi");

        holder.restoreButton.setOnClickListener(v -> {
            meal.setCancelled(false);
            restoreListener.onMealRestore(meal);
            // The actual removal from the list will be handled by the Firebase listener
        });
    }

    private void updateUserWallet(String userId, double amount) {
        mDatabase.child("users").child(userId).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Double currentBalance = dataSnapshot.child("walletBalance").getValue(Double.class);
                        if (currentBalance != null) {
                            double newBalance = currentBalance + amount;
                            mDatabase.child("users").child(userId).child("walletBalance")
                                    .setValue(newBalance);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return cancelledMeals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView typeText;
        TextView menuText;
        TextView priceText;
        TextView statusText;
        Button restoreButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            typeText = itemView.findViewById(R.id.typeText);
            menuText = itemView.findViewById(R.id.menuText);
            priceText = itemView.findViewById(R.id.priceText);
            statusText = itemView.findViewById(R.id.statusText);
            restoreButton = itemView.findViewById(R.id.restoreButton);
        }
    }
} 