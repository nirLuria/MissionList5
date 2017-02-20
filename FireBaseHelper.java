package com.example.nluria.missionlist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by nluria on 2/9/2017.
 */





public class FireBaseHelper extends AppCompatActivity
{
    private DatabaseReference  mRootRef;
    private static ListView mListView;
    private List<String> groupsArray = new ArrayList<String>();



    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    public void insertNewList(String title, final Context context, final EditText editText)
    {
        final boolean[] enteredToMethod = new boolean[1];
        enteredToMethod[0]=false;

        //for now - only me create new lists.
        final String group = "0546443430_" + title;
        final String myPhoneNum="0546443430";
        final String t=title;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //check if the title of the group is already exists.
                if (dataSnapshot.child(group).getValue() != null)
                {
                    if (enteredToMethod[0]==false)
                    {
                        System.out.println(group + " is already exist");
                        enteredToMethod[0]=true;
                        titleNameExistsAlertDialog(context);
                        editText.setText("");
                    }
                }
                //write the data.
                else
                {
                    //members.
                    mRootRef=mRootRef.child(group);
                    Map<String, String> userData = new HashMap<String, String>();
                    userData.put(myPhoneNum, "1");
                    mRootRef = mRootRef.child("members");
                    mRootRef.setValue(userData);

                    //name
                    mRootRef=FirebaseDatabase.getInstance()
                            .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group+"");
                    mRootRef=mRootRef.child("name");
                    mRootRef.setValue(t);


                    enteredToMethod[0]=true;
                    newGroupEnteredSuccessfullyAlertDialog(context);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    public void getGroups(final Context context, ArrayAdapter<String> adapter)
    {
        final ArrayAdapter<String> a=adapter;
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int count=(int)dataSnapshot.getChildrenCount();
                count--;
                System.out.println("Size "+count);
                if (count==0)
                {
                    //no data.
                    showMessage("Mmmmm... ", "There is no any group yet.", context);
                    //  finish();
                    return;
                }
                else
                {
                    setContentView(R.layout.activity_view_groups);
                    mListView = (ListView)findViewById(R.id.listView);
                    mListView.setAdapter(a);
                    String value = dataSnapshot.getValue(String.class);
                    groupsArray.add(value);
                    a.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void newGroupEnteredSuccessfullyAlertDialog(Context context)
    {
        Toast.makeText(context, "New list inserted successfully", Toast.LENGTH_LONG).show();
        ((Activity)context).finish();
    }

    public void titleNameExistsAlertDialog(Context context)
    {
        //print error message to screen.
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
        alert_builder.setMessage("Please choose another name.")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("This title name is already exists!");
        alert.show();
    }


    //show the data in an openned windows.
    //print to screen the groups.
    public void showMessage(String title, String message,Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
}







