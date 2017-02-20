package com.example.nluria.missionlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

 import com.google.firebase.database.*;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    private static DataBaseHelper myDb;
    private static Button exit_button;
    private static Button view_groups_button;
    private static Button new_list_button;
    Typeface buttonFont;
    Typeface alertDialogFont;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = getSupportActionBar();        //
        ab.setLogo(R.drawable.logo);                 //
        ab.setDisplayUseLogoEnabled(true);           //     <- option bar.
        ab.setDisplayHomeAsUpEnabled(true);          //
        ab.setDisplayShowHomeEnabled(true);          //
        ab.setTitle("ToDo");
        ab.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));


        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");
        alertDialogFont= Typeface.createFromAsset(getAssets(), "dragon-webfont.ttf");

        //execute methods.
        exitButtonClickListener();
        newGroupClickListener();
        viewGroupsClickListener();

        myDb = new DataBaseHelper(this);
    }


    //display options menu bar.
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //the menu bar itself.
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.info_id:
                Toast.makeText(getApplicationContext(), "info icon is selected", Toast.LENGTH_SHORT).show();
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "setting icon is selected", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //exit.
    public void exitButtonClickListener()
    {

        exit_button = (Button)findViewById(R.id.exitBotton);
        exit_button.setTypeface(buttonFont);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                alert_builder.setMessage(R.string.Do_you_want_to_exit)
                        .setCancelable(false)
                        .setNegativeButton(" ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(" ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                AlertDialog alert = alert_builder.create();
                alert.setTitle(getString(R.string.exit));
                alert.show();
                Button negButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                negButton.setBackgroundResource(R.drawable.x);
                Button posButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                posButton.setBackgroundResource(R.drawable.v);
            }
        });
    }


    //print to screen the groups.
    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
     }

    public void newGroupClickListener()
    {
        new_list_button= (Button)findViewById(R.id.New_ListButton);
        new_list_button.setTypeface(buttonFont);
        new_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.nluria.missionlist.newList");
                startActivity(intent);
            }
        });
    }



    public void viewGroupsClickListener()
    {
        view_groups_button = (Button)findViewById(R.id.newViewGroupsButton);
        view_groups_button.setTypeface(buttonFont);
        view_groups_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent("com.example.nluria.missionlist.viewGroups");
                        startActivity(intent);
                    }
                }
        );
    }

}
