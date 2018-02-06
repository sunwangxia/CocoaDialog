package com.berwin.cocoadialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.berwin.cocoadialog.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class CocoaDialog extends Dialog implements CocoaDialogInterface {

    @Nullable
    public List<EditText> editTextList;

    private TextView mPanelBorder;
    private ProgressBar mProgressBar;
    private LinearLayout mContentPanel;
    private LinearLayout mButtonPanel;
    private LinearLayout mHeaderPanel;

    private CharSequence mTitle;
    private CharSequence mMessage;
    private int mAnimStyleResId = 0;
    private CocoaDialogStyle mPreferredStyle;
    private List<CocoaDialogAction> mActionList;

    private CocoaDialog(@NonNull Context context, int themeResId, @NonNull CocoaDialogStyle preferredStyle) {
        super(context, themeResId);
        setPreferredStyle(preferredStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window mWindow = getWindow();
        assert mWindow != null;
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        mWindow.setBackgroundDrawableResource(android.R.color.transparent);
        View contentView;
        if (mPreferredStyle == CocoaDialogStyle.alert) {
            contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_alert, null, false);
            if (mAnimStyleResId <= 0) {
                mWindow.setWindowAnimations(android.R.style.Animation_Dialog);
            } else {
                mWindow.setWindowAnimations(mAnimStyleResId);
            }
            mHeaderPanel = contentView.findViewById(R.id.headPanel);
            if (mTitle == null && mMessage == null && (editTextList == null || editTextList.isEmpty())) {
                mHeaderPanel.setVisibility(View.GONE);
            }
            if (mProgressBar != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (mTitle != null && mMessage != null) {
                    params.topMargin = DensityUtil.dip2px(getContext(), 10);
                }
                params.gravity = Gravity.CENTER_HORIZONTAL;
                mProgressBar.setLayoutParams(params);
                mHeaderPanel.addView(mProgressBar);
            }
            if (editTextList != null) {
                for (EditText editText : editTextList) {
                    editText.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_edit_text_background);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = DensityUtil.dip2px(getContext(), 8);
                    editText.setLayoutParams(params);
                    int padding = DensityUtil.dip2px(getContext(), 4);
                    editText.setPadding(padding, padding, padding, padding);
                    editText.setLines(1);
                    mHeaderPanel.addView(editText);
                }
            }
        } else {
            contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_action_sheet, null, false);
            mWindow.setWindowAnimations(com.berwin.cocoadialog.R.style.Animation_CocoaDialog_ActionSheet);
            mWindow.setGravity(Gravity.BOTTOM);
            mHeaderPanel = contentView.findViewById(R.id.headPanel);
            if (mTitle == null && mMessage == null) {
                mHeaderPanel.setVisibility(View.GONE);
            }

        }
        TextView titleText = contentView.findViewById(R.id.title);
        TextView messageText = contentView.findViewById(R.id.message);
        if (mTitle != null) {
            titleText.setText(mTitle);
        } else {
            titleText.setVisibility(View.GONE);
        }
        if (mMessage != null) {
            messageText.setText(mMessage);
        } else {
            messageText.setVisibility(View.GONE);
        }
        mContentPanel = contentView.findViewById(R.id.contentPanel);
        mPanelBorder = contentView.findViewById(R.id.panelBorder);
        mButtonPanel = contentView.findViewById(R.id.buttonPanel);
        resolveActions();
        setContentView(contentView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window mWindow = getWindow();
        assert mWindow != null;
        WindowManager.LayoutParams l = mWindow.getAttributes();
        if (mPreferredStyle == CocoaDialogStyle.alert) {
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            l.width = Math.min(dm.widthPixels, dm.heightPixels);
        } else {
            l.width = WindowManager.LayoutParams.MATCH_PARENT;
        }
        l.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(l);
    }

    /**
     * Set the preferred style for the cocoa dialog, default value is CocoaDialogStyle.alert.
     *
     * @param preferredStyle Preferred style for the cocoa dialog.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog setPreferredStyle(@NonNull CocoaDialogStyle preferredStyle) {
        this.mPreferredStyle = preferredStyle;
        return this;
    }

    /**
     * Set the animation style(include enter animation and exit animation) for the cocoa dialog,
     * only effective on a cocoa dialog with a style of CocoaDialogStyle.alert.
     *
     * @param animStyleResId Style resource id of the animation.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog setAnimStyle(@StyleRes int animStyleResId) {
        this.mAnimStyleResId = animStyleResId;
        return this;
    }


    @Override
    public void setTitle(CharSequence title) {
        this.mTitle = title;
    }

    @Override
    public void setTitle(@StringRes int titleResId) {
        if (titleResId != 0)
            this.mTitle = getContext().getString(titleResId);
    }

    /**
     * Set title for the cocoa dialog.
     *
     * @param title The title for the cocoa dialog.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog setTitleText(CharSequence title) {
        setTitle(title);
        return this;
    }

    /**
     * Set title for the cocoa dialog.
     *
     * @param titleResId The title  resource id for the cocoa dialog, ignored when resource id is zero.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog setTitleText(@StringRes int titleResId) {
        if (titleResId != 0)
            setTitle(titleResId);
        return this;
    }

    /**
     * Set message for the cocoa dialog.
     *
     * @param message The message for the cocoa dialog.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog setMessage(CharSequence message) {
        this.mMessage = message;
        return this;
    }

    /**
     * Set message for the cocoa dialog.
     *
     * @param messageResId The message resource id for the cocoa dialog, ignored when resource id is zero.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog setMessage(@StringRes int messageResId) {
        if (messageResId != 0)
            this.mMessage = getContext().getString(messageResId);
        return this;
    }

    /**
     * Add action to cocoa dialog.
     *
     * @param action CocoaDialogAction, appears as a button of the cocoa dialog.
     * @return Cocoa dialog instance.
     */
    public CocoaDialog addAction(@NonNull CocoaDialogAction action) {
        if (mActionList == null) {
            mActionList = new ArrayList<>();
        }
        if (action.getStyle() == CocoaDialogActionStyle.cancel) {
            if (mActionList != null && mActionList.size() > 0 && mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) {
                throw new IllegalArgumentException("Cocoa dialog can only have one action with a style of CocoaDialogActionStyle.Cancel");
            } else {
                mActionList.add(0, action);
            }
        } else {
            mActionList.add(action);
        }
        return this;
    }

    /**
     * Add an edit text to the cocoa dialog, only effective on a cocoa dialog with a style of CocoaDialogStyle.alert.
     *
     * @param configurationHandler The handler to configure the edit text, such as text color, hint and default text.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog addEditText(EditTextConfigurationHandler configurationHandler) {
        CocoaDialogStyle style = this.mPreferredStyle;
        if (style != CocoaDialogStyle.alert) {
            throw new IllegalArgumentException("EditText can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
        }
        EditText editText = new EditText(getContext());
        mProgressBar = null;
        if (editTextList == null) {
            editTextList = new ArrayList<>();
        }
        editTextList.add(editText);
        if (configurationHandler != null) {
            configurationHandler.onEditTextAdded(editText);
        }
        return this;
    }

    /**
     * Add a progress bar to the cocoa dialog, only effective on a cocoa dialog with a style of CocoaDialogStyle.alert.
     *
     * @param handler The handler to build and configure the progress bar.
     * @return {@link CocoaDialog} instance.
     */
    public CocoaDialog addProgressBar(@NonNull ProgressBarBuildHandler handler) {
        if (mPreferredStyle != CocoaDialogStyle.alert) {
            throw new IllegalArgumentException("ProgressBar can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
        }
        mProgressBar = handler.build(getContext());
        if (editTextList != null && editTextList.size() > 0) {
            editTextList.clear();
        }
        return this;
    }

    /**
     * Set the current progress to the progress bar.
     *
     * @param progress The current progress value, ignored if {@link #addProgressBar(ProgressBarBuildHandler)} not called.
     */
    public void setProgress(int progress) {
        if (mProgressBar != null) {
            int newProgress = progress < 0 ? 0 : progress > mProgressBar.getMax() ? mProgressBar.getMax() : progress;
            mProgressBar.setProgress(newProgress);
        }
    }

    /**
     * Get the the current progress of the progress bar.
     *
     * @return The current progress, return 0 if {@link #addProgressBar(ProgressBarBuildHandler)} not called.
     */
    public int getProgress() {
        return mProgressBar != null ? mProgressBar.getProgress() : 0;
    }

    private CocoaDialog setProgressBar(ProgressBar progressBar) {
        this.mProgressBar = progressBar;
        return this;
    }

    private void resolveActions() {
        if (mPreferredStyle == CocoaDialogStyle.alert) {
            resolveAlertActions();
        } else {
            resolveActionSheetActions();
        }
    }

    private void resolveAlertActions() {
        boolean isHeaderHidden = mTitle == null && mMessage == null && (editTextList == null || editTextList.isEmpty());
        if (mActionList == null || mActionList.isEmpty()) {
            mPanelBorder.setVisibility(View.GONE);
        } else if (isHeaderHidden || mActionList.size() > 2) {
            // 没有title、message且无输入框或者拥有3个以上Action时，每个Action Button占用整行空间
            mPanelBorder.setVisibility(View.VISIBLE);
            mButtonPanel.setOrientation(LinearLayout.VERTICAL);
            if (mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) { // 调整取消按钮放到最下方
                CocoaDialogAction cancelAction = mActionList.remove(0);
                mActionList.add(cancelAction);
            }
            for (int i = 0; i < mActionList.size(); i++) {
                final CocoaDialogAction action = mActionList.get(i);
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 50));
                Button button = buildActionButton(action, buttonParams);
                boolean needBorder = i < mActionList.size() - 1;
                if (i == 0 && isHeaderHidden) {
                    mPanelBorder.setVisibility(View.GONE);
                    mButtonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                    button.setBackgroundResource(needBorder ? com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius : com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                } else if (needBorder) {
                    button.setBackgroundColor(Color.WHITE);
                } else {
                    button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius);
                    mButtonPanel.addView(button);
                    break;
                }
                mButtonPanel.addView(button);
                TextView border = new TextView(getContext());
                border.setBackgroundColor(0xFFC8C7CC);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
                border.setLayoutParams(params);
                mButtonPanel.addView(border);
            }
        } else {
            mPanelBorder.setVisibility(View.VISIBLE);
            mButtonPanel.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < mActionList.size(); i++) {
                final CocoaDialogAction action = mActionList.get(i);
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, DensityUtil.dip2px(getContext(), 45));
                buttonParams.weight = 1;
                Button button = buildActionButton(action, buttonParams);
                if (mButtonPanel.getChildCount() > 0) {
                    // 添加按钮分隔线
                    TextView border = new TextView(getContext());
                    border.setBackgroundColor(0xFFC8C7CC);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(), 1), ViewGroup.LayoutParams.MATCH_PARENT);
                    border.setLayoutParams(params);
                    mButtonPanel.addView(border);
                    button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_right_radius);
                } else {
                    button.setBackgroundResource(i == mActionList.size() - 1 ? com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius : com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_left_radius);
                }
                mButtonPanel.addView(button);
            }
        }
    }

    private void resolveActionSheetActions() {
        mPanelBorder.setVisibility(View.GONE);
        if (mActionList == null || mActionList.isEmpty()) {
            mHeaderPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
            return;
        }
        boolean hasCancelAction = mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel;
        if (hasCancelAction && mActionList.size() == 1) {
            mHeaderPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
        } else {
            mHeaderPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius);
        }
        for (int i = 0; i < mActionList.size(); i++) {
            CocoaDialogAction action = mActionList.get(i);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 50));
            Button button = buildActionButton(action, buttonParams);
            if (action.getStyle() == CocoaDialogActionStyle.cancel) {
                buttonParams.topMargin = DensityUtil.dip2px(getContext(), 10);
                button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                mContentPanel.addView(button);
                continue;
            }
            // 按钮分隔线
            TextView border = new TextView(getContext());
            border.setBackgroundColor(0xFFC8C7CC);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
            border.setLayoutParams(params);
            if (((i == 0 && action.getStyle() != CocoaDialogActionStyle.cancel) || (i == 1 && hasCancelAction)) && mTitle == null && mMessage == null) {
                mButtonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                button.setBackgroundResource(i < mActionList.size() - 1 ? com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius : com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
            } else if (i == mActionList.size() - 1) {
                mButtonPanel.addView(border);
                button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius);
            } else {
                mButtonPanel.addView(border);
                button.setBackgroundColor(Color.WHITE);
            }
            mButtonPanel.addView(button);
        }
    }

    private Button buildActionButton(CocoaDialogAction cocoaDialogAction, ViewGroup.LayoutParams layoutParams) {
        final CocoaDialogAction action = cocoaDialogAction;
        Button button = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
        button.setAllCaps(false);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        button.setLayoutParams(layoutParams);
        button.setText(action.getTitle());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (action.getOnClickListener() != null) {
                    action.getOnClickListener().onClick(CocoaDialog.this);
                }
            }
        });
        switch (action.getStyle()) {
            case cancel:
            case normal:
                button.setTextColor(0xFF007AFF);
                break;
            case destructive:
                button.setTextColor(Color.RED);
                break;
        }
        return button;
    }


    public static class Builder {

        Context mContext;
        CharSequence mTitle;
        CharSequence mMessage;
        int mAnimStyleRes = 0;
        ProgressBar mProgressBar;
        List<EditText> mEditTextList;
        CocoaDialogStyle mPreferredStyle;
        List<CocoaDialogAction> mActionList;

        public Builder(@NonNull Context context) {
            this(context, CocoaDialogStyle.alert);
        }

        public Builder(@NonNull Context context, @NonNull CocoaDialogStyle preferredStyle) {
            mContext = context;
            mPreferredStyle = preferredStyle;
        }

        public Builder(@NonNull Context context, @StringRes int titleRes, @StringRes int messageRes, @NonNull CocoaDialogStyle preferredStyle) {
            this(context, preferredStyle);
            mTitle = titleRes != 0 ? context.getString(titleRes) : null;
            mMessage = messageRes != 0 ? context.getString(messageRes) : null;
        }


        public Builder(@NonNull Context context, CharSequence title, CharSequence message, @NonNull CocoaDialogStyle preferredStyle) {
            this(context, preferredStyle);
            mTitle = title;
            mMessage = message;
        }

        /**
         * Set the preferred style for the cocoa dialog, default value is CocoaDialogStyle.alert.
         *
         * @param preferredStyle Preferred style for the cocoa dialog.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setPreferredStyle(@NonNull CocoaDialogStyle preferredStyle) {
            this.mPreferredStyle = preferredStyle;
            return this;
        }

        /**
         * Set the animation style(include enter animation and exit animation) for the cocoa dialog,
         * only effective on a cocoa dialog with a style of CocoaDialogStyle.alert.
         *
         * @param animStyleResId Style resource id of the animation.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setAnimStyle(@StyleRes int animStyleResId) {
            mAnimStyleRes = animStyleResId;
            return this;
        }

        /**
         * Set title for the cocoa dialog.
         *
         * @param title The title for the cocoa dialog.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Set title for the cocoa dialog.
         *
         * @param titleResId The title resource id for the cocoa dialog, ignored when resource id is zero.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setTitle(@StringRes int titleResId) {
            if (titleResId != 0)
                this.mTitle = mContext.getString(titleResId);
            return this;
        }

        /**
         * Set message for the cocoa dialog.
         *
         * @param message The message for the cocoa dialog.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }


        /**
         * Set message for the cocoa dialog.
         *
         * @param messageResId The message resource id for the cocoa dialog, ignored when resource id is zero.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setMessage(@StringRes int messageResId) {
            if (messageResId != 0)
                this.mMessage = mContext.getString(messageResId);
            return this;
        }

        /**
         * Add action to cocoa dialog.
         *
         * @param action CocoaDialogAction, appears as a button of the cocoa dialog.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder addAction(@NonNull CocoaDialogAction action) {
            if (mActionList == null) {
                mActionList = new ArrayList<>();
            }
            if (action.getStyle() == CocoaDialogActionStyle.cancel) {
                if (mActionList != null && mActionList.size() > 0 && mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) {
                    throw new IllegalArgumentException("Cocoa dialog can only have one action with a style of CocoaDialogActionStyle.Cancel");
                } else {
                    mActionList.add(0, action);
                }
            } else {
                mActionList.add(action);
            }
            return this;

        }

        /**
         * Add an edit text to the cocoa dialog, only effective on a cocoa dialog with a style of CocoaDialogStyle.alert.
         *
         * @param configurationHandler The handler to configure the edit text, such as text color, hint and default text.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder addEditText(EditTextConfigurationHandler configurationHandler) {
            Context context = mContext;
            CocoaDialogStyle style = mPreferredStyle;
            if (style != CocoaDialogStyle.alert) {
                throw new IllegalArgumentException("EditText can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
            }
            EditText editText = new EditText(context);
            mProgressBar = null;
            if (mEditTextList == null) {
                mEditTextList = new ArrayList<>();
            }
            mEditTextList.add(editText);
            if (configurationHandler != null) {
                configurationHandler.onEditTextAdded(editText);
            }
            return this;
        }

        /**
         * Add a progress bar to the cocoa dialog, only effective on a cocoa dialog with a style of CocoaDialogStyle.alert.
         *
         * @param handler The handler to build and configure the progress bar.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public CocoaDialog.Builder addProgressBar(@NonNull ProgressBarBuildHandler handler) {
            Context context = mContext;
            CocoaDialogStyle style = mPreferredStyle;
            if (style != CocoaDialogStyle.alert) {
                throw new IllegalArgumentException("ProgressBar can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
            }
            mProgressBar = handler.build(context);
            if (mEditTextList != null && mEditTextList.size() > 0) {
                mEditTextList.clear();
            }
            return this;
        }

        /**
         * Create a cocoa dialog.
         *
         * @return {@link CocoaDialog} instance.
         */
        public CocoaDialog create() {
            CocoaDialog dialog = new CocoaDialog(mContext, android.R.style.Theme_Dialog, mPreferredStyle)
                    .setAnimStyle(mAnimStyleRes)
                    .setTitleText(mTitle)
                    .setMessage(mMessage)
                    .setProgressBar(mProgressBar);
            dialog.editTextList = mEditTextList;
            dialog.mActionList = mActionList;
            return dialog;
        }
    }

}
