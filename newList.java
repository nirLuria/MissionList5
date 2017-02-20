package com.example.nluria.missionlist;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;

public class newList extends AppCompatActivity {

    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    EditText title;
    Button btnAddList;
    Typeface buttonFont;
    TextView giveTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        myDb = new DataBaseHelper(this);
        fireDb = new FireBaseHelper();
        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");


        title = (EditText)findViewById(R.id.add_a_title);
        title.setTypeface(buttonFont);
        giveTitle= (TextView)findViewById(R.id.giveTitle);
        giveTitle.setTypeface(buttonFont);
        btnAddList = (Button)findViewById(R.id.addNewListBtn);
        addNewList();
    }


    public void addNewList()
    {
        btnAddList.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        //empty title.
                        if (title.getText().toString().equals(""))
                        {
                            emptyTitleInsertedAlertDialog();
                        }
                        else
                        {

                            fireDb.insertNewList(title.getText().toString(), newList.this,title);



                            /*  SQL code


                            //boolean isInserted = myDb.insertNewList(title.getText().toString());

                            //isInserted=fireDb.getIExecuted();


                            isInserted=fireDb.getIExecuted();

                            if (isInserted == 1)
                            {
                                newGroupEnteredSuccessfullyAlertDialog();
                            }
                            else if (isInserted == 2)
                            {
                                emptyTitleInsertedAlertDialog();
                            }
                            */
                        }
                    }
                }
        );
    }


    public void emptyTitleInsertedAlertDialog()
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
        alert.setTitle("The title name can't be blank!");
        alert.show();
    }
}
