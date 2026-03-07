package dev.mamii.instafel.updater.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.app.Dialog;
import android.view.View;

public class MaterialListPreference extends ListPreferenceDialogFragmentCompat {
    private int mWhichButtonClicked = 0;
    private boolean onDialogClosedWasCalledFromOnDismiss = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getPreference().getDialogTitle())
                .setIcon(getPreference().getDialogIcon())
                .setPositiveButton(getPreference().getPositiveButtonText(), this)
                .setNegativeButton(getPreference().getNegativeButtonText(), this);

        if (context != null) {
            View contentView = onCreateDialogView(context);
            if (contentView != null) {
                onBindDialogView(contentView);
                builder.setView(contentView);
            } else {
                builder.setMessage(getPreference().getDialogMessage());
            }
        }

        onPrepareDialogBuilder(builder);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mWhichButtonClicked = which;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        onDialogClosedWasCalledFromOnDismiss = true;
        super.onDismiss(dialog);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (onDialogClosedWasCalledFromOnDismiss) {
            onDialogClosedWasCalledFromOnDismiss = false;
            super.onDialogClosed(mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE);
        } else {
            super.onDialogClosed(positiveResult);
        }
    }
}