package com.example.nluria.missionList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class newList extends AppCompatActivity {

    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    EditText title;
    Button btnAddList;
    Typeface buttonFont;
    TextView giveTitle;
    String myPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        myDb = new DataBaseHelper(this);
        fireDb = new FireBaseHelper();

        // get my phone number
        Intent intent = getIntent();
        myPhoneNumber= intent.getStringExtra("myPhoneNumber");


        //myPhoneNumber = "01234567";


/*
        TextView number = (TextView)findViewById(R.id.myPhoneNumText);
     //   TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
      //  myPhoneNumber = tMgr.getSimSerialNumber();
        number.setText("1:"+myPhoneNumber+":22");

*/

        fireDb.initialize(myPhoneNumber);

        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");


        title = (EditText)findViewById(R.id.add_a_title);
        title.setTypeface(buttonFont);
        giveTitle= (TextView)findViewById(R.id.giveTitle);
        giveTitle.setTypeface(buttonFont);
        btnAddList = (Button)findViewById(R.id.addNewListBtn);


        //methods.
        addNewList();


     }


    public void addNewList()
    {
         btnAddList.setOnClickListener(
                new View.OnClickListener()
                {

                    public void onClick(View v)
                    {

                        String str=title.getText().toString();

                        //empty title.
                        if (str.equals(""))
                        {
                            errorTitleInsertedAlertDialog("The title name can't be blank!");
                        }
                        //check firebase valid input.
                        else if (str.contains(".") ||str.contains("#") ||str.contains("$")
                                ||str.contains("[")||str.contains("]")  )
                        {
                            errorTitleInsertedAlertDialog("The title name should not contain '.', '#', '$', '[', or ']'");
                        }
                        else
                        {
                            fireDb.addNewList(str, newList.this,title);
                        }
                    }
                }
        );
    }


    public void errorTitleInsertedAlertDialog(String msg)
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(newList.this);
        alert_builder.setMessage("Please choose another name.")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle(msg);
        alert.show();
    }
}
