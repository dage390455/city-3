package com.sensoro.smartcity.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CustomCornerDialog extends Dialog {
    public CustomCornerDialog(@NonNull Context context) {
        super(context);
    }

    public CustomCornerDialog(@NonNull Context context, int themeResId,View view) {
        super(context, themeResId);
        setContentView(view);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int) (d.getWidth()*0.88);
        getWindow().setAttributes(p);
    }

    public CustomCornerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    public void setView(View view) {
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();

    }
}
