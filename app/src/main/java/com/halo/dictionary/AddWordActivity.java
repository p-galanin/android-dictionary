package com.halo.dictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.halo.dictionary.rcclrview.WordsRcclrViewAdapter;
import com.halo.dictionary.sql.WordDbHelper;

public class AddWordActivity extends AppCompatActivity {

    private EditText mTvWord;
    private EditText mTvTranslation;

    private static final String TAG = "AddWordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        mTvWord = (EditText) findViewById(R.id.tvWord);
        mTvTranslation = (EditText) findViewById(R.id.tvTranslation);
    }

    /**
     * Добавление записи о слове и переводе по нажатию кнопки
     *
     * @param view
     */
    public void addWordEntry(View view) {
        final String newWord = mTvWord.getText().toString();
        final String translation = mTvTranslation.getText().toString();

        final WordDbHelper wordDbHelper = WordDbHelper.getInstance();
        final WordsRcclrViewAdapter wordsViewAdapter = WordsRcclrViewAdapter.getInstance();

        wordDbHelper.createWordEntryAndSetLatestIndex(newWord, translation);
        wordsViewAdapter.swapCursor(WordDbHelper.getInstance().getAllWordsEntries());

        final Toast toast = Toast.makeText(view.getContext(), "Added", Toast.LENGTH_SHORT);
        toast.show();

        this.finish();
    }
}
