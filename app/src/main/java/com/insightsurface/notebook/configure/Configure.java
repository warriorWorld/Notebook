package com.insightsurface.notebook.configure;

import android.os.Environment;

public class Configure {
    final public static String DOWNLOAD_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/" + "aNotebook";
}
