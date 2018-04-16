package com.berwin.cocoadialog;


import android.support.annotation.NonNull;

public final class CocoaDialogAction {

    private final String title;
    private final CocoaDialogActionStyle style;
    private final OnClickListener listener;

    /**
     * An action for a {@link CocoaDialog}, appears as a button.
     *
     * @param title The title of the action.
     * @param style The {@link CocoaDialogActionStyle} of the action, {@link CocoaDialogActionStyle#cancel} always lay at the left or bottom of the actions, {@link CocoaDialogActionStyle#destructive}'s text would be red.
     * @param listener The click listener, when user click the button {@link CocoaDialogAction.OnClickListener#onClick(CocoaDialog)} would be called.
     */
    public CocoaDialogAction(String title, @NonNull CocoaDialogActionStyle style, OnClickListener listener) {
        this.title = title;
        this.style = style;
        this.listener = listener;
    }

    String getTitle() {
        return title;
    }

    CocoaDialogActionStyle getStyle() {
        return style;
    }

    OnClickListener getOnClickListener() {
        return listener;
    }

    public interface OnClickListener {
        void onClick(CocoaDialog dialog);
    }
}

