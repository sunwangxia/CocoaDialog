package com.berwin.cocoadialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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

public class CocoaDialog extends DialogFragment {

    public interface EditTextConfigurationHandler {
        void onEditTextAdded(EditText editText);
    }

    private static final String ARGUMENT_TITLE = "title";
    private static final String ARGUMENT_MESSAGE = "message";
    private static final String ARGUMENT_STYLE = "style";
    private static final String ARGUMENT_ANIMATION_STYLE = "animation_style";

    public List<EditText> editTextList = new ArrayList<>();

    private CocoaDialogStyle preferredStyle;
    private int animStyleRes = 0;
    private List<CocoaDialogAction> actionList = new ArrayList<>();
    private CharSequence title;
    private CharSequence message;
    private LinearLayout contentPanel;
    private LinearLayout buttonPanel;
    private TextView panelBorder;
    private LinearLayout headerPanel;

    @SuppressLint("ValidFragment")
    private CocoaDialog() {
    }

    /**
     * Create a cocoa dialog instance with the preferred style.
     *
     * @param preferredStyle The preferred syle of the cocoa dialog.
     * @return CocoaDialog Instance.
     */
    public static CocoaDialog build(CocoaDialogStyle preferredStyle) {
        CocoaDialog dialog = new CocoaDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_STYLE, preferredStyle);
        dialog.setArguments(bundle);
        return dialog;
    }

    /**
     * Create a cocoa dialog instance with the title, message and preferred style.
     *
     * @param title          The title of the cocoa dialog.
     * @param message        The message of the cocoa dialog.
     * @param preferredStyle The preferred syle of the cocoa dialog.
     * @return CocoaDialog Instance.
     */
    public static CocoaDialog build(CharSequence title, CharSequence message, CocoaDialogStyle preferredStyle) {
        CocoaDialog dialog = new CocoaDialog();
        Bundle bundle = new Bundle();
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
        title = bundle.getCharSequence(ARGUMENT_TITLE);
        message = bundle.getCharSequence(ARGUMENT_MESSAGE);
        preferredStyle = (CocoaDialogStyle) bundle.getSerializable(ARGUMENT_STYLE);
        animStyleRes = bundle.getInt(ARGUMENT_ANIMATION_STYLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        assert window != null;
        WindowManager.LayoutParams l = window.getAttributes();
        l.width = WindowManager.LayoutParams.MATCH_PARENT;
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
        if (preferredStyle == CocoaDialogStyle.alert) {
            contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_alert, null, false);
            if (animStyleRes <= 0) {
                mWindow.setWindowAnimations(android.R.style.Animation_Dialog);
            } else {
                mWindow.setWindowAnimations(animStyleRes);
            }
            headerPanel = (LinearLayout) contentView.findViewById(com.berwin.cocoadialog.R.id.headPanel);
            if (title == null && message == null && editTextList.isEmpty() && actionList.size() > 2) {
                headerPanel.setVisibility(View.GONE);
            }
            for (EditText editText : editTextList) {
                editText.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_edit_text_background);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = DensityUtil.dip2px(getContext(), 8);
                editText.setLayoutParams(params);
                int padding = DensityUtil.dip2px(getContext(), 4);
                editText.setPadding(padding, padding, padding, padding);
                editText.setTextSize(14);
                editText.setLines(1);
                editText.setTypeface(Typeface.DEFAULT);
                headerPanel.addView(editText);
            }
        } else {
            contentView = LayoutInflater.from(getContext()).inflate(com.berwin.cocoadialog.R.layout.cocoa_dialog_action_sheet, null, false);
            mWindow.setWindowAnimations(com.berwin.cocoadialog.R.style.Animation_CocoaDialog_ActionSheet);
            mWindow.setGravity(Gravity.BOTTOM);
            headerPanel = (LinearLayout) contentView.findViewById(com.berwin.cocoadialog.R.id.headPanel);
            if (title == null && message == null) {
                headerPanel.setVisibility(View.GONE);
            }

        }
        TextView titleText = (TextView) contentView.findViewById(com.berwin.cocoadialog.R.id.title);
        TextView messageText = (TextView) contentView.findViewById(com.berwin.cocoadialog.R.id.message);
        if (title != null) {
            titleText.setText(title);
        } else {
            titleText.setVisibility(View.GONE);
        }
        messageText.setText(message);
        contentPanel = (LinearLayout) contentView.findViewById(com.berwin.cocoadialog.R.id.contentPanel);
        panelBorder = (TextView) contentView.findViewById(com.berwin.cocoadialog.R.id.panelBorder);
        buttonPanel = (LinearLayout) contentView.findViewById(com.berwin.cocoadialog.R.id.buttonPanel);
        resolveActions();
        return contentView;
    }


    /**
     * Set the animation style(include enter animation and exit animation) for the cocoa dialog,
     * only effective on a cocoa dialog with a style of CocoaDialogStyle.Alert.
     *
     * @param animStyleRes Resource ID of the animation.
     * @return CocoaDialog Instance.
     */
    public CocoaDialog setAnimStyleRes(int animStyleRes) {
        Bundle bundle = getArguments();
        bundle.putInt(ARGUMENT_ANIMATION_STYLE, animStyleRes);
        return this;
    }

    /**
     * Set title for the cocoa dialog to show.
     *
     * @param title The title for the cocoa dialog to show.
     * @return CocoaDialog Instance
     */
    public CocoaDialog setTitle(CharSequence title) {
        Bundle bundle = getArguments();
        bundle.putCharSequence(ARGUMENT_TITLE, title);
        return this;
    }

    /**
     * Set message for the cocoa dialog to show.
     *
     * @param message The message for the cocoa dialog to show.
     * @return CocoaDialog Instance
     */
    public CocoaDialog setMessage(CharSequence message) {
        Bundle bundle = getArguments();
        bundle.putCharSequence(ARGUMENT_MESSAGE, message);
        return this;
    }

    /**
     * Add action to cocoa dialog.
     *
     * @param action CocoaDialogAction, appears as a button of the cocoa dialog.
     * @return CocoaDialog Instance
     */
    public CocoaDialog addAction(CocoaDialogAction action) {
        if (action.getStyle() == CocoaDialogActionStyle.cancel) {
            if (!actionList.isEmpty() && actionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) {
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
     * Add an EditText to the cocoa dialog, only effective on a cocoa dialog with a style of CocoaDialogStyle.Alert.
     *
     * @param configurationHandler The handler to configure the edit text, e.g. text color, hint, default text.
     * @return CocoaDialog Instance.
     */
    public CocoaDialog addEditText(Context context, EditTextConfigurationHandler configurationHandler) {
        CocoaDialogStyle style = (CocoaDialogStyle) getArguments().getSerializable(ARGUMENT_STYLE);
        if (style == CocoaDialogStyle.actionSheet) {
            throw new IllegalArgumentException("EditText can only be added to a cocoa dialog of style CocoaDialogStyle.Alert");
        }
        EditText editText = new EditText(context);
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
        if (preferredStyle == CocoaDialogStyle.alert) {
            if (actionList.isEmpty()) {
                panelBorder.setVisibility(View.GONE);
            } else if (actionList.size() > 2) {// 拥有3个以上Action时，每个Action Button占用整行空间
                panelBorder.setVisibility(View.VISIBLE);
                buttonPanel.setOrientation(LinearLayout.VERTICAL);
                if (actionList.get(0).getStyle() == CocoaDialogActionStyle.cancel) { // 调整取消按钮放到最下方
                    CocoaDialogAction cancelAction = actionList.remove(0);
                    actionList.add(cancelAction);
                }
                for (int i = 0; i < actionList.size(); i++) {
                    final CocoaDialogAction action = actionList.get(i);
                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 50));
                    Button button = buildActionButton(action, buttonParams);
                    boolean needBorder = true;
                    if (i == 0 && title == null && message == null && editTextList.isEmpty()) {
                        panelBorder.setVisibility(View.GONE);
                        buttonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius);
                    } else if (i == (actionList.size() - 1)) {
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius);
                        needBorder = false;
                    } else {
                        button.setBackgroundColor(Color.WHITE);
                    }
                    buttonPanel.addView(button);
                    if (needBorder) {
                        TextView border = new TextView(getContext());
                        border.setBackgroundColor(0xFFC8C7CC);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
                        border.setLayoutParams(params);
                        buttonPanel.addView(border);
                    }
                }
            } else {
                panelBorder.setVisibility(View.VISIBLE);
                buttonPanel.setOrientation(LinearLayout.HORIZONTAL);
                for (int i = 0; i < actionList.size(); i++) {
                    final CocoaDialogAction action = actionList.get(i);
                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, DensityUtil.dip2px(getContext(), 45));
                    buttonParams.weight = 1;
                    Button button = buildActionButton(action, buttonParams);
                    if (buttonPanel.getChildCount() > 0) {
                        // 添加按钮分隔线
                        TextView border = new TextView(getContext());
                        border.setBackgroundColor(0xFFC8C7CC);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(), 1), ViewGroup.LayoutParams.MATCH_PARENT);
                        border.setLayoutParams(params);
                        buttonPanel.addView(border);
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_right_radius);
                    } else {
                        if (i + 1 >= actionList.size()) {
                            button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius);
                        } else {
                            button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_left_radius);
                        }
                    }
                    buttonPanel.addView(button);
                }
            }

        } else {
            panelBorder.setVisibility(View.GONE);
            if (actionList.isEmpty() || (actionList.size() == 1 && actionList.get(0).getStyle() == CocoaDialogActionStyle.cancel)) {
                headerPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
            } else {
                headerPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius);
            }
            for (int i = 0; i < actionList.size(); i++) {
                CocoaDialogAction action = actionList.get(i);
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 50));
                Button button = buildActionButton(action, buttonParams);
                if (action.getStyle() == CocoaDialogActionStyle.cancel) {
                    buttonParams.topMargin = DensityUtil.dip2px(getContext(), 10);
                    button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                    contentPanel.addView(button);
                    continue;
                }
                // 按钮分隔线
                TextView border = new TextView(getContext());
                border.setBackgroundColor(0xFFC8C7CC);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
                border.setLayoutParams(params);
                if (((i == 0 && action.getStyle() != CocoaDialogActionStyle.cancel) || (i == 1 && actionList.get(0).getStyle() == CocoaDialogActionStyle.cancel)) && title == null && message == null) {
                    buttonPanel.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                    if (i + 1 < actionList.size()) {
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_top_radius);
                    } else {
                        button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_corner_radius);
                    }
                } else if (i + 1 >= actionList.size()) {
                    buttonPanel.addView(border);
                    button.setBackgroundResource(com.berwin.cocoadialog.R.drawable.cocoa_dialog_bottom_radius);
                } else {
                    buttonPanel.addView(border);
                    button.setBackgroundColor(Color.WHITE);
                }
                buttonPanel.addView(button);
            }
        }
    }
}

