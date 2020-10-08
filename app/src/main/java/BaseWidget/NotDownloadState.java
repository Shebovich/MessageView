package BaseWidget;

import android.graphics.drawable.Drawable;

import static BaseWidget.MessageView.NOT_DOWNLOADED;

public class NotDownloadState implements IFileState {
    private OnFinishedStateLisner onFinishedStateLisner;
    private Drawable drawable;
    @Override
    public void initState() {
        onFinishedStateLisner.onNotDownloadedStateInited(drawable);
    }


    @Override
    public int getCurrentState() {
        return NOT_DOWNLOADED;
    }

    @Override
    public void setFinishedStateListner(OnFinishedStateLisner onFinishedStateLisner) {
        this.onFinishedStateLisner = onFinishedStateLisner;
    }

    @Override
    public void setProgress(int progress) {

    }

    @Override
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
