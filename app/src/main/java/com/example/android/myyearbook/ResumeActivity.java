package com.example.android.myyearbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ResumeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        String html = "<html><body><center><h1>Testing</h1></center></body></html>";

        ((WebView) findViewById(R.id.webview)).loadData(html, "text/html", null);
    }
}
