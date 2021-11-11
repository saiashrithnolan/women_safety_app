package com.example.women_safety_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Livelocation extends AppCompatActivity
{

    Button bt,bt2;
    TextView tv1,tv2,tv3,tv4,tv5;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livelocation);

        bt=findViewById(R.id.bt_location);


        tv1=findViewById(R.id.text_view1);
        tv2=findViewById(R.id.text_view2);
        tv3=findViewById(R.id.text_view3);
        tv4=findViewById(R.id.text_view4);
        tv5=findViewById(R.id.text_view5);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                //check permission
                if (ActivityCompat.checkSelfPermission(Livelocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // when pemission grantied
                    getLocation();
                } else {
                    // when denied
                    ActivityCompat.requestPermissions(Livelocation.this
                            , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
    }

    private void getLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task)
            {
                // initiatlize location

                Location location = task.getResult();
                if (location != null)
                {

                    try
                    {
                        // initalize geocoder
                        Geocoder geocoder = new Geocoder(Livelocation.this, Locale.getDefault());
                        //initilize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);

                        // set latitude on text view
                        tv1.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Latitude :</b><br></font>"
                                        + addresses.get(0).getLatitude()));

                        //set longitude
                        tv2.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Longitude :</b><br></font>"
                                        + addresses.get(0).getLongitude()));

                        // set  country name
                        tv3.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Country Name :</b><br></font>"
                                        + addresses.get(0).getCountryName()));

                        //set Locality
                        tv4.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Locality :</b><br></font>"
                                        + addresses.get(0).getLocality()));

                        // set address
                        tv5.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Address :</b><br></font>"
                                        + addresses.get(0).getAddressLine(0)));

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}