package com.hoangt3k56.dropbox.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;
import com.hoangt3k56.dropbox.R;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    public static final String KEY="sv2abu0lwbaoed6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.startOAuth2Authentication(getApplicationContext(),KEY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = Auth.getOAuth2Token();
        Log.d("hoangdev", "tocken  " + token);
        if(token!=null)
        {
            Intent intent=new Intent(this, MainActivity.class);
            intent.putExtra("TOKEN",token);
            startActivity(intent);
        }
    }
}