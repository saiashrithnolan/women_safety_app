package com.example.women_safety_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.telephony.SmsManager;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
{

    ImageButton call,location,video,contact,report,feedback;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);



        location = findViewById(R.id.locationBtn);
        video = findViewById(R.id.videoBtn);
        contact = findViewById(R.id.contactBtn);
        feedback = findViewById(R.id.feedbackBtn);


        contact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this,ContactActivity.class));
            }
        });


        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://youtube.com/playlist?list=PLA86B58B7DA1FF904");
            }
        });

        feedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, Feedbackactivity.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Livelocation.class));
            }
        });

    }

    private void gotoUrl(String s)
    {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

 // it worked properly ..all we need to do is to press the volume key down

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        super.onKeyLongPress(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.w("LightWriter", "I WORK BRO.");
            return true;
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);



            FusedLocationProviderClient  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            // i am going to extract latitude and longitude of myself as soon as i click the volume down button
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                // when pemission grantied

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {

                    Toast.makeText(getApplicationContext(),"the permission to find the location is not enabled",Toast.LENGTH_SHORT).show();
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task)
                    {


                        Location location = task.getResult();
                        if (location != null)
                        {

                            try
                            {

                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                //initilize address list
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1);


                                double lat = addresses.get(0).getLatitude();

                                //set longitude
                                double lon = addresses.get(0).getLongitude();

                                // set  country name
                                String country = addresses.get(0).getCountryName();

                                //set Locality
                                String locality = addresses.get(0).getLocality();

                                // set address
                                String address = addresses.get(0).getAddressLine(0);


                                String latitude_coordinate = Double.toString(lat);
                                String longitude_coordinate = Double.toString(lon);
                                String loc = "Latitude".concat(latitude_coordinate).concat(" ").concat("Longitude").concat(longitude_coordinate).concat(" ").concat(locality).concat(" ").concat(address);


                                List<String> phonenumbers = new ArrayList<>();


                                DatabaseReference number_ref = FirebaseDatabase.getInstance().getReference("phone_numbers");

                                number_ref.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        phonenumbers.clear();
                                        for (DataSnapshot postSnapshot : snapshot.getChildren())
                                        {
                                            String num = postSnapshot.child("number").getValue(String.class);
                                            phonenumbers.add(num);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                        Toast.makeText(getApplicationContext(), "the database is not proper with this adapter", Toast.LENGTH_SHORT).show();
                                    }
                                });



                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                {
                                    if(checkSelfPermission(Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED)
                                    {
                                        sendSMS(loc,phonenumbers);
                                    }
                                    else
                                    {
                                         requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                                    }
                                }

                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }



            else

                {

                // when denied
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }


            return true;
        }
        return true;
    }

    private void sendSMS(String locate,List<String> phonenumbers)
    {
        try {

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("+919902324770", null, locate, null, null);

                Toast.makeText(getApplicationContext(), "Message Sent successfully",
                        Toast.LENGTH_LONG).show();

        }
        catch(Exception e)
        {

            Toast.makeText(getApplicationContext(), "Message Sending failure",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }
}