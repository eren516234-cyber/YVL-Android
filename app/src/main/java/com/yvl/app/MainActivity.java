package com.yvl.app;

import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yvl.app.fragment.*;
import com.yvl.app.util.ThemeManager;

public class MainActivity extends AppCompatActivity {

    private ThemeManager themeManager;
    private BottomNavigationView bottomNav;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeManager = ThemeManager.getInstance(this);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        applyTheme();

        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), false);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment f;
            if      (id == R.id.nav_home)     f = new HomeFragment();
            else if (id == R.id.nav_search)   f = new SearchFragment();
            else if (id == R.id.nav_together) f = new ListenTogetherFragment();
            else if (id == R.id.nav_library)  f = new LibraryFragment();
            else                              return false;
            switchFragment(f, true);
            return true;
        });
    }

    private void switchFragment(Fragment f, boolean animate) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        if (animate) {
            tx.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        }
        tx.replace(R.id.fragment_container, f);
        tx.commit();
        currentFragment = f;
    }

    public void applyTheme() {
        int bg   = themeManager.getBgColor();
        int surf = themeManager.getSurfaceColor();
        int txt  = themeManager.getTextColor();
        FrameLayout container = findViewById(R.id.fragment_container);
        if (container != null) container.setBackgroundColor(bg);
        bottomNav.setBackgroundColor(bg);
        bottomNav.setItemTextColor(android.content.res.ColorStateList.valueOf(txt));
        bottomNav.setItemIconTintList(android.content.res.ColorStateList.valueOf(txt));
    }

    @Override
    public void onBackPressed() {
        if (bottomNav.getSelectedItemId() != R.id.nav_home) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}
