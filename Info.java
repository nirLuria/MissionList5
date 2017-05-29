package com.example.nluria.missionList;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by nluria on 5/21/2017.
 */

public class Info extends AppCompatActivity
{

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        textView = (TextView)findViewById(R.id.info);
        String str="כל הזכויות שמורות לניר לוריא.\n" +
                "אם יש לכם הצעות לשיפור, אנא צרו עמי קשר:\n" +
                "2nirluria@gmail.com";
        textView.setText(str);
    }

}
