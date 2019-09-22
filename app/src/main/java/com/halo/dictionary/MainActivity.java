/*
 * TODO s:
 * - удобная красивая таблица
 * - различные сортировки (в том числе случайные)
 * - представления ру-анг/анг-ру
 * - поиск
 * - случайный отбор 5-10-20 слов для проверки
 * - подсказка по таймеру (в уведомления) - firebase
 * - синхронизация?
 */

package com.halo.dictionary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.halo.dictionary.rcclrview.WordsRcclrViewAdapter;
import com.halo.dictionary.sql.WordDbHelper;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRcclrViewWords;
    private RecyclerView.Adapter mRcclrViewWordsAdapter;
    private RecyclerView.LayoutManager mRcclrViewWordsLayoutManager;

    private WordDbHelper mDbHelperWords;

    private static final String TAG = "MainActivity";

    @Override
    protected void onDestroy() {
        mDbHelperWords.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Long position = mDbHelperWords.getLatestWordIndex();
        if (position != null) {
            mRcclrViewWords.getLayoutManager().scrollToPosition(position.intValue());
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRcclrViewWords = (RecyclerView) findViewById(R.id.recycler_view_words);
        mRcclrViewWords.setHasFixedSize(true); // Improve performance
        mRcclrViewWordsLayoutManager = new LinearLayoutManager(this);
        mRcclrViewWords.setLayoutManager(mRcclrViewWordsLayoutManager);

        mDbHelperWords = WordDbHelper.getInstance(this);

        final Cursor cursor = mDbHelperWords.getAllWordsEntries();
        mRcclrViewWordsAdapter = new WordsRcclrViewAdapter(cursor, this);
        mRcclrViewWords.setAdapter(mRcclrViewWordsAdapter);

        /*
         * Remove word entry swiping
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(
                    RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                View view = viewHolder.itemView;
                String idString = ((TextView) view.findViewById(R.id.tv_c_rcclr_view_words_id)).getText().toString();
                Long id = Long.parseLong(idString);
                mDbHelperWords.removeWordEntry(id);
                WordsRcclrViewAdapter.getInstance().swapCursor(mDbHelperWords.getAllWordsEntries());
            }

        }).attachToRecyclerView(mRcclrViewWords);

        /*
         * On add button click start new activity to add word
         */
        final Button button = (Button) findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddWordActivity.class);
                startActivity(intent);
            }

        });

        final Button sortButton = findViewById(R.id.button_resort);
        //sortButton.
    }
}
