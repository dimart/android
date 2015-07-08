package com.dimart.myappportfolio;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
    }

    public void showMessage(View view) {
//        mToast.cancel();
        Button btn = (Button) view;
        CharSequence msg = String.format(getString(R.string.btn_clicked), btn.getText());
        mToast.setText(msg);
        mToast.show();
    }
}
