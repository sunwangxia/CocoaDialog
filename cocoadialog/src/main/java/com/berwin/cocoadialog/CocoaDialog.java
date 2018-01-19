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
import android.widget.TextView;
import com.berwin.cocoadialog.utils.DensityUtil;
import java.util.ArrayList;
import java.util.List;

public class CocoaDialog extends Dialog implements CocoaDialogInterface {

    public List<EditText> editTextList;

    private TextView mPanelBorder;
    private LinearLayout mContentPanel;
    private LinearLayout mButtonPanel;
    private LinearLayout mHeaderPanel;

    private CharSequence mTitle;
    private CharSequence mMessage;
    private int mAnimStyleResId = 0;
    private CocoaDialogStyle mPreferredStyle;
    private List<CocoaDialogAction> mActionList;


    protected CocoaDialog(@NonNull Context context) {
        this(context, CocoaDialogStyle.alert);
    }

    protected CocoaDialog(@NonNull Context context, int themeResId) {
        this(context, themeResId, CocoaDialogStyle.alert);
    }

    protected CocoaDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        this(context, cancelable, CocoaDialogStyle.alert, cancelListener);
    }

    private CocoaDialog(@NonNull Context context, CocoaDialogStyle preferredStyle) {
        super(context);
        setPreferredStyle(preferredStyle);
    }

    private CocoaDialog(@NonNull Context context, int themeResId, CocoaDialogStyle preferredStyle) {
        super(context, themeResId);
        setPreferredStyle(preferredStyle);
    }

    private CocoaDialog(@NonNull Context context, boolean cancelable, CocoaDialogStyle preferredStyle, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
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
            if (mTitle == null && mMessage == null && editTextList.isEmpty()) {
                mHeaderPanel.setVisibility(View.GONE);
            }
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
        }else {
            l.width = WindowManager.LayoutParams.MATCH_PARENT;
        }
        l.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(l);
    }

    /**
     * Set the preferred style for the cocoa dialog.
     * @param preferredStyle Preferred style for the cocoa dialog.
     * @return CocoaDialog instance.
     */
    public CocoaDialog setPreferredStyle(CocoaDialogStyle preferredStyle) {
        this.mPreferredStyle = preferredStyle;
        return this;
    }

    /**
     * Set the animation style(include enter animation and exit animation) for the cocoa dialog,
     * only effective on a cocoa dialog with a style of CocoaDialogStyle.Alert.
     * @param animStyleResId Style resource id of the animation.
     * @return CocoaDialog instance.
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
        this.mTitle = getContext().getString(titleResId);
    }

    /**
     * Set message for the cocoa dialog.
     * @param title The title for the cocoa dialog.
     * @return CocoaDialog instance.
     */
    public CocoaDialog setTitleText(CharSequence title) {
        setTitle(title);
        return this;
    }

    /**
     * Set message for the cocoa dialog.
     * @param titleResId The title  resource id for the cocoa dialog.
     * @return CocoaDialog instance.
     */
    public CocoaDialog setTitleText(@StringRes int titleResId) {
        setTitle(titleResId);
        return this;
    }

    /**
     * Set message for the cocoa dialog.
     * @param message The message for the cocoa dialog.
     * @return CocoaDialog instance.
     */
    public CocoaDialog setMessage(CharSequence message) {
        this.mMessage = message;
        return this;
    }

    /**
     * Set message for the cocoa dialog.
     *
     * @param messageResId The message resource id for the cocoa dialog.
     * @return CocoaDialog instance.
     */
    public CocoaDialog setMessage(@StringRes int messageResId) {
        this.mMessage = getContext().getString(messageResId);
        return this;
    }

    /**
     * Add action to cocoa dialog.
     *
     * @param action CocoaDialogAction, appears as a button of the cocoa dialog.
     * @return Cocoa dialog instance.
     */
    public CocoaDialog addAction(CocoaDialogAction action) {
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
     * Add an EditText to the cocoa dialog, only effective on a cocoa dialog with a style of CocoaDialogStyle.Alert.
     * @param configurationHandler The handler to configure the edit text, such as text color, hint and default text.
     * @return CocoaDialog instance.
     */
    public CocoaDialog addEditText(EditTextConfigurationHandler configurationHandler) {
        CocoaDialogStyle style = this.mPreferredStyle;
        if (style != CocoaDialogStyle.alert) {
            throw new IllegalArgumentException("EditText can only be added to a cocoa dialog of style CocoaDialogStyle.Alert");
        }
        EditText editText = new EditText(getContext());
        editTextList.add(editText);
        if (configurationHandler != null) {
            configurationHandler.onEditTextAdded(editText);
        }
        return this;
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

    private void resolveActions() {
        if (mPreferredStyle == CocoaDialogStyle.alert) {
            boolean noHeader = mTitle == null && mMessage == null && editTextList.isEmpty();
            if (mActionList.isEmpty()) {
                mPanelBorder.setVisibility(View.GONE);
            } else if (noHeader || mActionList.size() > 2) {
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
                    boolean needBorder = true;
                    if (i == 0 && noHeader) {
                        mPanelBorder.setVisibility(View.GONE);
                        mButtonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                        if (mActionList.size() > 1) {
                            button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius);
                        }else {
                            needBorder = false;
                            button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                        }
                    } else if (i == mActionList.size() - 1) {
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius);
                        needBorder = false;
                    } else {
                        button.setBackgroundColor(Color.WHITE);
                    }
                    mButtonPanel.addView(button);
                    if (needBorder) {
                        TextView border = new TextView(getContext());
                        border.setBackgroundColor(0xFFC8C7CC);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
                        border.setLayoutParams(params);
                        mButtonPanel.addView(border);
                    }
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
                        if (i + 1 >= mActionList.size()) {
                            button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius);
                        } else {
                            button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_left_radius);
                        }
                    }
                    mButtonPanel.addView(button);
                }
            }

        } else {
            mPanelBorder.setVisibility(View.GONE);
            if (mActionList.isEmpty() || (mActionList.size() == 1 && mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel)) {
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
                if (((i == 0 && action.getStyle() != CocoaDialogActionStyle.cancel) || (i == 1 && mActionList.get(0).getStyle() == CocoaDialogActionStyle.cancel)) && mTitle == null && mMessage == null) {
                    mButtonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                    if (i + 1 < mActionList.size()) {
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius);
                    } else {
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                    }
                } else if (i + 1 >= mActionList.size()) {
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


    public static class Builder {

        Context mContext;
        CharSequence mTitle;
        CharSequence mMessage;
        int mAnimStyleRes = 0;
        List<EditText> mEditTextList;
        CocoaDialogStyle mPreferredStyle;
        List<CocoaDialogAction> mActionList;

        public Builder(Context context, CocoaDialogStyle preferredStyle) {
            this(context, null, null, preferredStyle);
        }


        public Builder(Context context, @StringRes int titleRes, @StringRes int messageRes, CocoaDialogStyle preferredStyle) {
            this(context, context.getString(titleRes), context.getString(messageRes), preferredStyle);
        }


        public Builder(Context context, CharSequence title, CharSequence message, CocoaDialogStyle preferredStyle) {
            mEditTextList = new ArrayList<>();
            mActionList = new ArrayList<>();
            mContext = context;
            mTitle = title;
            mMessage = message;
            mPreferredStyle = preferredStyle;
        }

        /**
         * Set the animation style(include enter animation and exit animation) for the cocoa dialog,
         * only effective on a cocoa dialog with a style of CocoaDialogStyle.Alert.
         * @param animStyleResId Style resource id of the animation.
         * @return CocoaDialog.Builder instance.
         */
        public Builder setAnimStyle(@StyleRes int animStyleResId) {
            mAnimStyleRes = animStyleResId;
            return this;
        }

        /**
         * Set title for the cocoa dialog.
         * @param title The title for the cocoa dialog.
         * @return CocoaDialog.Builder instance.
         */
        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Set title for the cocoa dialog.
         * @param titleResId The title resource id for the cocoa dialog.
         * @return CocoaDialog.Builder instance.
         */
        public Builder setTitle(@StringRes int titleResId) {
            this.mTitle = mContext.getString(titleResId);
            return this;
        }

        /**
         * Set message for the cocoa dialog.
         * @param message The message for the cocoa dialog.
         * @return CocoaDialog.Builder instance.
         */
        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }


        /**
         * Set message for the cocoa dialog.
         * @param messageResId The message resource id for the cocoa dialog.
         * @return CocoaDialog.Builder instance.
         */
        public Builder setMessage(@StringRes int messageResId) {
            this.mMessage = mContext.getString(messageResId);
            return this;
        }

        /**
         * Add action to cocoa dialog.
         * @param action CocoaDialogAction, appears as a button of the cocoa dialog.
         * @return CocoaDialog.Builder instance.
         */
        public Builder addAction(CocoaDialogAction action) {
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
         * Add an EditText to the cocoa dialog, only effective on a cocoa dialog with a style of CocoaDialogStyle.Alert.
         *
         * @param configurationHandler The handler to configure the edit text, such as text color, hint and default text.
         * @return CocoaDialog.Builder instance.
         */
        public Builder addEditText(EditTextConfigurationHandler configurationHandler) {
            Context context = mContext;
            CocoaDialogStyle style = mPreferredStyle;
            if (style != CocoaDialogStyle.alert) {
                throw new IllegalArgumentException("EditText can only be added to a cocoa dialog of style CocoaDialogStyle.Alert");
            }
            EditText editText = new EditText(context);
            mEditTextList.add(editText);
            if (configurationHandler != null) {
                configurationHandler.onEditTextAdded(editText);
            }
            return this;
        }

        /**
         * Create a cocoa dialog.
         * @return CocoaDialog instance.
         */
        public CocoaDialog create() {
            CocoaDialog dialog = new CocoaDialog(mContext, android.R.style.Theme_Dialog, mPreferredStyle);
            dialog.setTitle(mTitle);
            dialog.setMessage(mMessage);
            dialog.setAnimStyle(mAnimStyleRes);
            dialog.editTextList = mEditTextList;
            dialog.mActionList = mActionList;
            return dialog;
        }
    }

}
