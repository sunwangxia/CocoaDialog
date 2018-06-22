package com.berwin.cocoadialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
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

public final class CocoaDialog extends Dialog {

    private LinearLayout mContentPanel;
    private LinearLayout mButtonPanel;
    private LinearLayout mHeaderPanel;
    private View mPanelBorder;

    private final ProgressBar mProgressBar;
    private final List<EditText> mEditTextList;

    private final CharSequence mTitle;
    private final CharSequence mMessage;
    private final int mAnimStyleResId;
    private final CocoaDialogStyle mPreferredStyle;
    private final List<CocoaDialogAction> mActionList;

    private int mCustomWidth;
    private int mCustomHeight;
    private int mCustomGravity;
    private final View mCustomContentView;

    private CocoaDialog(Builder builder) {
        super(builder.context, android.R.style.Theme_Dialog);
        this.mTitle = builder.title;
        this.mMessage = builder.message;
        this.mActionList = builder.actionList;
        this.mProgressBar = builder.progressBar;
        this.mEditTextList = builder.editTextList;
        this.mAnimStyleResId = builder.animStyleRes;
        this.mPreferredStyle = builder.preferredStyle;
        this.mCustomHeight = builder.customHeight;
        this.mCustomWidth = builder.customWidth;
        this.mCustomGravity = builder.customGravity;
        this.mCustomContentView = builder.customContentView;
        if (builder.cancelable != null) {
            setCancelable(builder.cancelable);
        }
        if (builder.canceledOnTouchOutside != null) {
            setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        }
        setOnCancelListener(builder.onCancelListener);
        setOnDismissListener(builder.onDismissListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window mWindow = getWindow();
        assert mWindow != null;
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        mWindow.setBackgroundDrawableResource(android.R.color.transparent);
        View contentView;
        switch (mPreferredStyle) {
            case alert:
                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
                mCustomWidth = Math.round(Math.min(dm.widthPixels, dm.heightPixels) * 0.8f);
                mCustomHeight = WindowManager.LayoutParams.WRAP_CONTENT;
                contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_alert, null, false);
                mWindow.setWindowAnimations(mAnimStyleResId == 0 ? android.R.style.Animation_Dialog : mAnimStyleResId);
                mHeaderPanel = contentView.findViewById(R.id.headPanel);
                if (mTitle == null && mMessage == null && (mEditTextList == null || mEditTextList.isEmpty())) {
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
                if (mEditTextList != null) {
//                    int padding = DensityUtil.dip2px(getContext(), 4);
                    LinearLayout.LayoutParams firstParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    firstParams.topMargin = DensityUtil.dip2px(getContext(), 12);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = DensityUtil.dip2px(getContext(), 8);
                    for (int i = 0; i < mEditTextList.size(); i++) {
                        EditText editText = mEditTextList.get(i);
//                        editText.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_edit_text_background);
                        editText.setLayoutParams(i == 0 ? firstParams : params);
//                        editText.setPadding(padding, padding, padding, padding);
                        editText.setLines(1);
                        editText.setMaxLines(1);
                        mHeaderPanel.addView(editText);
                    }
                }
                break;
            case actionSheet:
                mCustomWidth = WindowManager.LayoutParams.MATCH_PARENT;
                mCustomHeight = WindowManager.LayoutParams.WRAP_CONTENT;
                contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_action_sheet, null, false);
                mWindow.setWindowAnimations(com.berwin.cocoadialog.R.style.Animation_CocoaDialog_ActionSheet);
                mWindow.setGravity(Gravity.BOTTOM);
                mHeaderPanel = contentView.findViewById(R.id.headPanel);
                if (mTitle == null && mMessage == null) {
                    mHeaderPanel.setVisibility(View.GONE);
                }
                break;
            case custom:
                if (mCustomContentView == null) {
                    throw new IllegalArgumentException("Custom content view can not be null, call CocoaDailog.Builder.setCustomContentView(View) first.");
                }
                mWindow.setGravity(mCustomGravity);
                mWindow.setWindowAnimations(mAnimStyleResId == 0 ? android.R.style.Animation_Dialog : mAnimStyleResId);
                if (mCustomWidth < WindowManager.LayoutParams.WRAP_CONTENT) {
                    mCustomWidth = WindowManager.LayoutParams.WRAP_CONTENT;
                }
                if (mCustomHeight < WindowManager.LayoutParams.WRAP_CONTENT) {
                    mCustomHeight = WindowManager.LayoutParams.WRAP_CONTENT;
                }
                setContentView(mCustomContentView);
                return;
            default:
                return;
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
        l.width = this.mCustomWidth;
        l.height = this.mCustomHeight;
        mWindow.setAttributes(l);
    }

    /**
     * Get the edit text list that added to this {@link CocoaDialog}.
     *
     * @return The list of the edit texts.
     */
    @Nullable
    public List<EditText> getEditTextList() {
        if (mEditTextList != null) {
            return new ArrayList<>(mEditTextList);
        }
        return null;
    }

    /**
     * Set the current progress to the progress bar.
     *
     * @param progress The current progress value, ignored if {@link Builder#addProgressBar(ProgressBarBuildHandler)} not called.
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
     * @return The current progress, return 0 if {@link Builder#addProgressBar(ProgressBarBuildHandler)} did not called.
     */
    public int getProgress() {
        return mProgressBar != null ? mProgressBar.getProgress() : 0;
    }


    private void resolveActions() {
        if (mPreferredStyle == CocoaDialogStyle.alert) {
            resolveAlertActions();
        } else {
            resolveActionSheetActions();
        }
    }

    private void resolveAlertActions() {
        if (mActionList == null || mActionList.isEmpty()) {
            mPanelBorder.setVisibility(View.GONE);
            return;
        }
        boolean isHeaderHidden = mTitle == null && mMessage == null && (mEditTextList == null || mEditTextList.isEmpty());
        if (isHeaderHidden || mActionList.size() > 2) {
            // 没有title、message且无输入框或者拥有3个以上Action时，每个Action Button占据整行空间
            mPanelBorder.setVisibility(isHeaderHidden ? View.GONE : View.VISIBLE);
            mButtonPanel.setOrientation(LinearLayout.VERTICAL);
            if (isHeaderHidden) {
                mButtonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
            }
            if (mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel && mActionList.size() > 1) { // 调整取消按钮放到最下方
                CocoaDialogAction cancelAction = mActionList.remove(0);
                mActionList.add(cancelAction);
            }
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 45));
            LinearLayout.LayoutParams borderParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
            for (int i = 0; i < mActionList.size(); i++) {
                final CocoaDialogAction action = mActionList.get(i);
                Button button = buildActionButton(action, buttonParams);
                button.setBackgroundResource(i == mActionList.size() - 1 ? (i == 0 ? R.drawable.cocoa_dialog_corner_radius : R.drawable.cocoa_dialog_bottom_radius) : (i == 0 && isHeaderHidden ? R.drawable.cocoa_dialog_top_radius : android.R.color.white));
                mButtonPanel.addView(button);
                View border = new View(getContext());
                border.setBackgroundColor(0xFFC8C7CC);
                border.setLayoutParams(borderParams);
                mButtonPanel.addView(border);
            }
            mButtonPanel.removeViewAt(mButtonPanel.getChildCount() - 1);
        } else {
            mPanelBorder.setVisibility(View.VISIBLE);
            mButtonPanel.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, DensityUtil.dip2px(getContext(), 43));
            buttonParams.weight = 1;
            LinearLayout.LayoutParams borderParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(), 1), ViewGroup.LayoutParams.MATCH_PARENT);
            for (int i = 0; i < mActionList.size(); i++) {
                final CocoaDialogAction action = mActionList.get(i);
                Button button = buildActionButton(action, buttonParams);
                button.setBackgroundResource(i == 0 ? (i == mActionList.size() - 1 ? R.drawable.cocoa_dialog_bottom_radius : R.drawable.cocoa_dialog_bottom_left_radius) : R.drawable.cocoa_dialog_bottom_right_radius);
                mButtonPanel.addView(button);
                // 添加按钮分隔线
                View border = new View(getContext());
                border.setBackgroundColor(0xFFC8C7CC);
                border.setLayoutParams(borderParams);
                mButtonPanel.addView(border);
            }
            mButtonPanel.removeViewAt(mButtonPanel.getChildCount() - 1);
        }
    }

    private void resolveActionSheetActions() {
        if (mActionList == null || mActionList.isEmpty()) {
            mPanelBorder.setVisibility(View.GONE);
            mHeaderPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
            return;
        }
        if (mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) {
            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 50));
            mHeaderPanel.setBackgroundResource(mActionList.size() == 1 ? com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius : com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius);
            final CocoaDialogAction cancelAction = mActionList.remove(0);
            cancelParams.topMargin = DensityUtil.dip2px(getContext(), 10);
            Button button = buildActionButton(cancelAction, cancelParams);
            button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
            mContentPanel.addView(button);
        }
        boolean isHeaderHidden = mTitle == null && mMessage == null;
        if (isHeaderHidden) {
            mPanelBorder.setVisibility(View.GONE);
            mButtonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
        }
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 50));
        LinearLayout.LayoutParams borderParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
        for (int i = 0; i < mActionList.size(); i++) {
            CocoaDialogAction action = mActionList.get(i);
            Button button = buildActionButton(action, buttonParams);
            button.setBackgroundResource(i == 0 && isHeaderHidden ? (i == mActionList.size() - 1 ? R.drawable.cocoa_dialog_corner_radius : R.drawable.cocoa_dialog_top_radius) : (i == mActionList.size() - 1 ? R.drawable.cocoa_dialog_bottom_radius : android.R.color.white));
            mButtonPanel.addView(button);
            // 按钮分隔线
            View border = new View(getContext());
            border.setBackgroundColor(0xFFC8C7CC);
            border.setLayoutParams(borderParams);
            mButtonPanel.addView(border);
        }
        mButtonPanel.removeViewAt(mButtonPanel.getChildCount() - 1);
    }

    private Button buildActionButton(CocoaDialogAction cocoaDialogAction, ViewGroup.LayoutParams layoutParams) {
        final CocoaDialogAction action = cocoaDialogAction;
        Button button = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
        button.setAllCaps(false);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        button.setLayoutParams(layoutParams);
        button.setText(action.getTitle());
        button.setTextColor(action.getStyle() == CocoaDialogActionStyle.destructive ? Color.RED : 0xFF007AFF);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (action.getOnClickListener() != null) {
                    action.getOnClickListener().onClick(CocoaDialog.this);
                }
            }
        });
        return button;
    }


    public static class Builder {

        final Context context;
        final CocoaDialogStyle preferredStyle;

        Boolean cancelable;
        Boolean canceledOnTouchOutside;
        OnCancelListener onCancelListener;
        OnDismissListener onDismissListener;

        int customWidth = WindowManager.LayoutParams.WRAP_CONTENT;
        int customHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        int customGravity = Gravity.CENTER;
        View customContentView;

        int animStyleRes = 0;
        CharSequence title;
        CharSequence message;
        ProgressBar progressBar;
        List<EditText> editTextList;
        List<CocoaDialogAction> actionList;


        public Builder(@NonNull Context context) {
            this(context, CocoaDialogStyle.alert);
        }

        public Builder(@NonNull Context context, @NonNull CocoaDialogStyle preferredStyle) {
            this.context = context;
            this.preferredStyle = preferredStyle;
        }

        /**
         * Sets whether this {@link CocoaDialog} is cancelable with the
         * {@link KeyEvent#KEYCODE_BACK BACK} key.
         *
         * @param cancelable Whether this {@link CocoaDialog} is cancelable,
         *                   true for yes, and false for no.
         * @return {@link Builder} instance.
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * Sets whether this dialog is canceled when touched outside the window's
         * bounds. If setting to true, the dialog is set to be cancelable if not
         * already set.
         *
         * @param cancel Whether the dialog should be canceled when touched outside
         *               the window, true for yes, and false for no.
         * @return {@link Builder} instance.
         */
        public Builder setCanceledOnTouchOutside(boolean cancel) {
            this.canceledOnTouchOutside = cancel;
            return this;
        }

        /**
         * Set a listener to be invoked when the {@link CocoaDialog} is canceled.
         *
         * <p>This will only be invoked when the dialog is canceled.
         * Cancel events alone will not capture all ways that
         * the dialog might be dismissed. If the creator needs
         * to know when a dialog is dismissed in general, use
         * {@link #setOnDismissListener}.</p>
         *
         * @param listener The {@link DialogInterface.OnCancelListener} to use.
         * @return {@link Builder} instance.
         */
        public Builder setOnCancelListener(OnCancelListener listener) {
            this.onCancelListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the dialog is dismissed.
         *
         * @param listener The {@link DialogInterface.OnDismissListener} to use.
         * @return {@link Builder} instance.
         */
        public Builder setOnDismissListener(OnDismissListener listener) {
            this.onDismissListener = listener;
            return this;
        }

        /**
         * Set the custom width for this {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param customWidth The custom width of pixels, support {@link WindowManager.LayoutParams#MATCH_PARENT} or {@link WindowManager.LayoutParams#WRAP_CONTENT}.
         * @return {@link Builder} instance.
         */
        public Builder setCustomWidth(int customWidth) {
            this.customWidth = customWidth;
            return this;
        }

        /**
         * Set the custom height for this {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param customHeight The custom height of pixels, support {@link WindowManager.LayoutParams#MATCH_PARENT} or {@link WindowManager.LayoutParams#WRAP_CONTENT}.
         * @return {@link Builder} instance.
         */
        public Builder setCustomHeight(int customHeight) {
            this.customHeight = customHeight;
            return this;
        }

        /**
         * Set the custom gravity for this {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#custom}.
         * @param gravity see {@link Gravity}
         * @return {@link Builder} instance.
         */
        public Builder setCustomGravity(int gravity) {
            this.customGravity = gravity;
            return this;
        }

        /**
         * Set the custom view for this {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param contentView The custom content view.
         * @return {@link Builder} instance.
         */
        public Builder setCustomContentView(View contentView) {
            this.customContentView = contentView;
            return this;
        }

        /**
         * Set the animation style(include enter animation and exit animation) for this {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#alert}.
         *
         * @param animStyleResId Style resource id of the animation.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setAnimStyle(@StyleRes int animStyleResId) {
            animStyleRes = animStyleResId;
            return this;
        }

        /**
         * Set title for this {@link CocoaDialog}, will be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param title The title for the cocoa dialog.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        /**
         * Set title for this {@link CocoaDialog}, will be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param titleResId The title resource id for this {@link CocoaDialog}, ignored when resource id is zero.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setTitle(@StringRes int titleResId) {
            if (titleResId != 0) {
                this.title = context.getString(titleResId);
            }
            return this;
        }

        /**
         * Set message for this {@link CocoaDialog}, will be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param message The message for this {@link CocoaDialog}.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }


        /**
         * Set message for this {@link CocoaDialog}, will be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param messageResId The message resource id for this {@link CocoaDialog}, ignored when resource id is zero.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setMessage(@StringRes int messageResId) {
            if (messageResId != 0) {
                this.message = context.getString(messageResId);
            }
            return this;
        }

        /**
         * Add an action to {@link CocoaDialog}, appears as a button, will be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param action {@link CocoaDialogAction} instance.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder addAction(@NonNull CocoaDialogAction action) {
            if (actionList == null) {
                actionList = new ArrayList<>();
            }
            if (action.getStyle() == CocoaDialogActionStyle.cancel) {
                if (actionList.size() > 0 && actionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) {
                    throw new IllegalArgumentException("Cocoa dialog can only have one action with a style of CocoaDialogActionStyle.Cancel");
                } else {
                    actionList.add(0, action);
                }
            } else {
                actionList.add(action);
            }
            return this;

        }

        /**
         * Add an action for the {@link CocoaDialog}, appears as a button, will be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param title    The title of the action.
         * @param style    The {@link CocoaDialogActionStyle} of the action, {@link CocoaDialogActionStyle#cancel} always lay at the left or bottom of the actions, {@link CocoaDialogActionStyle#destructive}'s text will be red.
         * @param listener The click listener, when user click the button {@link CocoaDialogAction.OnClickListener#onClick(CocoaDialog)} will be called.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder addAction(String title, @NonNull CocoaDialogActionStyle style, CocoaDialogAction.OnClickListener listener) {
            return addAction(new CocoaDialogAction(title, style, listener));
        }

        /**
         * Add an edit text to this {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#alert}.
         *
         * @param configurationHandler The handler to configure the edit text, such as text color, hint and default text.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder addEditText(EditTextConfigurationHandler configurationHandler) {
            Context context = this.context;
            if (preferredStyle != CocoaDialogStyle.alert) {
                throw new IllegalArgumentException("EditText can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
            }
            EditText editText = new EditText(context);
            progressBar = null;
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
         * Add a progress bar to this {@link CocoaDialog}, only effective on a cocoa dialog with a style of {@link CocoaDialogStyle#alert}.
         *
         * @param handler The handler to build and configure the progress bar.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public CocoaDialog.Builder addProgressBar(@NonNull ProgressBarBuildHandler handler) {
            Context context = this.context;
            if (preferredStyle != CocoaDialogStyle.alert) {
                throw new IllegalArgumentException("ProgressBar can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
            }
            progressBar = handler.build(context);
            if (editTextList != null && editTextList.size() > 0) {
                editTextList.clear();
            }
            return this;
        }

        /**
         * Build a {@link CocoaDialog}.
         *
         * @return {@link CocoaDialog} instance.
         */
        public CocoaDialog build() {
            return new CocoaDialog(this);
        }
    }

}
