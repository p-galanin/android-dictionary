package com.halo.dictionary.mvp.ui.rcclrview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halo.dictionary.R;

/**
 * Created by halo on 26.01.2018.
 */

public class WordsRelativeLayout extends RelativeLayout {

    public WordsRelativeLayout(final Context context) {
        super(context);
    }

    public WordsRelativeLayout(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
    }

//    private void init(final Context context) {
//        //this.mDetector = new GestureDetectorCompat(context, new WordsGestureListener());
//
//        this.setOnClickListener(view -> {
//            final TextView mTvId = (TextView) view.findViewById(R.id.tv_c_rcclr_view_words_id);
//            final TextView mTvWord = (TextView) view.findViewById(R.id.tv_c_rcclr_view_words_word);
//            final TextView mTvTranslation = (TextView) view.findViewById(R.id.tv_c_rcclr_view_words_translation);
//        });
//    }

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
