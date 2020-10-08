package BaseWidget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.app.adprogressbarlib.AdCircleProgress;
import com.shebovich.messageview.R;


import java.util.HashMap;
import java.util.Map;


public class MessageView extends ConstraintLayout implements OnFinishedStateLisner {

    private int standartPadding;
    public static final int ERROR_STATUS = 0;
    public static final int NOT_SENDED = 1;
    public static final int IS_VIEWED = 2;
    public static final int IS_NOT_VIEWED = 3;

    private int wrapContent = LayoutParams.WRAP_CONTENT;
    private int matchParent = LayoutParams.MATCH_PARENT;

    @Dimension(unit = Dimension.SP)
    private float primaryTextSize;
    @Dimension(unit = Dimension.SP)
    private int radius;
    private int shadowSize = 6;
    private boolean showArrow;
    private int color;
    private ConstraintSet set;
    private Paint paint;
    private ConstraintLayout constraintLayout;
    private boolean isOwnMessage;
    private TextView messageText;
    private TextView textForwardDisplayName;
    private Map<Integer, Integer> messageStatus = new HashMap<Integer, Integer>();
    private TextView timeText;
    private TextView messageDisplayName;
    private ImageView replyImage;
    private TextView messageReply;
    private TextView nameReply;
    private TextView displayNameText;
    private TextView playTime;

    private SeekBar seekBar;
    private int secondaryColor;
    private ImageView linkImage;
    private int textColor;
    private Map<Integer,Integer> audioStatusMap = new HashMap<>();
    private TextView linkSiteName, linkTitle, linkDescription;
    private ImageView isEdited, isViewed;
    private TextView nameFile, sizeFile;
    private LinearLayout fileBaseLayout;
    private ImageView imagePost;
    private AdCircleProgress adCircleProgress;
    private Map<Integer,IFileState> fileStateMap;

    public static final int AUDIO_PLAY = 0;
    public static final int AUDIO_PAUSE = 1;
    public static final int AUDION_DOWNLOAD = 2;

    public static final int NOT_DOWNLOADED = 3;
    public static final int DOWNLOADING = 0;
    public static final int ERROR_DOWNLOADING = 1;
    public static final int FILE_DOWNLOADED = 2;


    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MessageView, 0, 0);
        try {
            //get the text and colors specified using the names in attrs.xml
            secondaryColor = a.getColor(R.styleable.MessageView_setSecondaryColor, Color.WHITE);
            textColor = a.getColor(R.styleable.MessageView_setTextColor, Color.WHITE);
            color = a.getColor(R.styleable.MessageView_setColor, Color.RED);
            radius = (int) (getResources().getDisplayMetrics().density * a.getInteger(R.styleable.MessageView_radius, 0));
            showArrow = a.getBoolean(R.styleable.MessageView_isShowArrow, true);
            isOwnMessage = a.getBoolean(R.styleable.MessageView_isOwnMessage, true);
        } finally {
            a.recycle();
        }
        initBaseFields(context);

    }

    public void setVisibilityViewed(int type) {
        if (isViewed != null) {
            isViewed.setVisibility(type);
        }
    }

    private void initBaseFields(@NonNull Context context) {
        messageStatus.put(ERROR_STATUS, R.drawable.ic_baseline_error_24);
        messageStatus.put(NOT_SENDED, R.drawable.ic_timer_black_24dp);
        messageStatus.put(IS_VIEWED, R.drawable.ic_baseline_done_all_24px);
        messageStatus.put(IS_NOT_VIEWED, R.drawable.ic_baseline_done_24px);

        audioStatusMap.put(AUDIO_PLAY,R.drawable.ic_play_circle_filled_black_24dp);
        audioStatusMap.put(AUDIO_PAUSE,R.drawable.ic_pause_circle_filled_black_24dp);
        audioStatusMap.put(AUDION_DOWNLOAD, R.drawable.ic_down);

        standartPadding = dpToPx(8);
        messageText = new TextView(getContext());
        messageText.setTextColor(textColor);
        messageText.setId(generateViewId());
        primaryTextSize = 16;

        constraintLayout = new ConstraintLayout(getContext());
        constraintLayout.setId(generateViewId());
        constraintLayout.setLayoutParams(new LayoutParams(wrapContent, wrapContent));
        addView(constraintLayout);
        set = new ConstraintSet();
        set.clone(constraintLayout);


        if (isOwnMessage) {
            initViewedMessage();
        } else {
            initMessageDisplayName("");
        }
        initMessageText();
        initTimeText();


    }


    private ImageView initReplyImage() {
        replyImage = new ImageView(getContext());
        LinearLayout.LayoutParams replyImageParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        replyImageParams.gravity = Gravity.CENTER;
        replyImageParams.setMarginStart(dpToPx(4));
        replyImage.setLayoutParams(replyImageParams);
        replyImage.setVisibility(GONE);
        replyImage.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        return replyImage;

    }


    private void initFileView(String fileName, String fileSize) {
        fileStateMap = new HashMap<>();
        fileStateMap.put(DOWNLOADING,new DownloadingState());
        fileStateMap.put(FILE_DOWNLOADED, new DownloadState());
        fileStateMap.put(NOT_DOWNLOADED, new NotDownloadState());

        fileBaseLayout = initBaseLinearLayout(LinearLayout.HORIZONTAL);
        fileBaseLayout.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));

        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setId(generateViewId());
        LayoutParams frameParams = new LayoutParams(wrapContent, wrapContent);
        frameLayout.setLayoutParams(frameParams);

        imagePost = new ImageView(getContext());
        imagePost.setId(generateViewId());
        LayoutParams imageParams = new LayoutParams(dpToPx(80), dpToPx(80));
        imagePost.setLayoutParams(imageParams);

        imagePost.setAdjustViewBounds(true);
        int imagePadding = dpToPx(2);
        imagePost.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
        imagePost.setScaleType(ImageView.ScaleType.FIT_XY);

