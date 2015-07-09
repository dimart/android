package com.dimart.myappportfolio;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    public void showMessage(View view) {
        Button btn = (Button) view;
        CharSequence msg = String.format(getString(R.string.btn_clicked), btn.getText());
        mToast.setText(msg);
        mToast.show();
    }
}
