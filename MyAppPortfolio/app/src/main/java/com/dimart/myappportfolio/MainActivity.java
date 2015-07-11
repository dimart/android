package com.dimart.myappportfolio;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public void showMessage(View view) {
        Button btn = (Button) view;
        CharSequence msg = String.format(getString(R.string.btn_clicked), btn.getText());
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
}