//        ImageView downloadImage = new ImageView(getContext());
//        downloadImage.setId(generateViewId());
//        FrameLayout.LayoutParams downloadImageParams = new FrameLayout.LayoutParams(dpToPx(40),dpToPx(40));
//        downloadImageParams.gravity = Gravity.CENTER;
//
//        downloadImage.setImageResource(R.drawable.ic_file_download_black_24dp);

        adCircleProgress = new AdCircleProgress(getContext());
        adCircleProgress.setId(generateViewId());
        adCircleProgress.setAdProgress(0);

        adCircleProgress.setFinishedStrokeColor(secondaryColor);
        adCircleProgress.setUnfinishedStrokeWidth(Color.WHITE);
        adCircleProgress.setFinishedStrokeWidth(dpToPx(5));

        adCircleProgress.setAttributeResourceId(R.drawable.ic_file_download_black_24dp);
        adCircleProgress.setShowText(false);
        adCircleProgress.setUnfinishedStrokeWidth(dpToPx(5));
        FrameLayout.LayoutParams adCircleLayoutParams = new FrameLayout.LayoutParams(dpToPx(60), dpToPx(60));
        adCircleLayoutParams.gravity = Gravity.CENTER;
        adCircleProgress.setLayoutParams(adCircleLayoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adCircleProgress.setBackgroundTintMode(PorterDuff.Mode.ADD);
        }
        LinearLayout fileCreditialsLayout = initBaseLinearLayout(LinearLayout.VERTICAL);

        nameFile = initBaseTextView(0, 0, 0, 0);
        nameFile.setPadding(imagePadding, imagePadding, dpToPx(24), imagePadding);
        nameFile.setSingleLine(true);
        nameFile.setText(fileName);
        nameFile.setMaxWidth(dpToPx(180));
        nameFile.setTextColor(secondaryColor);
        nameFile.setTextSize(14);

        sizeFile = initBaseTextView(0, 0, 0, 0);
        sizeFile.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
        sizeFile.setText(fileSize);
        sizeFile.setTextSize(12);
        sizeFile.setTextColor(textColor);

        fileCreditialsLayout.addView(nameFile);
        fileCreditialsLayout.addView(sizeFile);

        frameLayout.addView(imagePost);
        // frameLayout.addView(downloadImage);
        frameLayout.addView(adCircleProgress);
        fileBaseLayout.addView(frameLayout);
        fileBaseLayout.addView(fileCreditialsLayout);
        constraintLayout.addView(fileBaseLayout);
        set.clone(constraintLayout);
        set.connect(fileBaseLayout.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(messageText.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(messageText.getId(), ConstraintSet.TOP, fileBaseLayout.getId(), ConstraintSet.BOTTOM);
        if (isOwnMessage) {
            set.connect(fileBaseLayout.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);

            set.clear(messageText.getId(), ConstraintSet.END);
        } else {
            set.connect(fileBaseLayout.getId(), ConstraintSet.TOP, messageDisplayName.getId(), ConstraintSet.BOTTOM);
        }

        set.applyTo(constraintLayout);
    }

    public void setFileNameAndSize(String nameFile, String sizeFile) {
        if (this.nameFile != null) {
            this.nameFile.setText(nameFile);
            this.sizeFile.setText(sizeFile);
        } else {
            initFileView(nameFile, sizeFile);
        }
    }

    public void setFileState(int fileState, Drawable drawable){
        if (fileStateMap.containsKey(fileState)){
            fileStateMap.get(fileState).setFinishedStateListner(this);
            fileStateMap.get(fileState).setDrawable(drawable);
            fileStateMap.get(fileState).initState();
        }
    }


    public void setMessageText(String text) {
        if (text != null && text.length() > 0) {
            messageText.setText(Html.fromHtml((text.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br />")) + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
        } else {
            messageText.setText("");
        }

    }

    public void setTimeText(String timeText) {
        this.timeText.setText(timeText);
    }

    public void setMessageStatus(int status) {
        if (messageStatus.containsKey(status)) {
            isViewed.setImageResource(messageStatus.get(status));
        } else {
            isViewed.setImageResource(R.drawable.ic_baseline_done_24px);
        }
    }

    public void setMessageDisplayName(String displayName) {
        if (messageDisplayName != null) {
            messageDisplayName.setText(displayName);
        } else {
            initMessageDisplayName(displayName);
        }
    }

    public void setEditedMessage(int status) {
        if (isEdited != null) {
            isEdited.setVisibility(status);
        } else {
            initEditMessage(status);
        }

    }

    public void setForwardView(String forwardUser) {
        if (textForwardDisplayName != null) {
            textForwardDisplayName.setText(forwardUser);
        } else {
            initForwardFields(forwardUser);
        }
    }

    public void setReplyMessage(String replyMessage, String replyName) {
        if (this.messageReply != null && this.nameReply != null) {
            messageReply.setText(replyMessage);
            nameReply.setText(replyName);
        } else {
            initReplyLayout(replyMessage, replyName);
        }
    }

    public void setReplyMessageWithPhoto(String replyName, String replyMessage, Drawable drawable) {
        initReplyLayout(replyName, replyMessage);
        replyImage.setVisibility(VISIBLE);
        replyImage.setImageDrawable(drawable);
    }
    public void setMessageTextVisibility(int type){
        if (messageText!=null){
            messageText.setVisibility(type);
        }
    }


    private void initTimeText() {
        timeText = initBaseTextView(0, 0, dpToPx(3), dpToPx(2));
        timeText.setMaxLines(1);
        timeText.setText("14:42");
        timeText.setTextColor(textColor);
        timeText.setTextSize(12);
        constraintLayout.addView(timeText);
        set.clone(constraintLayout);
        set.connect(timeText.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        if (isOwnMessage) {
            set.connect(timeText.getId(), ConstraintSet.END, isViewed.getId(), ConstraintSet.START);

        } else {
            set.connect(timeText.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        }
        set.applyTo(constraintLayout);

    }

    private void initMessageText() {


        messageText.setMaxWidth(dpToPx(280));
        messageText.setLinksClickable(true);
        messageText.setText("333333333333333333333333333333333333333333333333333333333333");
        messageText.setLayoutParams(new LayoutParams(wrapContent, wrapContent));
        messageText.setAutoLinkMask(Linkify.ALL);
        messageText.setPadding(standartPadding, standartPadding, 0, standartPadding);
        messageText.setTextSize(primaryTextSize);
        messageText.setTextColor(textColor);
        messageText.setGravity(Gravity.START);
        constraintLayout.addView(messageText);
        set.clone(constraintLayout);
        set.connect(messageText.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        if (isOwnMessage) {

            set.connect(messageText.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        } else {
            set.connect(messageText.getId(), ConstraintSet.TOP, messageDisplayName.getId(), ConstraintSet.BOTTOM);
        }
        set.applyTo(constraintLayout);
    }

    private void initViewedMessage() {
        isViewed = new ImageView(getContext());
        isViewed.setId(generateViewId());
        LayoutParams isViewedParams = new LayoutParams(dpToPx(15), dpToPx(15));
        isViewedParams.setMargins(0, 0, dpToPx(3), dpToPx(2));
        isViewed.setLayoutParams(isViewedParams);
        isViewed.setImageResource(R.drawable.ic_baseline_done_24px);
        constraintLayout.addView(isViewed);
        set.clone(constraintLayout);
        set.connect(isViewed.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(isViewed.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.applyTo(constraintLayout);

    }

    private void initEditMessage(int visibility) {
        isEdited = new ImageView(getContext());
        isEdited.setId(generateViewId());
        LayoutParams isEditedParams = new LayoutParams(dpToPx(15), dpToPx(15));
        isEditedParams.setMargins(0, 0, dpToPx(8), dpToPx(3));
        isEdited.setLayoutParams(isEditedParams);
        isEdited.setImageResource(R.drawable.edit);
        isEdited.setColorFilter(secondaryColor);
        isEdited.setVisibility(visibility);
        constraintLayout.addView(isEdited);
        set.clone(constraintLayout);
        set.connect(isEdited.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(isEdited.getId(), ConstraintSet.END, timeText.getId(), ConstraintSet.START);
        set.applyTo(constraintLayout);

    }

    private void initMessageDisplayName(String displayName) {
        messageDisplayName = initBaseTextView(0, 0, 0, 0);
        messageDisplayName.setPadding(standartPadding, standartPadding, standartPadding, standartPadding);
        messageDisplayName.setText(displayName);
        messageDisplayName.setTextColor(secondaryColor);
        messageDisplayName.setTextSize(14);
        constraintLayout.addView(messageDisplayName);
        set.clone(constraintLayout);
        set.connect(messageDisplayName.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        set.connect(messageDisplayName.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.applyTo(constraintLayout);
    }

    private LinearLayout initBaseLinearLayout(int orientation) {
        LinearLayout replyLayout = new LinearLayout(getContext());
        replyLayout.setId(generateViewId());
        LinearLayout.LayoutParams replyLayoutParams = new LinearLayout.LayoutParams(wrapContent, wrapContent);
        replyLayoutParams.setMarginStart(dpToPx(10));
        replyLayout.setLayoutParams(replyLayoutParams);
        replyLayout.setPadding(0, standartPadding, 0, 0);
        replyLayout.setOrientation(orientation);
        return replyLayout;
    }

    private ImageView initLineImageView() {
        ImageView lineReplyImage = new ImageView(getContext());
        lineReplyImage.setId(generateViewId());
        ViewGroup.LayoutParams lineReplyImageParams = new ViewGroup.LayoutParams(dpToPx(2), ViewGroup.LayoutParams.MATCH_PARENT);
        lineReplyImage.setLayoutParams(lineReplyImageParams);
        lineReplyImage.setBackgroundColor(secondaryColor);
        return lineReplyImage;
    }

    private void initReplyLayout(String replyMessage, String replyName) {
        LinearLayout replyLayout = initBaseLinearLayout(LinearLayout.HORIZONTAL);
        LinearLayout messageReplyLayout = new LinearLayout(getContext());
        messageReplyLayout.setId(generateViewId());
        LayoutParams messageReplyLayoutParams = new LayoutParams(wrapContent, wrapContent);
        messageReplyLayout.setOrientation(LinearLayout.VERTICAL);
        messageReplyLayout.setLayoutParams(messageReplyLayoutParams);


        nameReply = initBaseTextView(dpToPx(5), 0, 0, 0);
        nameReply.setTextColor(secondaryColor);
        nameReply.setTextSize(12);
        nameReply.setText(replyName);
        nameReply.setPadding(0, 0, 5, 0);

        messageReply = initBaseTextView(dpToPx(5), 0, 0, 0);
        messageReply.setEllipsize(TextUtils.TruncateAt.END);
        messageReply.setMaxWidth(dpToPx(200));
        messageReply.setTextColor(textColor);
        messageReply.setTextSize(12);
        messageReply.setText(replyMessage);
        messageReply.setSingleLine(true);


        messageReplyLayout.addView(nameReply);
        messageReplyLayout.addView(messageReply);
        replyLayout.addView(initLineImageView());
        replyLayout.addView(initReplyImage());
        replyLayout.addView(messageReplyLayout);
        constraintLayout.addView(replyLayout);
        set.clone(constraintLayout);
        set.connect(replyLayout.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(messageText.getId(), ConstraintSet.TOP, replyLayout.getId(), ConstraintSet.BOTTOM);
        if (isOwnMessage) {
            set.connect(replyLayout.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
            set.connect(messageText.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
            set.clear(messageText.getId(), ConstraintSet.END);
        } else {
            set.connect(replyLayout.getId(), ConstraintSet.TOP, messageDisplayName.getId(), ConstraintSet.BOTTOM);
        }
        set.applyTo(constraintLayout);


    }

    private TextView initBaseTextView(int left, int top, int right, int bottom) {
        TextView textView = new TextView(getContext());
        textView.setId(generateViewId());
        LayoutParams textViewParams = new LayoutParams(wrapContent, wrapContent);
        textViewParams.setMargins(left, top, right, bottom);
        textView.setLayoutParams(textViewParams);
        return textView;
    }



    private void initLinkView(String siteName, String title, String description) {
        LinearLayout baseLinearLayout = initBaseLinearLayout(LinearLayout.HORIZONTAL);

        LinearLayout linkLinearLayout = initBaseLinearLayout(LinearLayout.VERTICAL);
        linkLinearLayout.setMinimumWidth(dpToPx(200));
        linkSiteName = initBaseTextView(dpToPx(5), 0, 0, 0);
        linkSiteName.setSingleLine(true);
        linkSiteName.setMaxWidth(dpToPx(200));
        linkSiteName.setTextSize(14);
        linkSiteName.setText(siteName);
        linkSiteName.setTextColor(secondaryColor);


        linkTitle = initBaseTextView(dpToPx(5), 0, 0, 0);
        linkTitle.setMaxWidth(dpToPx(200));
        linkTitle.setText(title);
        linkTitle.setMinWidth(dpToPx(100));
        linkTitle.setPadding(0, 0, standartPadding, 0);
        linkTitle.setTextColor(textColor);

        linkImage = new ImageView(getContext());
        linkImage.setId(generateViewId());
        linkImage.setScaleType(ImageView.ScaleType.FIT_START);
        linkImage.setPadding(standartPadding, standartPadding, standartPadding, standartPadding);
        LayoutParams layoutParams = new LayoutParams(matchParent, dpToPx(100));
        linkImage.setLayoutParams(layoutParams);

        linkDescription = initBaseTextView(dpToPx(5), 0, 0, 0);
        linkDescription.setMaxWidth(dpToPx(200));
        linkDescription.setPadding(0, 0, dpToPx(8), 0);
        linkDescription.setText(description);
        linkDescription.setTextColor(textColor);
        linkDescription.setTextSize(12);

        linkLinearLayout.addView(linkSiteName);
        linkLinearLayout.addView(linkTitle);
        linkLinearLayout.addView(linkImage);
        linkLinearLayout.addView(linkDescription);

        baseLinearLayout.addView(initLineImageView());
        baseLinearLayout.addView(linkLinearLayout);

        constraintLayout.addView(baseLinearLayout);
        set.clone(constraintLayout);
        set.connect(baseLinearLayout.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(messageText.getId(), ConstraintSet.TOP, baseLinearLayout.getId(), ConstraintSet.BOTTOM);
        if (isOwnMessage) {
            set.connect(baseLinearLayout.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
            set.connect(messageText.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
            set.clear(messageText.getId(), ConstraintSet.END);
        } else {
            set.connect(baseLinearLayout.getId(), ConstraintSet.TOP, messageDisplayName.getId(), ConstraintSet.BOTTOM);
        }

        set.applyTo(constraintLayout);


    }

    private void initForwardFields(String forwardUser) {
        TextView textForward = initBaseTextView(0, 0, 0, 0);
        textForward.setText("Пересланное сообщение");
        textForward.setTextColor(secondaryColor);
        textForward.setTextSize(14);
        textForward.setPadding(standartPadding, standartPadding, standartPadding, standartPadding);
        constraintLayout.addView(textForward);
        set.clone(constraintLayout);
        set.connect(textForward.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        if (isOwnMessage) {
            set.connect(textForward.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
            set.connect(messageText.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
            set.clear(messageText.getId(), ConstraintSet.END);
        } else {
            set.connect(textForward.getId(), ConstraintSet.TOP, messageDisplayName.getId(), ConstraintSet.BOTTOM);
        }

        set.applyTo(constraintLayout);

        textForwardDisplayName = initBaseTextView(0, 0, 0, 0);
        textForwardDisplayName.setPadding(standartPadding, standartPadding, standartPadding, standartPadding);
        textForwardDisplayName.setEllipsize(TextUtils.TruncateAt.END);
        textForwardDisplayName.setTextColor(textColor);
        textForwardDisplayName.setText(forwardUser);
        textForwardDisplayName.setTextSize(12);
        constraintLayout.addView(textForwardDisplayName);
        set.clone(constraintLayout);
        set.connect(textForwardDisplayName.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textForwardDisplayName.getId(), ConstraintSet.TOP, textForward.getId(), ConstraintSet.BOTTOM);
        set.connect(messageText.getId(), ConstraintSet.TOP, textForwardDisplayName.getId(), ConstraintSet.BOTTOM);
        set.applyTo(constraintLayout);


    }
    private void initRecordView(){
        messageText.setVisibility(GONE);
        RelativeLayout baseRelative = new RelativeLayout(getContext());

        baseRelative.setId(generateViewId());
        LayoutParams baseParams = new LayoutParams(dpToPx(280),wrapContent);
        baseParams.setMargins(0,dpToPx(10),0,0);
        baseRelative.setLayoutParams(baseParams);
        int padding4dp = dpToPx(4);
        baseRelative.setPadding(padding4dp,padding4dp,padding4dp,padding4dp);

        imagePost = new ImageView(getContext());
        imagePost.setId(generateViewId());
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(dpToPx(40),dpToPx(40));
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        imagePost.setLayoutParams(imageParams);
        imagePost.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);

        seekBar = new SeekBar(getContext());
        seekBar.setId(generateViewId());
        seekBar.getProgressDrawable().setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_ATOP);
        seekBar.getThumb().setColorFilter(secondaryColor, PorterDuff.Mode.SRC_ATOP);


        RelativeLayout.LayoutParams seekParams = new RelativeLayout.LayoutParams(matchParent,wrapContent);
        seekParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        seekParams.addRule(RelativeLayout.RIGHT_OF,imagePost.getId());
        seekBar.setLayoutParams(seekParams);
        playTime = initBaseTextView(dpToPx(35),0,0,0);
        playTime.setPadding(padding4dp,padding4dp,padding4dp,padding4dp);
        playTime.setText("00:00");
        playTime.setTextColor(textColor);
        baseRelative.addView(imagePost);
        baseRelative.addView(seekBar);
        constraintLayout.addView(playTime);
        constraintLayout.addView(baseRelative);
        set.clone(constraintLayout);
        set.connect(baseRelative.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(playTime.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(playTime.getId(), ConstraintSet.TOP, baseRelative.getId(), ConstraintSet.BOTTOM);
        set.connect(messageText.getId(), ConstraintSet.TOP, playTime.getId(), ConstraintSet.BOTTOM);
        if (isOwnMessage) {
            set.connect(baseRelative.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
            set.connect(messageText.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        } else {
            set.connect(baseRelative.getId(), ConstraintSet.TOP, messageDisplayName.getId(), ConstraintSet.BOTTOM);
        }
        set.applyTo(constraintLayout);



    }

    public void setPlayTime(String text){
        if (playTime!=null){
            playTime.setText(text);
        }
    }
    public void setSeekBarProgress(int progress){
        if (seekBar!=null){
            seekBar.setProgress(progress);
        }
    }

    public void setImagePlayResource(int type){
        if (audioStatusMap.containsKey(type)){
            imagePost.setImageResource(audioStatusMap.get(type));
        }else {
            imagePost.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        }

    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int childWith = 0;
        int childHeight = 0;

        int childState = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            childWith += child.getMeasuredWidth() + 2 * radius;
            childHeight += child.getMeasuredHeight();
            childState = combineMeasuredStates(childState, child.getMeasuredState());

        }
        setMeasuredDimension(resolveSizeAndState(childWith + shadowSize, widthMeasureSpec + 2 * radius, childState),
                resolveSizeAndState(childHeight + shadowSize * 2, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
        // Report our final dimensions.


    }


    @Override
    public void dispatchDraw(Canvas canvas) {


        int rX = radius;
        int rY = 0;
        int rW = getWidth() - radius;
        int rH = getHeight();
        RectF rectF = new RectF(0, 0, radius, radius);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);


        paint.setAntiAlias(true);

        Path path = new Path();

        path.moveTo(rX, rH - radius);

        path.lineTo(rX, rY + radius);

        path.arcTo(translate(rX, rY, rectF), 180, 90);
        path.lineTo(rW - radius, rY);
//
        path.arcTo(translate(rW - radius, rY, rectF), -90, 90);
        //right
        path.lineTo(rW, rH - radius);
//        //bottom right
//
        path.arcTo(translate(rW - radius, rH - radius, rectF), 0, 90);
//        //bottom
        if (showArrow) {
            path.lineTo(0, rH);


            path.arcTo(translate(0, rH - radius, rectF), 90, -90);
        } else {
            path.lineTo(rX + radius, rH);

            path.arcTo(translate(rX, rH - radius, rectF), -270, 90);

        }
        setOwnMessage(isOwnMessage);
        path.close();

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(color);
        canvas.drawPath(path, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setElevation(10);
        }
        super.dispatchDraw(canvas);
    }

    public void setOwnMessage(boolean isOwnMessage) {
        if (isOwnMessage) {
            this.setScaleX(-1);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.setScaleX(-1f);
            }
        }
    }

    private RectF translate(int dX, int dY, RectF rectF) {
        RectF rectF1 = new RectF(rectF);
        rectF1.offsetTo(dX, dY);
        return rectF1;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (isOwnMessage) {
                child.layout(radius, 0, child.getMeasuredWidth() + radius, child.getMeasuredHeight());
            } else
                child.layout(radius, 0, child.getMeasuredWidth() + 2 * radius, child.getMeasuredHeight());


        }
    }

    public static int convertPixelsToDp(float px, Context context) {
        return (int) (px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Px
    private int dpToPx(@Dimension(unit = Dimension.DP) int dp) {
        final Resources resources = getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    @Override
    public void onDownloadingStateInited(int progress) {

        imagePost.setVisibility(View.VISIBLE);
        adCircleProgress.setVisibility(View.VISIBLE);
        adCircleProgress.setAttributeResourceId(R.drawable.ic_close_icon);
        adCircleProgress.setUnfinishedStrokeWidth(dpToPx(5));
        adCircleProgress.setFinishedStrokeWidth(dpToPx(5));

    }

    @Override
    public void onDownloadStateInited(Drawable drawable) {

        imagePost.setVisibility(View.VISIBLE);
        imagePost.setImageDrawable(drawable);
        adCircleProgress.setVisibility(View.GONE);

    }

    @Override
    public void onNotDownloadedStateInited(Drawable drawable) {
        imagePost.setImageDrawable(drawable);
        adCircleProgress.setVisibility(View.VISIBLE);
        adCircleProgress.setAttributeResourceId(R.drawable.ic_file_download_black_24dp);
        adCircleProgress.setUnfinishedStrokeWidth(0);
        adCircleProgress.setFinishedStrokeWidth(0);
    }

    public void setMessageDisplayNameVisibility(int type) {
        if (messageDisplayName != null) {
            messageDisplayName.setVisibility(type);
        }
    }

    public ImageView getReplyImage() {
        return replyImage;
    }

    public void setReplyImageVisibility(int type) {
        if (replyImage != null) {
            replyImage.setVisibility(type);
        }
    }

    public void setLinkView(String siteName, String title, String description) {
        if (this.linkSiteName != null) {
            linkSiteName.setText(siteName);
            linkTitle.setText(title);
            linkDescription.setText(description);
        } else {
            initLinkView(siteName, title, description);
        }
    }

    public ImageView getLinkImage() {
        return linkImage;
    }

    public void setProgress(final int progress) {
        if (adCircleProgress!=null) {
            adCircleProgress.post(new Runnable() {
                @Override
                public void run() {
                    if (progress > 0) {
                        adCircleProgress.setProgress(progress);
                    }
                }
            });
        }
    }

    public View getMessageTextView() {
        return messageText;
    }

    public View getFileImage() {
        return imagePost;
    }

    public void setFileForwardView() {
        set.clone(constraintLayout);
        set.connect(fileBaseLayout.getId(), ConstraintSet.TOP, textForwardDisplayName.getId(), ConstraintSet.BOTTOM);
        set.applyTo(constraintLayout);
    }

    public View getProgressBar() {
        return adCircleProgress;
    }

    public void setRecordCreditials() {
        if (playTime==null){
            initRecordView();
        }
    }



    public SeekBar getSeekBar(){
        return seekBar;
    }
}
