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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.berwin.cocoadialog.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class CocoaDialog extends Dialog {

    private LinearLayout mContentPanel;
    private LinearLayout mButtonPanel;
    private LinearLayout mHeaderPanel;
    private TextView mPanelBorder;

    private final ProgressBar mProgressBar;
    private final List<EditText> mEditTextList;

    private final CharSequence mTitle;
    private final CharSequence mMessage;
    private final int mAnimStyleResId;
    private final CocoaDialogStyle mPreferredStyle;
    private final List<CocoaDialogAction> mActionList;

    private int mCustomWidth;
    private int mCustomHeight;
    private View mCustomContentView;

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
        this.mCustomContentView = builder.customContentView;
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
                contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_alert, null, false);
                if (mAnimStyleResId == 0) {
                    mWindow.setWindowAnimations(android.R.style.Animation_Dialog);
                } else {
                    mWindow.setWindowAnimations(mAnimStyleResId);
                }
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
                    for (int i = 0; i < mEditTextList.size(); i++) {
                        EditText editText = mEditTextList.get(i);
                        editText.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_edit_text_background);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topMargin = DensityUtil.dip2px(getContext(), i == 0 ? 12 : 8);
                        editText.setLayoutParams(params);
                        int padding = DensityUtil.dip2px(getContext(), 4);
                        editText.setPadding(padding, padding, padding, padding);
                        editText.setLines(1);
                        mHeaderPanel.addView(editText);
                    }
                }
                break;
            case actionSheet:
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
                if (mCustomWidth == 0 || mCustomWidth < -2) {
                    mCustomWidth = WindowManager.LayoutParams.WRAP_CONTENT;
                }
                if (mCustomHeight == 0 || mCustomHeight < -2) {
                    mCustomHeight = WindowManager.LayoutParams.WRAP_CONTENT;
                }
                contentView = mCustomContentView;
                break;
            default:
                contentView = new FrameLayout(getContext());
                break;
        }
        if (mPreferredStyle != CocoaDialogStyle.custom) {
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
        }
        setContentView(contentView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window mWindow = getWindow();
        assert mWindow != null;
        WindowManager.LayoutParams l = mWindow.getAttributes();
        switch (mPreferredStyle) {
            case actionSheet:
                l.width = WindowManager.LayoutParams.MATCH_PARENT;
                l.height = WindowManager.LayoutParams.WRAP_CONTENT;
                break;
            case alert:
                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
                l.width = Math.round(Math.min(dm.widthPixels, dm.heightPixels) * 0.8f);
                l.height = WindowManager.LayoutParams.WRAP_CONTENT;
                break;
            case custom:
                l.width = this.mCustomWidth;
                l.height = this.mCustomHeight;
                break;
        }
        mWindow.setAttributes(l);
    }

    /**
     * Get the edit text list that added to the {@link CocoaDialog}.
     *
     * @return The list of the edit texts.
     */
    @Nullable
    public List<EditText> getEditTextList() {
        if (mEditTextList != null) {
            List<EditText> list = new ArrayList<>();
            list.addAll(mEditTextList);
            return list;
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
     * @return The current progress, return 0 if {@link Builder#addProgressBar(ProgressBarBuildHandler)} not called.
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
        boolean isHeaderHidden = mTitle == null && mMessage == null && (mEditTextList == null || mEditTextList.isEmpty());
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
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 45));
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
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, DensityUtil.dip2px(getContext(), 43));
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

        final Context context;
        final CocoaDialogStyle preferredStyle;

        int customWidth = 0;
        int customHeight = 0;
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
         * Set the custom width for the {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#custom}.
         * @param customWidth The custom width of pixels, can use {@link WindowManager.LayoutParams#MATCH_PARENT} or {@link WindowManager.LayoutParams#WRAP_CONTENT} also.
         * @return {@link Builder} instance.
         */
        public Builder setCustomWidth(int customWidth) {
            this.customWidth = customWidth;
            return this;
        }

        /**
         * Set the custom height for the {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#custom}.
         * @param customHeight The custom height of pixels, can use {@link WindowManager.LayoutParams#MATCH_PARENT} or {@link WindowManager.LayoutParams#WRAP_CONTENT} also.
         * @return {@link Builder} instance.
         */
        public Builder setCustomHeight(int customHeight) {
            this.customHeight = customHeight;
            return this;
        }

        /**
         * Set the custom view for the {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#custom}.
         * @param contentView The custom content view.
         * @return {@link Builder} instance.
         */
        public Builder setCustomContentView(View contentView) {
            this.customContentView = contentView;
            return this;
        }

        /**
         * Set the animation style(include enter animation and exit animation) for the {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#alert}.
         *
         * @param animStyleResId Style resource id of the animation.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setAnimStyle(@StyleRes int animStyleResId) {
            animStyleRes = animStyleResId;
            return this;
        }

        /**
         * Set title for the {@link CocoaDialog}, would be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param title The title for the cocoa dialog.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        /**
         * Set title for the {@link CocoaDialog}, would be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param titleResId The title resource id for the {@link CocoaDialog}, ignored when resource id is zero.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setTitle(@StringRes int titleResId) {
            if (titleResId != 0) {
                this.title = context.getString(titleResId);
            }
            return this;
        }

        /**
         * Set message for the {@link CocoaDialog}, would be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param message The message for the {@link CocoaDialog}.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }


        /**
         * Set message for the {@link CocoaDialog}, would be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param messageResId The message resource id for the {@link CocoaDialog}, ignored when resource id is zero.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder setMessage(@StringRes int messageResId) {
            if (messageResId != 0) {
                this.message = context.getString(messageResId);
            }
            return this;
        }

        /**
         * Add action to {@link CocoaDialog}, the {@link CocoaDialogAction} will appears as a button of the cocoa dialog, would be ignored on the style of {@link CocoaDialogStyle#custom}.
         *
         * @param action {@link CocoaDialogAction} instance.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public Builder addAction(@NonNull CocoaDialogAction action) {
            if (actionList == null) {
                actionList = new ArrayList<>();
            }
            if (action.getStyle() == CocoaDialogActionStyle.cancel) {
                if (actionList != null && actionList.size() > 0 && actionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) {
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
         * Add an edit text to the {@link CocoaDialog}, only effective on the style of {@link CocoaDialogStyle#alert}.
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
         * Add a progress bar to the {@link CocoaDialog}, only effective on a cocoa dialog with a style of {@link CocoaDialogStyle#alert}.
         *
         * @param handler The handler to build and configure the progress bar.
         * @return {@link CocoaDialog.Builder} instance.
         */
        public CocoaDialog.Builder addProgressBar(@NonNull ProgressBarBuildHandler handler) {
            Context context = this.context;
            CocoaDialogStyle style = preferredStyle;
            if (style != CocoaDialogStyle.alert) {
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
