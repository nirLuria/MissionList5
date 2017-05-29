package com.example.nluria.missionList;

import android.*;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nluria.mRecycler.MyAdapter;
import com.example.nluria.notification.SecondClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AbsRuntimePermissions
{
    private static DataBaseHelper myDb;
    private static Button exit_button;
    private static Button view_groups_button;
    private static Button view_swipe_button;
    private static Button new_list_button;
    private static Button contact_btn;
    private DatabaseReference mRootRef;

    Typeface buttonFont;
    Typeface alertDialogFont;
    private static int REQUEST_PERMISSION = 10;
    private EditText inputUserName;
    private EditText inputCode;
    private static ListView listView;
    private ArrayList<String> groupsArray = new ArrayList<String>();

    private SharedPreferences mPreferences;
    private String myPhoneNumber;
    Button sendSMS;
    private final static int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        optionBar();

        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");
        alertDialogFont= Typeface.createFromAsset(getAssets(), "dragon-webfont.ttf");

        myDb = new DataBaseHelper(this);

        //ask for permissions.
        requestAppPermissions(new String[]
        {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_CONTACTS,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,

        }, R.string.msg, REQUEST_PERMISSION);

        //authentication.
        mPreferences = getSharedPreferences("User", MODE_PRIVATE);


        //execute methods.
        exitButtonClickListener();
        newGroupClickListener();
        viewGroupsClickListener();
        getGroups();

       //logOut();
        authenticationManager();
    }

    public void showNotification(View v)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("Nir's notification");
        builder.setContentText("This is my great not...");
        Intent intent = new Intent(this, SecondClass.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SecondClass.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0,builder.build());
    }

    //display options menu bar.
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_main_activity, menu);
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
            case R.id.info_id:
                Intent intent = new Intent("com.example.nluria.missionList.Info");
                startActivity(intent);
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "setting icon is selected", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //functions for getting contact information.
    //1.
    public  void callContacts(View v)
    {
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
                        Toast.makeText(this, "my name is: "+friendName+ ", my number is: " + friendNumber, Toast.LENGTH_LONG).show();

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


    //function for fixing a bug in retrieving data in the first click.
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
                System.out.println("dataSnapshot.getKey: "+groupToCheck);

                ///
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
                            System.out.println(groupToCheck+" exists!");
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

                ///


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


    public void newGroupClickListener()
    {
        new_list_button= (Button)findViewById(R.id.New_ListButton);
        new_list_button.setTypeface(buttonFont);
        new_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.nluria.missionList.newList");
                intent.putExtra("myPhoneNumber", myPhoneNumber);
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
                        Intent intent = new Intent("com.example.nluria.missionList.viewGroups");
                        intent.putExtra("myPhoneNumber", myPhoneNumber);
                        startActivity(intent);


                    }
                }
        );
    }


    @Override
    public void onPermissionsGranted(int requestCode)
    {
        Toast.makeText(getApplicationContext(), "זיהוי משתמש עבר בהצלחה!", Toast.LENGTH_LONG).show();
    }


    //authentication
    public void authenticationManager()
    {
        if (mPreferences.contains("username"))
        {
            // start Main activity
            myPhoneNumber=mPreferences.getString("username","");
            System.out.print("myPhoneNumber is "+myPhoneNumber);
        }
        else
        {
            // ask him to enter his credentials
            authentication();
        }
    }


    public void authentication()
    {
        //print error message to screen.
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        inputUserName = new EditText(this);
        alert_builder.setView(inputUserName);
        alert_builder.setMessage("(זהו תהליך חד פעמי)")
       // alert_builder.setMessage("This is only one time process.")
                .setCancelable(false)
                .setPositiveButton("לא תודה, אנסה בפעם אחרת", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        finish();
                    }
                })
                .setNegativeButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("inputUserName is "+inputUserName.getText().toString());
                        SharedPreferences.Editor editor = mPreferences.edit();

                        editor.putString("username", inputUserName.getText().toString());
                        editor.commit();
                        myPhoneNumber=inputUserName.getText().toString();
                        if (myPhoneNumber.matches("[0-9]+") && myPhoneNumber.length() ==10)
                        {
                            dialogInterface.cancel();
                            int genNum=generateNumber();
                            sendSms(myPhoneNumber, genNum);
                            checkSmsCode(genNum);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"illegal number", Toast.LENGTH_LONG).show();
                            authentication();
                        }

                    }
                });
        AlertDialog alert = alert_builder.create();
        //alert.setTitle("For first use, please enter your phone number");
        alert.setTitle("עבור שימוש ראשוני באפליקציה, בבקשה הזן את מס' הפלאפון שלך:");
        alert.show();
    }


    public void checkSmsCode(final int genNum)
    {
        //print error message to screen.
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        inputCode = new EditText(this);
        alert_builder.setView(inputCode);
        alert_builder.setMessage("בבקשה הזן את הקוד שקיבלת בהודעת טקסט:")
                //alert_builder.setMessage("Please enter below the code you received:")
                .setCancelable(false)
                .setPositiveButton("לא תודה, אנסה בפעם אחרת", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        finish();
                    }
                })
                .setNegativeButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("inputCode is "+inputCode.getText().toString());
                        String strCode=""+genNum;

                        if (inputCode.getText().toString().equals(strCode))
                        {
                            Toast.makeText(getApplicationContext(),"הרישום לאפליקציה עבר בהצלחה!", Toast.LENGTH_LONG).show();
                            dialogInterface.cancel();
                            refreshActivity();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"קוד שגוי, אנא נסה שנית", Toast.LENGTH_LONG).show();
                            checkSmsCode(genNum);
                        }

                    }
                });
        AlertDialog alert = alert_builder.create();
       // alert.setTitle("Code Authentication");
        alert.setTitle("אימות קוד");
        alert.show();
    }


    public void sendSms(final String number, int genNum)
    {
        //String message = "Hello, please use the following code for access to my great app: "+genNum;
        String message = "שלום! בבקשה השתמש/י בקוד הבא בכדי להתחיל ולהשתמש באפליקציה: "+genNum;

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, message, null, null);
        Toast.makeText(getApplicationContext(),"הודעת טקסט נשלחה בהצלחה ל: " +number, Toast.LENGTH_LONG).show();
    }


    public int generateNumber()
    {
        int min = 1000;
        int max = 9999;

        Random r = new Random();
        int generatedNum = r.nextInt(max - min + 1) + min;
        //int generatedNum=1111;
        System.out.println("generatedNum: "+ generatedNum);

        return generatedNum;
    }


    public void logOut()
    {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();   // This will delete all your preferences, check how to delete just one
        editor.commit();
    }


    public void refreshActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void onBackPressed()
    {
        //there is no way back from the main activity.
    }
}
