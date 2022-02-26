package com.example.biometricauth;

import static androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    private BiometricPrompt biometricPrompt=null;
    private Executor executor = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btn_login);

        if(biometricPrompt==null){
            biometricPrompt=new BiometricPrompt(this,executor,callback);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndAuthenticate();
            }
        });
    }

    private BiometricPrompt.PromptInfo buildBiometricPrompt()
    {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setSubtitle("FingerPrint Authentication")
                .setDescription("Please place your finger on the sensor to unlock")
                .setDeviceCredentialAllowed(true)
                .build();

    }

    private void checkAndAuthenticate(){
        BiometricManager biometricManager=BiometricManager.from(this);
        switch (biometricManager.canAuthenticate())
        {
            case BiometricManager.BIOMETRIC_SUCCESS:
                BiometricPrompt.PromptInfo promptInfo = buildBiometricPrompt();
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                snack("Biometric Authentication currently unavailable");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                snack("Your device doesn't support Biometric Authentication");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                snack("Your device doesn't have any fingerprint enrolled");
                break;
        }
    }

    private BiometricPrompt.AuthenticationCallback callback=new
            BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    if(errorCode==ERROR_NEGATIVE_BUTTON && biometricPrompt!=null)
                        biometricPrompt.cancelAuthentication();
                    snack((String) errString);
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    snack("Authenticated");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(MainActivity.this,SecondActivity.class));
                }

                @Override
                public void onAuthenticationFailed() {
                    snack("The FingerPrint was not recognized.Please Try Again!");
                }
            };

    private void snack(String text)
    {
        View view=findViewById(R.id.view);
        Snackbar snackbar=Snackbar.make(view,text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}