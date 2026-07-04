package com.yvl.app;

import android.os.*;
import android.view.*;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yvl.app.fragment.*;
import com.yvl.app.util.ThemeManager;

public class MainActivity extends AppCompatActivity {
    private ThemeManager tm;
    private BottomNavigationView bottomNav;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tm = ThemeManager.getInstance(this);
        setContentView(R.layout.activity_main);
        applyTheme();

        bottomNav = findViewById(R.id.bottom_nav);
        if (savedInstanceState == null) switchFragment(new HomeFragment(), false);

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
        if (animate) tx.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        tx.replace(R.id.fragment_container, f).commit();
    }

    public void applyTheme() {
        int bg  = tm.getBgColor();
        int txt = tm.getTextColor();
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        FrameLayout container = findViewById(R.id.fragment_container);
        if (container != null) container.setBackgroundColor(bg);
        if (bottomNav == null) bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav != null) {
            bottomNav.setBackgroundColor(bg);
            android.content.res.ColorStateList states = android.content.res.ColorStateList.valueOf(txt);
            bottomNav.setItemTextColor(buildNavColorStateList(txt, tm.getAccentColor()));
            bottomNav.setItemIconTintList(buildNavColorStateList(txt, tm.getAccentColor()));
        }
    }

    private android.content.res.ColorStateList buildNavColorStateList(int unsel, int sel) {
        int[][] states = new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{} };
        int[] colors   = new int[]{ sel, unsel };
        return new android.content.res.ColorStateList(states, colors);
    }

    @Override public void onBackPressed() {
        if (bottomNav != null && bottomNav.getSelectedItemId() != R.id.nav_home)
            bottomNav.setSelectedItemId(R.id.nav_home);
        else super.onBackPressed();
    }
}
