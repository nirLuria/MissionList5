package com.example.nluria.missionlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.widget.ScrollView;
import android.widget.LinearLayout;



public class tasks extends AppCompatActivity
{
    DataBaseHelper myDb;
    String nameOfGroup;
    Button btnAddTask;
    EditText input;
    List<String> tasksArray = new ArrayList<String>();
    private static Button delete_tasks_button;



    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        myDb = new DataBaseHelper(this);


        //print title of group on screen.
        Intent intent = getIntent();
        nameOfGroup= intent.getStringExtra("name");
        TextView title= (TextView) findViewById(R.id.title);
        title.setText(nameOfGroup);
    //    title.setTextSize(100);

        addNewTask();
        tasksView();
     //   deleteTasksOfGroupClickListener();

     }


    public void deleteTasksOfGroupClickListener()
    {
        delete_tasks_button = (Button)findViewById(R.id.delete_tasks_btn);
        delete_tasks_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteAllTasksMessage();
            }
        });
    }


    public void addNewTask()
    {
        //create alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new task:");
        builder.setMessage("");
        input= new EditText(this);
        builder.setView(input);

        //set negative button.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //set positive button.
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                boolean isInserted = myDb.insertNewTask(input.getText().toString(),nameOfGroup);
                System.out.println(" isInserted is: " + isInserted);

                if (isInserted == true)
                {
                    Toast.makeText(tasks.this, "New task inserted successfully", Toast.LENGTH_LONG).show();
                    refreshActivity();
                }
                else
                {
                    Toast.makeText(tasks.this, "Baddddddd", Toast.LENGTH_LONG).show();

                }
                input.setText("");
            }
        });


        final AlertDialog alertDialog= builder.create();


        btnAddTask= (Button)findViewById(R.id.Add_btn);
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });
    }


    //show the data in an openned windows.
    //print to screen the groups.
    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    public void scrollView()
    {

        System.out.println(" scrollView!");


        //create scroll view.
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        TextView tv = new TextView(this);
        ll.addView(tv);

        for(int i = 0; i < 20; i++) {
            Button cb = new Button(this);
            cb.setText("I'm dynamic!");
            ll.addView(cb);
        }
        this.setContentView(sv);

    }


    public void tasksView()
    {
        Cursor res = myDb.getTasks(nameOfGroup);
        if (res.getCount()==0)
        {
            System.out.println(" no tasks");

            return;
        }
        else
        {
            System.out.println(" i have tasks!");
         ///   //scrollView();

/*
            //create scroll view.
            ScrollView sv = new ScrollView(this);
            LinearLayout home_linear = new LinearLayout(this);

            LinearLayout ll = new LinearLayout(this);

          //  ll.setLayoutParams(new ViewGroup.LayoutParams(180, 180));
           // ll.addView(home_linear, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            ll.setOrientation(LinearLayout.VERTICAL);
            sv.addView(ll);
            TextView tv = new TextView(this);
            ll.addView(tv);
*/


            StringBuffer buffer = new StringBuffer();
            int number=1;

            TableLayout table = (TableLayout)findViewById(R.id.table_for_buttons);


            //  ###print to screen the database data.        ###
            while (res.moveToNext())
            {
                final String str=res.getString(1);
                buffer.append(number+". " + str + "\n");
                System.out.println(str);
                tasksArray.add(str);
                number++;


                //create button for every task.
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                table.addView(tableRow);


                Button button = new Button(this);
            //    button.setText("I'm dynamic!");
            //    ll.addView(button);


               // Button button= new Button(this);
                button.setText(res.getString(1));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        deleteTaskMessage(str);
                    }


                });
                tableRow.addView(button);
             }
     //       this.setContentView(sv);
        }
    }


    public void deleteAllTasksMessage()
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(tasks.this);
        alert_builder.setMessage("Do you realy want to delete all tasks?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete all groups.
                        boolean isDeleted = myDb.deleteAllTasksOfGroup(nameOfGroup);
                        if (isDeleted == true)
                        {
                            Toast.makeText(tasks.this, "Tasks of " + nameOfGroup + " have been deleted successfully", Toast.LENGTH_LONG).show();
                            refreshActivity();
                        }
                        else
                        {
                            Toast.makeText(tasks.this, "didn't deleted", Toast.LENGTH_LONG).show();
                        }
                        //  finish();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("Delete all?");
        alert.show();

    }


    public void deleteTaskMessage(String str)
    {
        System.out.println(str);
        final String s=str;
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(tasks.this);
        alert_builder.setMessage("Do you realy want to delete " + str)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        boolean isDeleted = myDb.deleteOneTask(nameOfGroup, s);
                        if (isDeleted == true)
                        {
                            System.out.println(s + "was deleted");
                            refreshActivity();
                        }
                        else
                        {
                            System.out.println(s + "was not deleted");
                        }
                        //   finish();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("");
        alert.show();
    }


    public void refreshActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
