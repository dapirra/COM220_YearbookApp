package com.example.android.myyearbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ResumeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        String html = "<html><head><style>@keyframes test {\n" +
                "from {transform: rotateY(0deg);}\n" +
                "to {transform: rotateY(360deg);}\n" +
                "}</style></head>" +
                "<body><marquee>Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text </marquee>" +
                "<br><center><br><br><br>" +
                "<h1 style=\"animation: test 2s infinite linear; text-align: center; font-size: 40px;\">Hire me!</h1><br><br>" +
                "<marquee direction=\"down\" width=\"260\" height=\"200\" behavior=\"alternate\" style=\"border:solid\">\n" +
                "  <marquee behavior=\"alternate\">\n" +
                "    Best resume right here\n" +
                "  </marquee>\n" +
                "</marquee></center></body></html>";

        ((WebView) findViewById(R.id.webview)).loadData(html, "text/html", null);
    }
}
