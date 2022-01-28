package com.example.journal.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.journal.R;
import com.example.journal.databinding.FragmentFriendsBinding;

public class FriendsFragment extends Fragment {
    private FragmentFriendsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}