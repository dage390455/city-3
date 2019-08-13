package com.sensoro.smartcity.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sensoro.imagepicker.R;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.yanzhenjie.permission.runtime.Permission;

public class MainActivity extends AppCompatActivity {
    private final String[] requestPermissions = {Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.WRITE_CONTACTS, Permission.READ_CONTACTS, Permission.CAMERA, Permission.RECORD_AUDIO, Permission.CALL_PHONE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_imagepicker);

        findViewById(R.id.btn_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.getInstance().setSelectLimit(9 );
                final Intent intent = new Intent(getApplicationContext(), ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                getApplicationContext().startActivity(intent);
            }
        });

    }



}
