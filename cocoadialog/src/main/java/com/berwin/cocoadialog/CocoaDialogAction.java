package com.berwin.cocoadialog;


public class CocoaDialogAction {

    private String title;
    private CocoaDialogActionStyle style;
    private OnClickListener listener;

    public CocoaDialogAction(String title, CocoaDialogActionStyle style, OnClickListener listener) {
        this.title = title;
        this.style = style;
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStyle(CocoaDialogActionStyle style) {
        this.style = style;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public String getTitle() {
        return title;
    }

    public CocoaDialogActionStyle getStyle() {
        return style;
    }

    public OnClickListener getOnClickListener() {
        return listener;
    }

    public interface OnClickListener<T extends CocoaDialogInterface> {
        void onClick(T dialog);
    }
}

