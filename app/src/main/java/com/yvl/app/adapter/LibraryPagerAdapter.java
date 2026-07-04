package com.yvl.app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.yvl.app.fragment.*;

public class LibraryPagerAdapter extends FragmentStateAdapter {
    public LibraryPagerAdapter(Fragment f) { super(f); }
    @NonNull @Override
    public Fragment createFragment(int pos) {
        switch(pos) {
            case 1: return new LibraryPlaylistsFragment();
            case 2: return new LibraryAlbumsFragment();
            case 3: return new LibrarySongsFragment();
            default: return new LibraryArtistsFragment();
        }
    }
    @Override public int getItemCount() { return 4; }
}
