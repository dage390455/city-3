package com.sensoro.smartcity.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.sensoro.smartcity.R;

public class CustomCornerDialog extends Dialog {
    public CustomCornerDialog(@NonNull Context context) {
        super(context);
    }

    public CustomCornerDialog(@NonNull Context context, int themeResId,View view) {
        this(context,themeResId,view,0.88f);
    }

    public CustomCornerDialog(@NonNull Context context,View view,float percentWidth) {
        this(context, R.style.CustomCornerDialogStyle,view,percentWidth);
    }

    public CustomCornerDialog(@NonNull Context context, int themeResId,View view,float percentWidth) {
        super(context, themeResId);
        setContentView(view);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int) (d.getWidth()*percentWidth);
        getWindow().setAttributes(p);
    }

    public void setView(View view) {
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();

    }
}
