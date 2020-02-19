package com.halo.dictionary.mvp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.halo.dictionary.R;
import com.halo.dictionary.mvp.EditEntryPresenter;
import com.halo.dictionary.mvp.EditEntryView;
import com.halo.dictionary.mvp.impl.EditEntryPresenterImpl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditEntryDialogFragment extends DialogFragment implements EditEntryView {

    static final String ID_KEY = "processingId";

    private Long processingId;

    private EditEntryPresenter presenter;

    private EditText wordView;
    private EditText translationView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {

        if (getArguments() == null) {
            throw new IllegalStateException("Where is id?");
        } else {
            this.processingId = getArguments().getLong(ID_KEY);
        }

        this.presenter = new EditEntryPresenterImpl(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View fragmentView = inflater.inflate(R.layout.fragment_edit_entry, null);

        builder.setView(fragmentView)
                .setPositiveButton(R.string.save_button, (dialog, id) -> this.presenter.onSaveButtonClicked());

        this.wordView = fragmentView.findViewById(R.id.tvEditWord);
        this.translationView = fragmentView.findViewById(R.id.tvEditTranslation);

        final Dialog dialog = builder.create();

        this.presenter.onViewInitialized(this.processingId);

        return dialog;
    }

    @NonNull
    @Override
    public Long getEntryId() {
        return this.processingId;
    }

    @NonNull
    @Override
    public String getWordText() {
        return this.wordView.getText().toString();
    }

    @NonNull
    @Override
    public String getTranslationText() {
        return this.translationView.getText().toString();
    }

    @Override
    public void setWordText(final String text) {
        this.wordView.setText(text);
    }

    @Override
    public void setTranslationText(final String text) {
        this.translationView.setText(text);
    }

    @Override
    public void showMessage(@NonNull final String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void executeOnUiThread(@NonNull final Runnable task) {
        requireActivity().runOnUiThread(task);
    }

    @NonNull
    @Override
    public Context getViewContext() {
        final Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("No context");
        }
        return context;
    }

    @Override
    public void close() {

    }

}
