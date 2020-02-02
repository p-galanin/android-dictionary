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
package com.halo.dictionary.mvp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.halo.dictionary.R;
import com.halo.dictionary.mvp.Utils;
import com.halo.dictionary.mvp.ui.rcclrview.WordsRcclrViewAdapter;
import com.halo.dictionary.mvp.WordsListPresenter;
import com.halo.dictionary.mvp.impl.WordsListPresenterImpl;
import com.halo.dictionary.mvp.WordsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements WordsListView {

    private RecyclerView.Adapter rcclrViewAdapter;
    private WordsListPresenter presenter;
    private ProgressBar progressBar;

    private static final String TAG = "MainActivity";
    private static final int CHOOSE_FILE_CODE = 1;
    private static final int CHOOSE_DIRECTORY_CODE = 2;
    private static final int REQUEST_PERMISSION_CODE = 10;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.presenter = new WordsListPresenterImpl(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.presenter.detachView();
        this.presenter.onFinish();
    }

    private void init() {

        // TODO smart hide on scroll https://guides.codepath.com/android/using-the-app-toolbar
        setSupportActionBar(findViewById(R.id.main_toolbar));

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

        this.progressBar = findViewById(R.id.progress_circular);

    }

    @Override
    public void goToAddWordScreen() {
        Intent intent = new Intent(getApplicationContext(), AddEntryActivity.class);
        startActivity(intent);
    }

    @Override
    public void goToChooseFile() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, CHOOSE_FILE_CODE);
    }

    @Override
    public void goToChooseDirectory() {
        if (!Utils.hasPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Utils.requestPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_CODE);
            return;
        }

        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, CHOOSE_DIRECTORY_CODE);
    }

    @Override
    public void refreshList() {
        this.rcclrViewAdapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void showProgress() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgress() {
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showMessage(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == CHOOSE_FILE_CODE) {
                this.presenter.onDumpFilePicked(this, data.getData());
            } else if (requestCode == CHOOSE_DIRECTORY_CODE) {
                this.presenter.onDumpDirectoryPicked(data.getData());
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToChooseDirectory();
            }
        }
    }

    @Override
    public void executeOnUiThread(@NonNull final Runnable task) {
        runOnUiThread(task);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.item_dump) {
            this.presenter.onDumpToFileClicked();
        } else if (item.getItemId() == R.id.item_restore) {
            this.presenter.onRestoreFromDumpClicked();
        }
        return true;
    }
}
