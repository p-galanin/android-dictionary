package com.halo.dictionary.mvp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.halo.dictionary.R;
import com.halo.dictionary.mvp.AddEntryPresenter;
import com.halo.dictionary.mvp.AddEntryView;

import java.util.Optional;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddEntryActivity extends AppCompatActivity implements AddEntryView {

    @Inject
    AddEntryPresenter presenter;

    private EditText tvWord;

    private EditText tvTranslation;

    private Button buttonSave;

    private static final String TAG = "AddEntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        this.tvWord = findViewById(R.id.tvWord);
        this.tvTranslation = findViewById(R.id.tvTranslation);
        this.buttonSave = findViewById(R.id.buttonSaveEntry);
        this.buttonSave.setOnClickListener(v -> AddEntryActivity.this.presenter.onSaveButtonClicked());

        if (this.tvWord.requestFocus()) {
            getInputMethodManager().ifPresent(manager -> manager.showSoftInput(this.tvWord, InputMethodManager.SHOW_IMPLICIT));
        }
    }

    @Override
    public String getEnteredWord() {
        return this.tvWord.getText().toString();
    }

    @Override
    public String getEnteredTranslation() {
        return this.tvTranslation.getText().toString();
    }

    @Override
    public void showMessage(@NonNull final String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Context getViewContext() {
        return getApplicationContext();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void showProgress() {
        // TODO
    }

    @Override
    public void stopProgress() {
        // TODO
    }

    @Override
    public void executeOnUiThread(@NonNull final Runnable task) {
        runOnUiThread(task);
    }

    @NonNull
    private Optional<InputMethodManager> getInputMethodManager() {
        return Optional.ofNullable((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
    }
}
