package com.halo.dictionary.rcclrview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halo.dictionary.R;

/**
 * Created by halo on 26.01.2018.
 */

public class WordsRelativeLayout extends RelativeLayout {

    private GestureDetectorCompat mDetector;
    private LayoutInflater mInflater;

    /*public TextView mTvId;
    public TextView mTvWord;
    public TextView mTvTranslation;*/

    public WordsRelativeLayout(final Context context) {
        super(context);
        this.init(context);
    }

    public WordsRelativeLayout(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
        this.init(context);
    }

    private void init(final Context context) {
        //this.mDetector = new GestureDetectorCompat(context, new WordsGestureListener());

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                final TextView mTvId = (TextView) view.findViewById(R.id.tv_c_rcclr_view_words_id);
                final TextView mTvWord = (TextView) view.findViewById(R.id.tv_c_rcclr_view_words_word);
                final TextView mTvTranslation = (TextView) view.findViewById(R.id.tv_c_rcclr_view_words_translation);

                mTvTranslation.setVisibility(
                        mTvTranslation.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);

                Log.d("", mTvId == null ? "" : mTvId.toString());
            }

        });
    }

    /*private class WordsGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("tag", "on single tap");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }*/


}
