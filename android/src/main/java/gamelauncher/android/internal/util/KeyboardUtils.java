/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils implements ViewTreeObserver.OnGlobalLayoutListener {
    private final static int MAGIC_NUMBER = 200;
    private final View mRootView;
    private final float mScreenDensity;
    private SoftKeyboardToggleListener mCallback;
    private Boolean visible = null;

    public KeyboardUtils(Activity act, SoftKeyboardToggleListener listener) {
        mCallback = listener;

        mRootView = act.getWindow().getDecorView();
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mScreenDensity = act.getResources().getDisplayMetrics().density;
    }

    public static void forceShowKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) inputMethodManager.showSoftInput(view, 0);
    }

    public static void forceCloseKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean visible() {
        return visible != null && visible;
    }

    @Override public void onGlobalLayout() {
        Rect r = new Rect();
        mRootView.getWindowVisibleDisplayFrame(r);

        int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
        float dp = heightDiff / mScreenDensity;
        boolean isVisible = dp > MAGIC_NUMBER;

        if (visible == null || isVisible != visible) {
            visible = isVisible;
            if (mCallback != null) mCallback.onToggleSoftKeyboard(isVisible);
        }
    }

    public interface SoftKeyboardToggleListener {
        void onToggleSoftKeyboard(boolean isVisible);
    }
}
