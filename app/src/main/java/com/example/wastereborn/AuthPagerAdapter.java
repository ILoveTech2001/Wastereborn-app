package com.example.wastereborn;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AuthPagerAdapter extends FragmentStateAdapter {
    public AuthPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ? new LoginFragment() : new SignupFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

