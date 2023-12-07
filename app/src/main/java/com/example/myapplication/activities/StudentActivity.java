package com.example.myapplication.activities;

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

import com.example.myapplication.fragments.BetreuerFragmentStudent;
import com.example.myapplication.fragments.ArbeitenFragmentStudent;
import com.example.myapplication.R;
import com.example.myapplication.fragments.ProfileDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {
    //die activity für studenten, um die fragmente zu laden

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toolbar toolbar = findViewById(R.id.toolbar_student);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager_student);
        tabLayout = findViewById(R.id.tabs_student);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Fragmente werden im Viewpager vorbereitet
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BetreuerFragmentStudent(), "Betreuer");
        adapter.addFragment(new ArbeitenFragmentStudent(), "Arbeiten");
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

    //Menü wird initialisiert
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Logout und Profil Funktion des Menü initialisieren
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(StudentActivity.this, DeciderActivity.class);
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

    //Menü Hintergrundfarbe anpassen
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(menu.getItem(i).getTitle());
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            item.setTitle(s);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    //Öffnet das Dialogframent zum Anpassen des Profils
    private void openProfileDialog() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfileDialogFragment profileDialog = new ProfileDialogFragment(userId);
        profileDialog.show(getSupportFragmentManager(), "ProfileDialogFragment");
    }
}
