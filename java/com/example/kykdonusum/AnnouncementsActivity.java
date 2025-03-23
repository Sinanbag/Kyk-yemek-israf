package com.example.kykdonusum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.kykdonusum.adapters.AnnouncementAdapter;
import com.example.kykdonusum.models.Announcement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnnouncementsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private DatabaseReference mDatabase;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        // ActionBar ayarları
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Duyurular");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        // View'ları bağla
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        // RecyclerView ayarları
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        announcements = new ArrayList<>();
        adapter = new AnnouncementAdapter(announcements);
        recyclerView.setAdapter(adapter);

        // Firebase başlat
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Duyuruları yükle
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        mDatabase.child("announcements").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                announcements.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Announcement announcement = snapshot.getValue(Announcement.class);
                    if (announcement != null) {
                        announcements.add(announcement);
                    }
                }

                // Tarihe göre sırala (en yeni en üstte)
                Collections.sort(announcements, (a1, a2) -> 
                    a2.getDate().compareTo(a1.getDate()));

                progressBar.setVisibility(View.GONE);
                
                if (announcements.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("Duyurular yüklenirken hata oluştu");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 