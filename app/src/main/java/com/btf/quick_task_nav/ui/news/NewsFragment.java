package com.btf.quick_task_nav.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.btf.quick_task_nav.databinding.FragmentReflowBinding;

public class NewsFragment extends Fragment {

    private FragmentReflowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewsViewModel reflowViewModel =
                new ViewModelProvider(this).get(NewsViewModel.class);

        binding = FragmentReflowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textReflow;
        reflowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}