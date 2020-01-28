package com.halo.dictionary.rcclrview;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.halo.dictionary.R;
import com.halo.dictionary.sql.WordContract;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Адаптер для отображения переданных слов
 *
 * Created by halo on 08.09.2017.
 */

public class WordsRcclrViewAdapter
        extends RecyclerView.Adapter<WordsRcclrViewAdapter.WordViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    private final Set<Long> hiddenTrnslIds = new HashSet<>();

    private static WordsRcclrViewAdapter INSTANCE;

    /**
     * Создание адаптера для отображения переданных слов
     *
     * @param cursor    - курсов базы данных для отображения данных
     * @param context   - вызывающий контекст
     */
    public WordsRcclrViewAdapter(@NonNull Cursor cursor, @NonNull Context context) {
        this.mCursor = cursor;
        this.mContext = context;
        INSTANCE = this;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View wordView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_rcclr_view_words, parent, false);
        return new WordViewHolder(wordView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        // Перематываем список к элементу, который должен быть отображён
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        // Обновление ViewHolder для отображения элемента
        final String word = mCursor.getString(mCursor.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_WORD));
        final String translation = mCursor.getString(mCursor.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_TRANSLATION));
        Long id = mCursor.getLong(mCursor.getColumnIndex(WordContract.WordEntry._ID));
        holder.mTvWord.setText(word);
        holder.mTvTranslation.setText((translation == null) ? "" : translation);
        holder.mTvId.setText(id.toString());

        holder.mTvTranslation.setVisibility(WordViewHolder.hiddenIds.contains(id.toString())? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * Swaps the Cursor currently held in the adapter with a new one
     * and triggers a UI refresh
     *
     * @param newCursor the new cursor that will replace the existing one
     */
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    /**
     * Возвращает экземпляр {@link WordsRcclrViewAdapter}
     *
     * @return экземпляр {@link WordsRcclrViewAdapter} или {@code null}, если он не создан
     */
    public static WordsRcclrViewAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * View for each separate data item
     */
    public static class WordViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvId;
        public TextView mTvWord;
        public TextView mTvTranslation;

        public static final Set<String> hiddenIds = new HashSet<>();

        public WordViewHolder(View view) {
            super(view);
            this.mTvId = view.findViewById(R.id.tv_c_rcclr_view_words_id);
            this.mTvWord = view.findViewById(R.id.tv_c_rcclr_view_words_word);
            this.mTvTranslation = view.findViewById(R.id.tv_c_rcclr_view_words_translation);

            view.setOnClickListener(tvView -> {
                final String id = mTvId.getText().toString();
                if (!id.isEmpty()) {
                    if (hiddenIds.contains(id)) {
                        hiddenIds.remove(id);
                        mTvTranslation.setVisibility(View.VISIBLE);
                    } else {
                        hiddenIds.add(id);
                        mTvTranslation.setVisibility(View.INVISIBLE);
                    }
                }
            }
            );
        }
    }

}
