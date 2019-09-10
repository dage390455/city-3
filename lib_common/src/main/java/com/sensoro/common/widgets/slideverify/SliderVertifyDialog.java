package com.sensoro.common.widgets.slideverify;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.sensoro.common.R;


public class SliderVertifyDialog extends Dialog {
    private boolean mDismissKeyboard = false;

    public SliderVertifyDialog(@NonNull Context context) {
        super(context);
    }

    public SliderVertifyDialog(@NonNull Context context, int themeResId, View view) {
        this(context,themeResId,view,0.8f);
    }

    public SliderVertifyDialog(@NonNull Context context, View view, float percentWidth) {
        this(context, R.style.SliderVertifyDialogStyle,view,percentWidth);
    }

    /**
     * 改变dialog的宽度
     */
    public SliderVertifyDialog(@NonNull Context context, int themeResId, View view, float percentWidth) {
        super(context, themeResId);
        setContentView(view);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int) (d.getWidth()*percentWidth);
        getWindow().setAttributes(p);
    }

    /**
     * 不会设置dilog的总体宽度
     */
    public SliderVertifyDialog(@NonNull Context context, View view) {
        super(context, R.style.SliderVertifyDialogStyle);
        setContentView(view);

    }
    /**
     * 不会设置dilog的总体宽度
     */
    public SliderVertifyDialog(@NonNull Context context, View view, int theme) {
        super(context, theme);
        setContentView(view);

    }


    public SliderVertifyDialog(Activity activity, int customCornerDialogStyle, View view, boolean dismissKeyboard) {
        this(activity, customCornerDialogStyle, view);
        mDismissKeyboard = dismissKeyboard;
    }

    public void setView(View view) {
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        if (mDismissKeyboard) {
            View currentFocus = getCurrentFocus();
            if (currentFocus instanceof EditText) {
                InputMethodManager imm = (InputMethodManager) currentFocus.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);;
                if (imm != null && imm.isActive(currentFocus)) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        }
        super.dismiss();
    }
}
