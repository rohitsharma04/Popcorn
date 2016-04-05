package com.bitshifters.rohit.popcorn;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    private final static String URL = "https://in.linkedin.com/in/rohitsharmakolkata";

    @Bind(R.id.tvTitle) TextView title;
    @Bind(R.id.tvDeveloper) TextView developer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvDeveloper,R.id.tvTitle})
    public void onClick(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(browserIntent);
    }
}
