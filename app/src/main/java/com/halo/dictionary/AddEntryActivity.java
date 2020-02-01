package com.halo.dictionary;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.halo.dictionary.temp.AddEntryPresenter;
import com.halo.dictionary.temp.AddEntryPresenterImpl;
import com.halo.dictionary.temp.AddEntryView;

import androidx.appcompat.app.AppCompatActivity;

public class AddEntryActivity extends AppCompatActivity implements AddEntryView {

    private AddEntryPresenter presenter;
    private EditText tvWord;
    private EditText tvTranslation;
    private Button buttonSave;

    private static final String TAG = "AddEntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        this.presenter = new AddEntryPresenterImpl();
        this.presenter.attachView(this);

        this.tvWord = findViewById(R.id.tvWord);
        this.tvTranslation = findViewById(R.id.tvTranslation);
        this.buttonSave = findViewById(R.id.buttonSaveEntry);
        this.buttonSave.setOnClickListener(v -> AddEntryActivity.this.presenter.onSaveButtonClicked());

    }

    @Override
    public void focusWordEditView() {
        // TODO ннада?
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
    public void showMessage(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }


    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

}
