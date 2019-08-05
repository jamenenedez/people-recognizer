package com.lauszus.facerecognitionapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MoreInfoActivity extends AppCompatActivity {

    TextView myText;
    String person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        myText = (TextView) findViewById(R.id.mainText);
        myText.setMovementMethod(new ScrollingMovementMethod());
        Button btn = (Button) findViewById(R.id.btn);
        Intent intent = getIntent();
        person = intent.getStringExtra("com.lauszus.facerecognitionapp.SEARCH_TEXT");
        new DoIt().execute();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class DoIt extends AsyncTask<Void, Void, Void> {
        String words;
        String data;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                data = person.replaceAll(" ", "+");
                Document base = Jsoup.connect("https://www.google.com.mx/search?client=opera&q=\"" + data + "\"&sourceid=opera&ie=UTF-8&oe=UTF-8").userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36 OPR/62.0.3331.72").get();
                words = "";
                Elements baseElements = base.getElementsByClass("r");
                int countELements = base.getElementsByClass("r").size();

                if (countELements > 0) {
                    for (Element baseElement : baseElements.select("a")
                    ) {
                        String[] a = baseElement.text().split("\"^(www|http:\\/\\/)(.*\\.(com$|net$|org$))\"");
                        words += "<h2>" + a[0] + "</h2><br>";
                        break;
                    }
                }

                Document extra = Jsoup.connect("https://www.google.com/search?q=\"" + data + "\"&safe=active&client=opera&source=lnms&tbm=nws").userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36 OPR/62.0.3331.72").get();
                Elements e = extra.getAllElements();

                int total = extra.getElementsByClass("l").size();
                int end = 3;
                int count = 0;
                int foundData = 0;
                if (total > 0) {
                    words += "<ul>\n";
                    for (Element element : e.select("div.st")) {
                        int b = element.text().indexOf(person);
                        System.out.println(b);
                        if (b != -1) {
                            foundData++;
                        }
                    }
                    for (Element element : e.select("a.l")) {
                        if (count < end && foundData > 0) {
                            words += "<li>" + element.text() + "(" + element.attr("href") + ")</li>\n";
                        } else {
                            break;
                        }
                        count++;
                        foundData--;
                    }
                    words += "</ul>\n";
                } else {
                    words = "No records found";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myText.setText("Loading...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                myText.setText(Html.fromHtml(words, Html.FROM_HTML_MODE_COMPACT));
            } else {
                myText.setText(Html.fromHtml(words));
            }
            person = "";
        }
    }
}
