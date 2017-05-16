package com.example.nluria.missionList;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import android.widget.ScrollView;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class tasks extends AppCompatActivity
{
    DataBaseHelper myDb;
    String nameOfGroup;
    Button btnAddTask;
    EditText input;
    private static Button delete_tasks_button;
    FireBaseHelper fireDb;
    private DatabaseReference mRootRef;
    private static ListView listView;
 //   private ArrayList<String> tasksArray = new ArrayList<String>();
    private ArrayList<String> tasksArray;
    private String myPhoneNumber;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        myDb = new DataBaseHelper(this);
        fireDb= new FireBaseHelper();

        // get my phone number
     //   TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
     //   myPhoneNumber = tMgr.getSimSerialNumber();



        Intent intent = getIntent();
        myPhoneNumber= intent.getStringExtra("myPhoneNumber");
        fireDb.initialize(myPhoneNumber);

        System.out.println("myPhoneNumber is       ___"+ myPhoneNumber);
        System.out.println("nirnir");

        //print title of group on screen.
        nameOfGroup= intent.getStringExtra("nameOfGroup");
        TextView title= (TextView) findViewById(R.id.title);
        title.setText(nameOfGroup);
    //    title.setTextSize(100);


        //
   //     TextView number = (TextView)findViewById(R.id.myPhoneNumText2);

     //   number.setText("1:"+myPhoneNumber+":22");

        //

        addNewTask();
        getTasks();
        deleteTasksOfGroupClickListener();
        taskMenu();

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
                //empty title.
                if (input.getText().toString().equals(""))
                {
                    emptyTaskInsertedAlertDialog();
                }
                else
                {
             //       fireDb.addNewTask(input.getText().toString(),nameOfGroup,nameOfGroupFullName, tasks2.this, input, true);
                    input.setText("");



                }
              /*
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
                */
            }
        });


        final AlertDialog alertDialog= builder.create();

        buttonClickAction(alertDialog);

    }

    public void buttonClickAction(final AlertDialog a)
    {
        AlertDialog alertDialog=a;
        btnAddTask= (Button)findViewById(R.id.Add_btn);
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.show();
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


    public void getTasks()
    {
        String group=myPhoneNumber+"_"+nameOfGroup;
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);


        listView = (ListView)findViewById(R.id.tasksListView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, tasksArray );

        listView.setAdapter(adapter);

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.getKey().equals("tasks"))
                {
                    System.out.println("dataSnapshot: "+dataSnapshot);
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        String value = child.getKey().toString();
                        tasksArray.add(value);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void taskMenu()
    {
        listView = (ListView)findViewById(R.id.tasksListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, tasksArray );
        listView.setAdapter(adapter);

        //go to tasks of group.
        listView.setOnItemClickListener
                (
                        new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                            {
                                final int p=position;
                                AlertDialog.Builder builder = new AlertDialog.Builder(tasks.this);

                                builder.setTitle("");
                                builder.setItems(new CharSequence[]
                                                {"Delete Task","Cancel"},
                                        new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialogInterface, int which)
                                            {
                                                String task = (String)listView.getItemAtPosition(p);
                                                // The 'which' argument contains the index position
                                                // of the selected item
                                                switch (which)
                                                {

                                                    case 0:
                                                        deleteTask(task, nameOfGroup);
                                                        finish();
                                                        refreshActivity();
                                                        break;
                                                    case 1:
                                                        break;


                                                }
                                            }
                                        });
                                builder.create().show();


                                AlertDialog alert = builder.create();
                                alert.setTitle("Menu");
                                alert.show();
                                alert.dismiss();
                            }
                        }
                );
    }


    public void deleteTask(String task, String nameOfGroup)
    {

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+myPhoneNumber+"_"+nameOfGroup);
        Query query = mRootRef.child("tasks").orderByKey().equalTo(task);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });




    }
/*
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
/*

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
*/

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


                        //delete all tasks in group.
                        fireDb.deleteAllTasksOfGroup(tasks.this, nameOfGroup);
                        /*
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
                        */
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



    public void emptyTaskInsertedAlertDialog()
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(tasks.this);
        alert_builder.setMessage("Please choose another task.")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                     }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("The task can't be blank!");
        alert.show();
    }


}


