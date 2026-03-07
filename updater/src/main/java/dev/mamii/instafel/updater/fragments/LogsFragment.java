package dev.mamii.instafel.updater.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dev.mamii.instafel.updater.utils.LogUtils;
import dev.mamii.instafel.updater.R;

public class LogsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        TextView logView = view.findViewById(R.id.logs_view);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
        LogUtils logUtils = new LogUtils(getActivity());
        logView.setText(logUtils.readLog());
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.runtime_log_cleared), Toast.LENGTH_SHORT).show();
                logUtils.clearLog();
                logView.setText(logUtils.readLog());
            }
        });

        return view;
    }
}