package com.berwin.cocoadialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
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

public class CocoaDialogFragment extends DialogFragment implements CocoaDialogInterface {

    private static final String ARGUMENT_TITLE = "title";
    private static final String ARGUMENT_MESSAGE = "message";
    private static final String ARGUMENT_STYLE = "style";
    private static final String ARGUMENT_ANIMATION_STYLE = "animation_style";

    @Nullable
    public List<EditText> editTextList;

    private TextView mPanelBorder;
    private ProgressBar mProgressBar;
    private LinearLayout mButtonPanel;
    private LinearLayout mHeaderPanel;
    private LinearLayout mContentPanel;

    private CharSequence mTitle;
    private CharSequence mMessage;
    private int mAnimStyleRes = 0;
    private CocoaDialogStyle mPreferredStyle;
    private List<CocoaDialogAction> mActionList;

    public CocoaDialogFragment() {
        super();
        if (getArguments() == null) {
            Bundle bundle = new Bundle();
            setArguments(bundle);
        }
    }

    /**
     * Create a cocoa dialog instance with the preferred style.
     *
     * @param preferredStyle The preferred syle of the cocoa dialog.
     * @return {@link CocoaDialogFragment} instance.
     */
    public static CocoaDialogFragment create(@NonNull CocoaDialogStyle preferredStyle) {
        return create(null, null, preferredStyle);
    }

    /**
     * Create a cocoa dialog instance with the title, message and preferred style.
     *
     * @param title          The title of the cocoa dialog.
     * @param message        The message of the cocoa dialog.
     * @param preferredStyle The preferred syle of the cocoa dialog.
     * @return {@link CocoaDialogFragment} instance.
     */
    public static CocoaDialogFragment create(CharSequence title, CharSequence message, @NonNull CocoaDialogStyle preferredStyle) {
        CocoaDialogFragment dialog = new CocoaDialogFragment();
        Bundle bundle = dialog.getArguments();
        bundle.putCharSequence(ARGUMENT_TITLE, title);
        bundle.putCharSequence(ARGUMENT_MESSAGE, message);
        bundle.putSerializable(ARGUMENT_STYLE, preferredStyle);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Dialog);
        Bundle bundle = getArguments();
        mTitle = bundle.getCharSequence(ARGUMENT_TITLE);
        mMessage = bundle.getCharSequence(ARGUMENT_MESSAGE);
        mPreferredStyle = (CocoaDialogStyle) bundle.getSerializable(ARGUMENT_STYLE);
        mAnimStyleRes = bundle.getInt(ARGUMENT_ANIMATION_STYLE, 0);
        if (mPreferredStyle == null) {
            mPreferredStyle = CocoaDialogStyle.alert;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        assert window != null;
        WindowManager.LayoutParams l = window.getAttributes();
        if (mPreferredStyle == CocoaDialogStyle.alert) {
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            l.width = Math.min(dm.widthPixels, dm.heightPixels);
        } else {
            l.width = WindowManager.LayoutParams.MATCH_PARENT;
        }
        l.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(l);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window mWindow = getDialog().getWindow();
        assert mWindow != null;
        mWindow.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams l = mWindow.getAttributes();
        l.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindow.setAttributes(l);
        View contentView;
        if (mPreferredStyle == CocoaDialogStyle.alert) {
            contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_alert, null, false);
            if (mAnimStyleRes <= 0) {
                mWindow.setWindowAnimations(android.R.style.Animation_Dialog);
            } else {
                mWindow.setWindowAnimations(mAnimStyleRes);
            }
            mHeaderPanel = contentView.findViewById(R.id.headPanel);
            if (mTitle == null && mMessage == null && (editTextList == null || editTextList.isEmpty()) /*&& actionList.size() > 2*/) {
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
        return contentView;
    }

    /**
     * Set the animation style(include enter animation and exit animation) for the cocoa dialog,
     * only effective on a cocoa dialog with a style of CocoaDialogStyle.alert.
     *
     * @param animStyleResId Resource ID of the animation.
     * @return {@link CocoaDialogFragment} instance.
     */
    public CocoaDialogFragment setAnimStyle(@StyleRes int animStyleResId) {
        Bundle bundle = getArguments();
        bundle.putInt(ARGUMENT_ANIMATION_STYLE, animStyleResId);
        return this;
    }

    /**
     * Set title for the cocoa dialog.
     *
     * @param title The title for the cocoa dialog.
     * @return {@link CocoaDialogFragment} instance.
     */
    public CocoaDialogFragment setTitle(CharSequence title) {
        Bundle bundle = getArguments();
        bundle.putCharSequence(ARGUMENT_TITLE, title);
        return this;
    }

    /**
     * Set message for the cocoa dialog.
     *
     * @param message The message for the cocoa dialog.
     * @return {@link CocoaDialogFragment} instance.
     */
    public CocoaDialogFragment setMessage(CharSequence message) {
        Bundle bundle = getArguments();
        bundle.putCharSequence(ARGUMENT_MESSAGE, message);
        return this;
    }

    /**
     * Add action to cocoa dialog.
     *
     * @param action CocoaDialogAction, appears as a button of the cocoa dialog.
     * @return {@link CocoaDialogFragment} instance.
     */
    public CocoaDialogFragment addAction(@NonNull CocoaDialogAction action) {
        if (mActionList == null) {
            mActionList = new ArrayList<>();
        }
        if (action.getStyle() == CocoaDialogActionStyle.cancel) {
            if (!mActionList.isEmpty() && mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) {
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
     * @return {@link CocoaDialogFragment} instance.
     */
    public CocoaDialogFragment addEditText(@NonNull Context context, EditTextConfigurationHandler configurationHandler) {
        CocoaDialogStyle style = (CocoaDialogStyle) getArguments().getSerializable(ARGUMENT_STYLE);
        if (style != CocoaDialogStyle.alert) {
            throw new IllegalArgumentException("EditText can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
        }
        EditText editText = new EditText(context);
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
     * @return {@link CocoaDialogFragment} instance.
     */
    public CocoaDialogFragment addProgressBar(@NonNull ProgressBarBuildHandler handler) {
        CocoaDialogStyle preferredStyle = (CocoaDialogStyle) getArguments().getSerializable(ARGUMENT_STYLE);
        if (preferredStyle != CocoaDialogStyle.alert) {
            throw new IllegalArgumentException("ProgressBar can only be added to a cocoa dialog of style CocoaDialogStyle.alert");
        }
        mProgressBar = handler.build(getContext());
        if (mProgressBar != null && editTextList != null && editTextList.size() > 0) {
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
                    action.getOnClickListener().onClick(CocoaDialogFragment.this);
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

}

