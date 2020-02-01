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
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.halo.dictionary.rcclrview.WordsRcclrViewAdapter;
import com.halo.dictionary.temp.WordsListPresenter;
import com.halo.dictionary.temp.WordsListPresenterImpl;
import com.halo.dictionary.temp.WordsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements WordsListView {

    private RecyclerView.Adapter rcclrViewAdapter;
    private WordsListPresenter presenter;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.presenter = new WordsListPresenterImpl();
        this.presenter.attachView(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.presenter.detachView();
    }

    private void init() {

        final RecyclerView mRcclrViewWords = findViewById(R.id.recycler_view_words);
        mRcclrViewWords.setHasFixedSize(true); // Improve performance
        mRcclrViewWords.setLayoutManager(new LinearLayoutManager(this));
        this.rcclrViewAdapter = new WordsRcclrViewAdapter(this.presenter);
        mRcclrViewWords.setAdapter(this.rcclrViewAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(
                    @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                View view = viewHolder.itemView;
                String idString = ((TextView) view.findViewById(R.id.tv_c_rcclr_view_words_id)).getText().toString();
                MainActivity.this.presenter.onWordSwiped(idString);
            }

        }).attachToRecyclerView(mRcclrViewWords);

        final FloatingActionButton mFabAddWord = findViewById(R.id.button_add);
        mFabAddWord.setOnClickListener(button -> MainActivity.this.presenter.onAddWordButtonClicked());

    }

    @Override
    public void goToAddWordScreen() {
        Intent intent = new Intent(getApplicationContext(), AddEntryActivity.class);
        startActivity(intent);
    }

    @Override
    public void refreshList() {
        this.rcclrViewAdapter.notifyDataSetChanged();
    }

}
