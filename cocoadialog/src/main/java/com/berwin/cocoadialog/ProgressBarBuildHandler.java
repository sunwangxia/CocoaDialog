package com.berwin.cocoadialog;

import android.content.Context;
import android.widget.ProgressBar;

public interface ProgressBarBuildHandler<T extends ProgressBar> {
    T build(Context context);
}
