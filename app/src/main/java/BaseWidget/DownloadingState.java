package BaseWidget;

import android.graphics.drawable.Drawable;

import com.okbtsp.messenger.AppUtils.StaticValues;

public class DownloadingState implements IFileState {


    private int progress = 0;

    private OnFinishedStateLisner onFinishedStateLisner;
    @Override
    public void initState() {
        onFinishedStateLisner.onDownloadingStateInited(progress);
    }

    @Override
    public int getCurrentState() {
        return StaticValues.DOWNLOADING;
    }

    @Override
    public void setFinishedStateListner(OnFinishedStateLisner onFinishedStateLisner) {
        this.onFinishedStateLisner = onFinishedStateLisner;
    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public void setDrawable(Drawable drawable) {

    }


}
