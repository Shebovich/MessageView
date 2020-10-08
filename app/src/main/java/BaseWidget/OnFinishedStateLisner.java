package BaseWidget;

import android.graphics.drawable.Drawable;

public interface OnFinishedStateLisner {
    void onDownloadingStateInited(int progress);
    void onDownloadStateInited(Drawable drawable);
    void onNotDownloadedStateInited(Drawable drawable);
}
