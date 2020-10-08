package BaseWidget;

import android.graphics.drawable.Drawable;

import com.okbtsp.messenger.AppUtils.StaticValues;

public class DownloadState implements IFileState {
    private OnFinishedStateLisner onFinishedStateLisner;
    private Drawable drawable;




    @Override
    public void initState() {

        onFinishedStateLisner.onDownloadStateInited(drawable);


    }

    @Override
    public int getCurrentState() {
        return StaticValues.FILE_DOWNLOADED;
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
