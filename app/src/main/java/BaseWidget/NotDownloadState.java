package BaseWidget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.okbtsp.messenger.AppUtils.SessionManager;
import com.okbtsp.messenger.AppUtils.StaticValues;
import com.okbtsp.messenger.AppUtils.deviceId;

public class NotDownloadState implements IFileState {
    private OnFinishedStateLisner onFinishedStateLisner;
    private Drawable drawable;
    @Override
    public void initState() {
        onFinishedStateLisner.onNotDownloadedStateInited(drawable);
    }


    @Override
    public int getCurrentState() {
        return StaticValues.NOT_DOWNLOADED;
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
