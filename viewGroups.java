package com.example.nluria.missionList;


import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class viewGroups extends AppCompatActivity
{
    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    private DatabaseReference mRootRef, mRootRefUsers;

    private static ListView listView;
    private ArrayList<String> groupsArray = new ArrayList<String>();
    private ArrayList<String> groupsArrayFullName = new ArrayList<String>();


    private static Button delete_groups_button;
    Typeface buttonFont;
    String myPhoneNumber;
    private final static int PICK_CONTACT = 1;
    private String nameOfGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);
        myDb = new DataBaseHelper(this);
        fireDb= new FireBaseHelper();

        Intent intent = getIntent();
        myPhoneNumber= intent.getStringExtra("myPhoneNumber");
        fireDb.initialize(myPhoneNumber);

        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");




        //execute methods.
        optionBar();
        getGroups();
        groupMenu();
        //deleteAllGroupsClickListener();

    }

    //display options menu bar.
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_view_groups, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void optionBar()
    {
        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.logo);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("2Do");
        ab.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

    }

    //the menu bar itself.
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.refresh:
                refreshActivity();
             case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "setting icon is selected", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getGroups()
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");

        listView = (ListView)findViewById(R.id.groupsListView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, groupsArray );

        listView.setAdapter(adapter);

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s)
            {
                final String groupToCheck=dataSnapshot.getKey();

                DatabaseReference mRootRefTemp = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users/" + myPhoneNumber+"/");
                mRootRefTemp.addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshotSon, String s)
                    {
                        //show me only groups that i belong to.
                        if (dataSnapshotSon.child(groupToCheck).getValue() != null)
                        {
                            groupsArrayFullName.add(dataSnapshotSon.child(groupToCheck).getKey().toString());
                            for (DataSnapshot child: dataSnapshot.getChildren())
                            {
                                if (child.getKey().toString().equals("nameOfGroup"))
                                {
                                    String value = child.getValue().toString();
                                    System.out.println("the child: "+value);
                                    groupsArray.add(value);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        else
                        {
                            System.out.println(groupToCheck+" not exists!");
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    /*
    public ArrayList<String> getGroupsOfOneUser()
    {
        int flag=-1;
        final ArrayList<String> groupsOfOneUser = new ArrayList<String>();

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users/"+myPhoneNumber);

        listView = (ListView)findViewById(R.id.groupsListView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, groupsArray );

        listView.setAdapter(adapter);


        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                //get only groups that i am member of.
                for (DataSnapshot child: dataSnapshot.getChildren())
                {


                    if (child.getKey().toString().equals("nameOfGroup"))
                    {
                        String value = child.getValue().toString();
                        System.out.println("getValue: "+child.getValue());
                        groupsArray.add(value);
                        adapter.notifyDataSetChanged();
                    }

                    String key = child.getKey().toString();
                    System.out.println("getKey: "+child.getKey());
                    groupsOfOneUser.add(key);
                }
                int flag=0;
                System.out.println("Done! " );

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        System.out.println("return! " );

        return groupsOfOneUser;
    }
*/

    /*
    public void getGroups()
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");

        listView = (ListView)findViewById(R.id.groupsListView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, groupsArray );

        listView.setAdapter(adapter);

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    if (child.getKey().toString().equals("nameOfGroup"))
                    {
                        String value = child.getValue().toString();
                        System.out.println("getValue: "+child.getValue());
                        groupsArray.add(value);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
*/

    //functions for getting contact information.
//1.
    public  void callContacts(View v, String group)
    {
        nameOfGroup=group;
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }



    //2.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_CONTACT)
        {
            if (resultCode== ActionBarActivity.RESULT_OK)
            {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null,null,null);

                if(c.moveToFirst())
                {
                    String friendName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                        String friendNumber = getContactNumber(friendName);
                        fireDb.addUserToGroup(friendNumber, friendName,nameOfGroup, viewGroups.this);

                    }
                }
            }
        }
    }

    //3.Find contact based on name.
    private String getContactNumber(String name)
    {
        String ret=null;
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME = '" + name + "'", null, null);
        if (cursor.moveToFirst())
        {
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext())
            {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                ret=number;
            }
            phones.close();
        }
        cursor.close();

        return ret;
    }


    public void groupMenu()
    {
        listView = (ListView)findViewById(R.id.groupsListView);

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
                                             //   {"Watch tasks", "Watch members", "Add a friend to list", "Delete Group","Cancel"},
                                                {"צפה במטלות", "צפה באנשי הקבוצה", "הוסף חבר לקבוצה", "מחק קבוצה", "צא מהקבוצה","ביטול"},
                                        new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialogInterface, int which)
                                            {


                                                String grp = (String)listView.getItemAtPosition(p);
                                                //
                                                System.out.println("p is: "+groupsArrayFullName.get(p));

                                                //

                                                // The 'which' argument contains the index position
                                                // of the selected item
                                                switch (which)
                                                {
                                                    case 0:
                                                        Intent intent = new Intent("com.example.nluria.missionList.tasks2");
                                                        //pass the name of the group to the next activity.
                                                        intent.putExtra("nameOfGroup", grp);
                                                        intent.putExtra("nameOfGroupFullName", groupsArrayFullName.get(p));
                                                        intent.putExtra("myPhoneNumber", myPhoneNumber);
                                                        intent.putExtra("enter", "false");
                                                        dialogInterface.cancel();
                                                        startActivity(intent);
                                                        break;
                                                    case 1:
                                                        intent = new Intent("com.example.nluria.missionList.members");
                                                        //pass the name of the group to the next activity.
                                                        intent.putExtra("nameOfGroup", grp);
                                                        intent.putExtra("myPhoneNumber", myPhoneNumber);
                                                        intent.putExtra("nameOfGroupFullName", groupsArrayFullName.get(p));
                                                        dialogInterface.cancel();
                                                        startActivity(intent);
                                                        break;
                                                    case 2:

                                                        callContacts(null, grp);

                                           //             finish();
                                           //             refreshActivity();
                                                        break;
                                                    case 3:
                                                        deleteGroup(grp);
                                                        Toast.makeText( viewGroups.this, "הרשימה נמחקה בהצלחה!", Toast.LENGTH_LONG).show();
                                                        finish();
                                                        refreshActivity();
                                                        break;
                                                    case 4:
                                                    {
                                                        leaveGroup(grp);
                                                        break;
                                                    }
                                                    case 5:
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



    public void deleteGroup(String group)
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com");
        Query query = mRootRef.child("groups").orderByChild("nameOfGroup").equalTo(group);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    appleSnapshot.getRef().removeValue();
                    // finish();
                    //refreshActivity();

                }
                //refreshActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        removeUserFromUsersList( group, myPhoneNumber);
        finish();
        refreshActivity();
     }


    public void leaveGroup(String group)
    {
         //to implement.
    }


    public void removeUserFromUsersList(final String g, final String userNumber)
    {
        final String groupToDelete=userNumber+"_"+g;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users/");

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        for (DataSnapshot grandsun: child.getChildren())
                        {
                            if (grandsun.getKey().equals(groupToDelete))
                            {
                                grandsun.getRef().removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
     }



/*
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
                                fireDb.deleteAllGroups(viewGroups.this);
                                finish();
                            }
                        });
                AlertDialog alert = alert_builder.create();
                alert.setTitle("Delete all?");
                alert.show();
            }
        });
    }
*/

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


    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
    }

}