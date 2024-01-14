package com.example.moneywise.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.moneywise.R;

public class ContactUsActivity extends AppCompatActivity {

    RelativeLayout phone_column,email_column,address_column,whatsapp_column;
    ImageButton back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        back_btn = findViewById(R.id.btn_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), HnSActivity.class);
                startActivity(intent);
                finish();
            }
        });

        phone_column=findViewById(R.id.phone_column);
        email_column=findViewById(R.id.email_column);
        address_column=findViewById(R.id.address_column);
        whatsapp_column=findViewById(R.id.whatsapp_column);

        phone_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:+60123456789"));
                startActivity(phoneIntent);
            }
        });

        email_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent=new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                String[] recipientEmails = {"mwadmin@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipientEmails);
                try {
                    startActivity(Intent.createChooser(emailIntent,"Choose an Email Client"));
                }
                catch (Exception e){
                    Toast.makeText(ContactUsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        address_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("https://maps.google.com/maps/place/Faculty+of+Computer+Science+and+Information+Technology+(FCSIT)/@3.1282134,101.6506948,15z/data=!4m6!3m5!1s0x31cc49720ec81b9b:0x58d63e7d8749e9d8!8m2!3d3.1282134!4d101.6506948!16s%2Fg%2F11r8lvmtm?entry=ttu");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        whatsapp_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent whatsappIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://wa.me/601161418828"));
                    startActivity(whatsappIntent);
            }
        });
    }

    // Check if WhatsApp is installed on the device
    private boolean isWhatsAppInstalled(){
        PackageManager packageManager= getApplicationContext().getPackageManager();
        boolean whatsappInstalled;

        try {
            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            Log.d("TAG","have WhatsApp");
            whatsappInstalled=true;
        } catch (PackageManager.NameNotFoundException e) {
            whatsappInstalled=false;
            Log.d("TAG","no WhatsApp");
        }
        return whatsappInstalled;
    }
}