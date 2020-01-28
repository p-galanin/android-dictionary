package com.halo.dictionary;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class DumpFileWordsService extends IntentService {

    public DumpFileWordsService() {
        super("DumpFileWordsService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra("RECEIVER");

        if ("DUMP_WORDS".equals(intent.getAction())) {

            final Bundle bundle = new Bundle();
            int code;

            try {
                Utils.dumpWordsToExternalStorage(this);
                bundle.putSerializable("RESULT", true);
                code = 0;
            } catch (Exception e) {
                bundle.putSerializable("RESULT", false);
                code = -1;
            }

            receiver.send(code, bundle);
        }
    }
}
