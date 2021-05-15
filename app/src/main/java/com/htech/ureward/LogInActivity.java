package com.htech.ureward;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.htech.ureward.helpers.UHelper;
import com.htech.ureward.model.Userss;
import com.htech.ureward.utils.PreferencesManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LogInActivity extends AppCompatActivity{
    @BindView(R.id.textView3)
    TextView sign;
    @BindView(R.id.textView2)
    TextView signTitle;
    @BindView(R.id.textView)
    TextView logTitle;
    @BindView(R.id.imageButton)
    ImageButton btnLogIn;
    @BindView(R.id.motionLay)
    MotionLayout motionLayout;
    @BindView(R.id.btnVerify)
    ImageButton btnVerify;
    @BindView(R.id.editText)
    TextInputLayout edtPhone;
    @BindView(R.id.editText3)
    TextInputLayout edtName;
    @BindView(R.id.editText2)
    TextInputLayout edtPass;
    @BindView(R.id.editText4)
    TextInputLayout edtConPass;

    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    int isLogIn=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        if (PreferencesManager.getInstance().getIsReg(getApplicationContext())){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
            return;
        }

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MyTag","OnClickCalled");
                if (isLogIn==1) {
                    ((MotionLayout)view.getParent()).transitionToEnd();
                    isLogIn=2;
                    signTitle.setText("Already have an account");
                    logTitle.setText("Create Account");
                    sign.setText("Sign In");
                    btnLogIn.setImageResource(R.drawable.btnsignup);
                }
                else if (isLogIn==3){

                }
                else {
                    ((MotionLayout)view.getParent()).transitionToStart();
                    isLogIn=1;
                    signTitle.setText("Don't have an account");
                    logTitle.setText("Log in");
                    sign.setText("Sign Up");
                    btnLogIn.setImageResource(R.drawable.btnlogin);
                }
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MyTag","OnClickCalled");
                String mail=edtPhone.getEditText().getText().toString();
                String pass=edtPass.getEditText().getText().toString();
                String name=edtName.getEditText().getText().toString();
                if (isLogIn==2){
                    if (isValidCredential()){
                       if (isPasswordValid()){
                           UHelper.showDialog(LogInActivity.this,"Sending Otp");
                           PreferencesManager.getInstance().setMail(getApplicationContext(),mail);
                           PreferencesManager.getInstance().setPass(getApplicationContext(),pass);
                           PreferencesManager.getInstance().setName(getApplicationContext(),name);
                           sendVerificationMessage(mail,pass);
                       }
                       else {
                           return;
                       }
                    }
                    else {
                        return;
                    }
                }
                else if (isLogIn==1){
                    UHelper.showDialog(LogInActivity.this,"Loggin In");
                    mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                UHelper.hideDialog();
                                if (mAuth.getCurrentUser().isEmailVerified()){
                                    PreferencesManager.getInstance().setMail(getApplicationContext(),mail);
                                    PreferencesManager.getInstance().setIsReg(getApplicationContext(),true);
                                    startActivity(new Intent(LogInActivity.this,MainActivity.class));
                                }
                                else {
                                    UHelper.showToast(getApplicationContext(),"Verify you email");
                                }
                            }
                            else {
                                UHelper.hideDialog();
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    UHelper.showToast(getApplicationContext(),"Password is incorrect");
                                }
                                else {
                                    UHelper.showToast(getApplicationContext(),"Email not in use");
                                }
                            }
                        }
                    });
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UHelper.showDialog(LogInActivity.this,"Logging in");
                String mail=PreferencesManager.getInstance().getMail(getApplicationContext());
                String pass=PreferencesManager.getInstance().getPass(getApplicationContext());
                mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            UHelper.hideDialog();
                            if (mAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Please verify your email",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            UHelper.hideDialog();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void sendVerificationMessage(String mail, String pass) {
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                           String name=PreferencesManager.getInstance().getName(getApplicationContext());
                                Userss users=new Userss(name,mail,null);
                                firebaseFirestore.collection("Users").document(mail).set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        UHelper.hideDialog();
                                        motionLayout.setTransition(R.id.otpTrans);
                                        motionLayout.transitionToState(R.id.endotptrans);
                                        motionLayout.setState(R.id.endotptrans, ConstraintSet.WRAP_CONTENT,ConstraintSet.WRAP_CONTENT);
                                        signTitle.setVisibility(View.GONE);
                                        sign.setVisibility(View.GONE);
                                    }
                                });
                            }
                            else {
                                UHelper.hideDialog();
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    UHelper.hideDialog();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"Email already in use",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isPasswordValid() {
        String pass=edtPass.getEditText().getText().toString();
        String conPass=edtConPass.getEditText().getText().toString();

        if (!pass.equals(conPass)){
            edtConPass.setError("Passwords should match");
            return false;
        }
        else {
            edtConPass.setError(null);
            return true;
        }
    }

    private boolean isValidCredential() {
        String email = edtPhone.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            edtPhone.setError("Email is required: Can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtPhone.setError("Invalid Email Address");
            return false;
        } else if (edtPass.getEditText().getText().length() < 8){
            edtPass.setError("Password should contain 8 letters minimum");
            return false;
        }
        else {
            edtPhone.setError(null);
            edtPass.setError(null);
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}