package BaseWidget;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public interface IFileState {
    void initState();
    int getCurrentState();
    void setFinishedStateListner(OnFinishedStateLisner onFinishedStateLisner);
    void setProgress(int progress);
    void setDrawable(Drawable drawable);
}
