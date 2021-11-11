package com.example.women_safety_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactActivity extends AppCompatActivity
{

    Button savecontact;
    EditText name;
    EditText phone;

    FirebaseDatabase rootnode;
    DatabaseReference name_ref,number_ref;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //hooks from the layout_contact
        name = findViewById(R.id.name_input);
        phone = findViewById(R.id.phoneNoInput);
        savecontact=findViewById(R.id.saveContactBtn);

        // saving the name and number on clicking the button

        savecontact.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {




                rootnode=FirebaseDatabase.getInstance();

                number_ref=rootnode.getReference("phone_numbers");

                String names = name.getText().toString();
                String numbers = phone.getText().toString();



                contact_helper helper2 = new contact_helper(numbers);

                 number_ref.child(numbers).setValue(helper2);
                Toast.makeText(getApplicationContext(),"You added this contact to the database",Toast.LENGTH_SHORT).show();
            }
        });
    }
}