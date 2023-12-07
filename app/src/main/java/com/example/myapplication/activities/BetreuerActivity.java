package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.fragments.BetreuteArbeitenFragment;
import com.example.myapplication.fragments.OffeneArbeitenFragment;
import com.example.myapplication.R;
import com.example.myapplication.fragments.ProfileDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BetreuerActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public BetreuerActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betreuer);

        Toolbar toolbar = findViewById(R.id.toolbarAddWork);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        FloatingActionButton fab = findViewById(R.id.fab_add_work);

        // Bei Drücken des Buttons kommt man auf die AddWorkActivity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BetreuerActivity.this, AddWorkActivity.class);
                startActivity(intent);
            }
        });
        setupViewPager(viewPager); //Für die Fragmente
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OffeneArbeitenFragment(), "Offene Arbeiten");
        adapter.addFragment(new BetreuteArbeitenFragment(), "Betreute Arbeiten");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Macht das Menü auf
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Wenn man auf die Items im Menü klickt, passieren die entsprechenden Dinge (Logout / Profil aufrufen)
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(BetreuerActivity.this, DeciderActivity.class);
            // FLAG_ACTIVITY_NEW_TASK und FLAG_ACTIVITY_CLEAR_TASK sorgen dafür, dass der Benutzer nicht
            // zum vorherigen Zustand der App zurückkehren kann, indem er die Zurück-Taste drückt.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();


            return true;
        } else if (id == R.id.action_profile) {

            openProfileDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Funktion, welche die Hintergrundfarbe des Menüs anpasst
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(menu.getItem(i).getTitle());
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            item.setTitle(s);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //öffnet das dialogfenster zum anpassen des Profils
    private void openProfileDialog() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfileDialogFragment profileDialog = new ProfileDialogFragment(userId);
        profileDialog.show(getSupportFragmentManager(), "ProfileDialogFragment");
    }
}