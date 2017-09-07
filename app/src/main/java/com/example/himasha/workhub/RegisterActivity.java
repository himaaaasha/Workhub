package com.example.himasha.workhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText fulname;
    private EditText email;
    private EditText telephone;
    private EditText password;
    private EditText confPassword;
    private Button registerBTN;
    private Button linktologin;

    private ProgressDialog progress;

    private FirebaseAuth auth;
    private DatabaseReference workhub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        workhub = FirebaseDatabase.getInstance().getReference().child("users");

        progress = new ProgressDialog(this);

        fulname = (EditText)findViewById(R.id.fulNameRegisterET);
        email = (EditText)findViewById(R.id.emailRegisterET);
        telephone = (EditText)findViewById(R.id.telephoneRegisterET);
        password = (EditText) findViewById(R.id.passwordRegisterET);
        confPassword = (EditText)findViewById(R.id.confPasswordRegisterET);
        registerBTN = (Button) findViewById(R.id.registerBTN);
        linktologin = (Button) findViewById(R.id.linktologinBTN);

        linktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = fulname.getText().toString().trim();
                final String userEmail = email.getText().toString().trim();
                final String userTelephone = telephone.getText().toString().trim();
                String userPass = password.getText().toString().trim();
                String userConfPass = confPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(userEmail) || !TextUtils.isEmpty(userPass) || !TextUtils.isEmpty(userConfPass) || !TextUtils.isEmpty(userName) || !TextUtils.isEmpty(userTelephone))
                {
                    if (validateEmail(userEmail)== true)
                    {
                        if (userConfPass.equals(userPass))
                        {
                            progress.setMessage("Signing Up, Please Wait!");
                            progress.show();

                            auth.createUserWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        String uid = auth.getCurrentUser().getUid();
                                        User newuser = new User();
                                        newuser.setUserEmail(userEmail);
                                        newuser.setUserName(userName);
                                        newuser.setUserTelephone(userTelephone);

                                        workhub.child(uid).setValue(newuser);

                                        progress.dismiss();
                                        Toast.makeText(RegisterActivity.this,"You have been successfuly registered, please login to continue!",Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        progress.dismiss();
                                        Toast.makeText(RegisterActivity.this,task.getException().getMessage()+" Try a different Email",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            progress.dismiss();
                            Toast.makeText(RegisterActivity.this,"Passwords you entered do not match, Please try again!",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,"Email Invalid, Please enter a valid Email!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"You cannot have one or more empty fields!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }
}
