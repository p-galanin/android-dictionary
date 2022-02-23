package com.halo.dictionary.mvp.ui.rcclrview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.halo.dictionary.R;
import com.halo.dictionary.mvp.WordsListPresenter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Адаптер для отображения переданных слов.
 *
 * Created by halo on 08.09.2017.
 */

public class WordsRcclrViewAdapter
        extends RecyclerView.Adapter<WordsRcclrViewAdapter.EntryViewHolder> {

    private WordsListPresenter presenter;

    /**
     * Создание адаптера для отображения переданных слов.
     *
     * @param presenter презентер для получения некоторой информации по отображению
     */
    public WordsRcclrViewAdapter(@NonNull WordsListPresenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View wordView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_rcclr_view_words, parent, false);
        return new EntryViewHolder(wordView, this.presenter);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        this.presenter.getEntryForPosition(position).ifPresent(wordEntry ->
        {
            holder.tvWord.setText(wordEntry.getWord());
            holder.tvTranslation.setText(wordEntry.getTranslation());
            holder.tvId.setText(String.valueOf(wordEntry.getId()));
            holder.tvTranslation.setVisibility(this.presenter.isTranslationVisible(wordEntry.getId()) ? View.VISIBLE : View.INVISIBLE);
            holder.isArchivedView.setChecked(wordEntry.isArchived());
        });
    }

    @Override
    public int getItemCount() {
        return this.presenter.getEntriesAmount();
    }

    /**
     * View for each separate data item.
     */
    static class EntryViewHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvWord;
        TextView tvTranslation;
        CheckBox isArchivedView;
        WordsListPresenter presenter;

        EntryViewHolder(View view, WordsListPresenter presenter) {
            super(view);
            this.tvId = view.findViewById(R.id.tv_c_rcclr_view_words_id);
            this.tvWord = view.findViewById(R.id.tv_c_rcclr_view_words_word);
            this.tvTranslation = view.findViewById(R.id.tv_c_rcclr_view_words_translation);
            this.isArchivedView = view.findViewById(R.id.is_archived);
            this.presenter = presenter;

            view.setOnClickListener(tvView -> {
                final Long id = Long.parseLong(this.tvId.getText().toString());
                this.presenter.onClickEntry(id);
                this.tvTranslation.setVisibility(this.presenter.isTranslationVisible(id) ? View.VISIBLE : View.INVISIBLE);
            });

            view.setOnLongClickListener(tvView -> {
                this.presenter.onWordLongPressed(Long.parseLong(this.tvId.getText().toString()));
                return true;
            });

            this.isArchivedView.setOnClickListener(checkBoxView -> {
                final Long id = Long.parseLong(this.tvId.getText().toString());
                this.presenter.onArchiveClicked(id);
            });
        }
    }

}
