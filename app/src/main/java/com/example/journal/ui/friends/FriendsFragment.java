package com.example.journal.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.journal.QRReader;
import com.example.journal.R;
import com.example.journal.databinding.FragmentFriendsBinding;

public class FriendsFragment extends Fragment {
    private FragmentFriendsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.friends_page_menu, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_qr:
                QRReader fragment = QRReader.newInstance();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.add(R.id.Constraint, fragment);
                ft.addToBackStack(null);
                ft.commit();
        }
        return true;
    }
}