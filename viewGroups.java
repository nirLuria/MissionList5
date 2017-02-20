package com.example.nluria.missionlist;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class viewGroups extends AppCompatActivity
{
    private static ListView listView;
    private ArrayList<String> groupsArray = new ArrayList<String>();
    private static Button view_groups_button;
    private static Button delete_groups_button;
    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    Typeface buttonFont;
    private DatabaseReference mRootRef;
    private  ListView mGroupsList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);
        myDb = new DataBaseHelper(this);
        fireDb= new FireBaseHelper();

        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");

        listView = (ListView)findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, groupsArray );
        listView.setAdapter(adapter);

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {

                int count=0;
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    if (child.getKey().toString().equals("name"))
                    {
                        String value = child.getValue().toString();
                        System.out.println("getValue: "+child.getValue());
                        groupsArray.add(value);
                        adapter.notifyDataSetChanged();
                        ++count;
                    }
                }

                /*
                if (count==0)
                {
                    //no data.
                    System.out.println("no childs");
                    showMessage("Mmmmm... ", "There is no any group yet.");
                    //  finish();
                    return;
                }
                */


                /*
                String value = dataSnapshot.getValue(String.class);
                 groupsArray.add(value);
                adapter.notifyDataSetChanged();
                */
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



        //execute methods.
        groupsView();
        deleteAllGroupsClickListener();
    }


    public void groupsView()
    {

        /*
        fireDb.getGroups();
        Cursor res = myDb.getGroups();
        if (res.getCount()==0)
        {
            //no data.
            showMessage("Mmmmm... ", "There is no any group yet.");
          //  finish();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        int number=1;

        //  ###print to screen the database data.        ###
        while (res.moveToNext())
        {
            buffer.append(number+". " + res.getString(1) + "\n");
            System.out.println(res.getString(1));
            groupsArray.add(res.getString(1));
            number++;
        }
        //  ###                                         ###

*/
        listView = (ListView)findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, groupsArray );
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(viewGroups.this);

                        builder.setTitle("");
                        builder.setItems(new CharSequence[]
                                        {"Watch tasks", "Delete Group","Cancel"},
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialogInterface, int which)
                                    {
                                        String grp = (String)listView.getItemAtPosition(p);
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        switch (which) {
                                            case 0:
                                                Intent intent = new Intent("com.example.nluria.missionlist.tasks");

                                                //pass the name of the group to the next activity.
                                                intent.putExtra("name", grp);
                                                dialogInterface.cancel();
                                                startActivity(intent);
                                                break;
                                            case 1:
                                                boolean isDeleted = myDb.deleteOneGroup(grp);
                                                if (isDeleted = true)
                                                    Toast.makeText(viewGroups.this, "Group "+grp+" was deleted successfully", Toast.LENGTH_LONG).show();
                                                else
                                                    Toast.makeText(viewGroups.this, "error when deleting", Toast.LENGTH_LONG).show();
                                                refreshActivity();
                                                break;
                                            case 2:
                                            {
                                                break;
                                            }
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

///


    //delete all groups.
    public void deleteAllGroupsClickListener()
    {

        delete_groups_button = (Button)findViewById(R.id.Delete_all_groupsBtn);
        delete_groups_button.setTypeface(buttonFont);
        delete_groups_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(viewGroups.this);
                alert_builder.setMessage("Do you realy want to delete all groups?")
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
                                boolean isDeleted = myDb.deleteAllGroups();
                                if (isDeleted = true)
                                    Toast.makeText(viewGroups.this, "All groups were deleted successfully", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(viewGroups.this, "error when deleting", Toast.LENGTH_LONG).show();

                                finish();
                            }
                        });
                AlertDialog alert = alert_builder.create();
                alert.setTitle("Delete all?");
                alert.show();
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


    public void refreshActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


}