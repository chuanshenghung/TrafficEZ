package com.sheng.gavin.scooterproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Personal_InformationActivity extends AppCompatActivity {
    //Element
    String GenderSlelect,Age,CarClass,CC;
    EditText PersonalActivityAge,PersonalActivityCarClass,PersonalActivityCC;
    Button PersonalActivitySubmitButton;

    //Firebase
    FirebaseUser user;
    FirebaseDatabase db;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal__information);

        //Element
        PersonalActivityAge = (EditText) findViewById(R.id.PersonActivityAge);
        PersonalActivityCarClass = (EditText) findViewById(R.id.PersonalActivityCarClass);
        PersonalActivityCC = (EditText) findViewById(R.id.PersonalActivityCC);
        PersonalActivitySubmitButton = (Button) findViewById(R.id.PersonalActivitySubmitButton);

        //Firebase
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();

        //GenderSpinner
        final Spinner Gender = (Spinner) findViewById(R.id.GenderSpinner);
        ArrayAdapter<CharSequence> nAdapter = ArrayAdapter.createFromResource(
                this, R.array.personalInformationActivity, android.R.layout.simple_spinner_item );
        nAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        Gender.setAdapter(nAdapter);

        //GenderSpinner Listener
        Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                GenderSlelect = Gender.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //PersonalActivitySubmitButton
        PersonalActivitySubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Age = PersonalActivityAge.getText().toString();
                CarClass = PersonalActivityCarClass.getText().toString();
                CC = PersonalActivityCC.getText().toString();
                if(!Age.matches("")&!CarClass.matches("")&!CC.matches("")){
                    DatabaseReference usersRef = db.getReference("Users");
                    usersRef.child(UID).child("個人資料").child("年齡").setValue(Age);
                    usersRef.child(UID).child("個人資料").child("車種").setValue(CarClass);
                    usersRef.child(UID).child("個人資料").child("性別").setValue(GenderSlelect);
                    usersRef.child(UID).child("個人資料").child("車輛CC數").setValue(CC);
                    Toast.makeText(Personal_InformationActivity.this,"提交完成",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Personal_InformationActivity.this,"請輸入完整",Toast.LENGTH_LONG).show();
                }
            }
        });

    }//onCreate
}
