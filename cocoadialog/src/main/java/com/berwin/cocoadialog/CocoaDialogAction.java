package com.berwin.cocoadialog;


import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

public final class CocoaDialogAction {

    private final String title;
    private final CocoaDialogActionStyle style;
    private final int color;
    private final OnClickListener listener;

    /**
     * An action for a {@link CocoaDialog}, appears as a button.
     *
     * @param title    The title of the action.
     * @param style    The {@link CocoaDialogActionStyle} of the action, {@link CocoaDialogActionStyle#cancel} always lay at the left or bottom of the actions, {@link CocoaDialogActionStyle#destructive}'s text would be red.
     * @param listener The click listener, when user click the button {@link CocoaDialogAction.OnClickListener#onClick(CocoaDialog)} would be called.
     */
    public CocoaDialogAction(String title, @NonNull CocoaDialogActionStyle style, OnClickListener listener) {
        this(title, style, style == CocoaDialogActionStyle.destructive ? Color.RED : 0xFF007AFF, listener);
    }

    /**
     * An action for a {@link CocoaDialog}, appears as a button.
     *
     * @param title    The title of the action.
     * @param color    The {@link Color} of the action's title.
     * @param listener The click listener, when user click the button {@link CocoaDialogAction.OnClickListener#onClick(CocoaDialog)} would be called.
     */
    public CocoaDialogAction(String title, @NonNull CocoaDialogActionStyle style, @ColorInt int color, OnClickListener listener) {
        this.title = title;
        this.style = style;
        this.listener = listener;
        this.color = color;
    }

    String getTitle() {
        return title;
    }

    CocoaDialogActionStyle getStyle() {
        return style;
    }

    @ColorInt
    int getColor() {
        return this.color;
    }

    OnClickListener getOnClickListener() {
        return listener;
    }

    public interface OnClickListener {
        void onClick(CocoaDialog dialog);
    }
}

