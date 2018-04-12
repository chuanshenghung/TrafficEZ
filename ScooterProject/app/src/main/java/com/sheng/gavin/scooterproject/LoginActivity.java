package com.sheng.gavin.scooterproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    //Element
    Button LoginActivityLoginButton,LoginActivitySignUpButton;

    //Firebase
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;

    //GoogleSignIn
    private GoogleApiClient mGoogleApiClient;
    private SignInButton LoginActivityGoogleSignInButton;
    int RC_SIGN_IN=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        //Element

        LoginActivityLoginButton = (Button)findViewById(R.id.LoginButton);
        LoginActivitySignUpButton = (Button)findViewById(R.id.SignUpButton);

        //Firebase authStateListener
        auth=FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent MainActivity = new Intent(LoginActivity.this, com.sheng.gavin.scooterproject.MainActivity.class);
                    startActivity(MainActivity);
                    LoginActivity.this.finish();
                    Log.d("onAuthStateChanged","登入"+user.getUid());
                }else{
                    Log.d("onAuthStateChanged","已登出");
                }

            }
        };

        //Firebase Login
        LoginActivityLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String LoginActivityAddress = ((EditText)findViewById(R.id.Address)).getText().toString();
                String LoginActivityPassword =((EditText)findViewById(R.id.password)).getText().toString();
                    auth=FirebaseAuth.getInstance();
                if(LoginActivityAddress.matches("")||LoginActivityPassword.matches("")) {
                    Toast.makeText(LoginActivity.this,"請輸入帳號密碼",Toast.LENGTH_LONG).show();
                }else{
                    auth.signInWithEmailAndPassword(LoginActivityAddress,LoginActivityPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent MainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(MainActivity);
                                //LoginActivity.this.finish();
                                Toast.makeText(LoginActivity.this,"ok",Toast.LENGTH_LONG).show();
                                Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                            }else {
                                Toast.makeText(LoginActivity.this,"oss",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }


                }


        });

        //Firebase SignUp
        LoginActivitySignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(SignUpActivity);
                //LoginActivity.this.finish();
            }
        });

        //Firebase Google LogIn

        LoginActivityGoogleSignInButton = (SignInButton)findViewById(R.id.GoogleLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Yes Got an Error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        LoginActivityGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }//onCreate

    //Google Login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    //Google Login
    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

}
