package com.lguipeng.notes.mvp.views.impl;

import android.support.annotation.StringRes;

import com.lguipeng.notes.model.UNote;
import com.lguipeng.notes.mvp.views.View;

/**
 * Created by lgp on 2015/9/4.
 */
public interface NoteView extends View {
    void finishView();
    void setToolbarTitle(String title);
    void setToolbarTitle(@StringRes int title);
    void initViewOnEditMode(UNote note);
    void initViewOnViewMode(UNote note);
    void initViewOnCreateMode(UNote note);
    void setOperateTimeLineTextView(String text);
    void setDoneMenuItemVisible(boolean visible);
    boolean isDoneMenuItemVisible();
    boolean isDoneMenuItemNull();
    String getLabelText();
    String getContentText();
    void hideKeyBoard();
    void showKeyBoard();
    void showNotSaveNoteDialog();
}
