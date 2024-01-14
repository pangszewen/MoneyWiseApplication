package com.example.moneywise.scholarship;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ApplyScholarship extends AppCompatActivity {

    String scholarshipID, institution, title, description, criteria, award, website;
    int scholarshipNum;
    Date deadline;
    boolean saved;
    TextView txtTitle, txtAbout, txtValue, txtCriteria, txtWebsite;
    Button apply;
    FirebaseAuth auth;
    FirebaseUser user;
    String userID;
    FirebaseFirestore db;
    ImageView bookmark;
    boolean isSaved,notification;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    private static final int defaultValue = 100;
    private BroadcastReceiver localReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_scholarship);

        auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userID = user.getUid();

        // Create notification channel for deadlines
        createNotificationChannel();



        // Register a local broadcast receiver
        Log.d("TAG","Notification 3"+notification);
        localReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Handle the broadcast and call makeNotification
                if (notification){
                    String scholarshipTitle = intent.getStringExtra("scholarshipTitle");
                    makeNotification(scholarshipTitle);
                    Log.d("TAG","Notification enabled");
                }
            }
        };

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(localReceiver, new IntentFilter("com.example.madassignment.scholarship.NOTIFICATION_RECEIVED"));


        txtTitle = findViewById(R.id.txtTitle);
        txtAbout = findViewById(R.id.txtAbout);
        txtValue = findViewById(R.id.txtValue);
        txtCriteria = findViewById(R.id.txtCriteria);
        txtWebsite = findViewById(R.id.txtWebsite);

        // Retrieve scholarship information from the Intent
        scholarshipID = getIntent().getStringExtra("scholarshipID");
        institution = getIntent().getStringExtra("institution");
        title = getIntent().getExtras().getString("title");
        description = getIntent().getExtras().getString("description");
        criteria = getIntent().getExtras().getString("criteria");
        award = getIntent().getExtras().getString("award");
        deadline = (Date) getIntent().getSerializableExtra("deadline");
        website = getIntent().getExtras().getString("website");
        isSaved = getIntent().getExtras().getBoolean("saved");

        // Create the scholarship object in this activity
        Scholarship scholarship = new Scholarship(scholarshipID, institution, title, description, criteria, award, deadline, website, saved);

        // Set scholarship details to corresponding TextViews
        txtTitle.setText(getIntent().getExtras().getString("title"));
        txtAbout.setText(getIntent().getExtras().getString("description"));
        txtValue.setText(getIntent().getExtras().getString("award"));
        txtCriteria.setText(getIntent().getExtras().getString("criteria"));
        txtWebsite.setText(getIntent().getExtras().getString("website"));

        // Use scholarship ID to generate scholarshipNum, unique id for pending intent and alarm manager
        String numericPart = scholarshipID.replaceAll("\\D+", "");
        scholarshipNum = Integer.parseInt(numericPart) + defaultValue;

        // Listen for changes in notification preference from Firestore
        DocumentReference ref= db.collection("USER_DETAILS").document(userID);
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                notification=value.getBoolean("notification");
                Log.d("TAG","Notification 1"+notification);
                Log.d("TAG","userid "+userID);
                if (notification){
                    Log.d("TAG","Notification 2"+notification);
                }if(!notification){
                    Log.d("TAG","Notification 2"+notification);
                }
            }
        });

        // Ask for user's permission to send notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(ApplyScholarship.this,
                    Manifest.permission.POST_NOTIFICATIONS)!=
                    PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(ApplyScholarship.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Set bookmark image based on saved status
        bookmark = findViewById(R.id.imageSave);

        setBookmarkImage(isSaved);

        // Toggle saved status and update UI and Firestore on bookmark click
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TAG","Notification bookmark"+notification);
                // Toggle the saved status
                isSaved = !isSaved;

                // Update the saved status in Firestore
                updateSavedStatusInFirestore(isSaved, userID, scholarshipID);

                // Update the bookmark image
                setBookmarkImage(isSaved);

                // Update the isSaved property in the Scholarship object
                scholarship.setSaved(isSaved);


            }
        });


        // Open scholarship website in browser on "Apply" button click
        apply = findViewById(R.id.btnApply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getIntent().getExtras().getString("website")));
                startActivity(intent);
            }
        });


        // Navigate back to the previous page on back arrow click
        ImageView back = findViewById(R.id.imageArrowleft);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the origin activity from the Intent
                String originActivity = getIntent().getStringExtra("originActivity");

                // Handle the click event, navigate to the appropriate activity
                Intent intent;
                if ("FindScholarshipActivity".equals(originActivity)) {
                    intent = new Intent(ApplyScholarship.this, FindScholarshipActivity.class);
                } else if ("BookmarkActivity".equals(originActivity)) {
                    intent = new Intent(ApplyScholarship.this, BookmarkActivity.class);
                } else {
                    // Default to FindScholarshipActivity if not specified
                    intent = new Intent(ApplyScholarship.this, FindScholarshipActivity.class);
                }

                startActivity(intent);
            }
        });



    }
    @Override
    protected void onDestroy() {
        // Unregister the local broadcast receiver to avoid memory leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
        super.onDestroy();
    }

    // Create a notification channel for the app (for Android Oreo and above)
    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ReminderChannel";
            String description = "Channel for Deadline Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyDeadline", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Display a notification with a reminder message
    public void makeNotification(String scholarshipTitle){

            String channelID = "notifyDeadline";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);

            // Set the time zone to UTC
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Format the deadline as a string with UTC time zone
            String utcDeadlineString = sdf.format(deadline);

            builder.setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                    .setContentTitle("Reminder")
                    .setContentText("The scholarship deadline for " + title + " is " + utcDeadlineString + " .Apply on time!")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), scholarshipNum, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

                if (notificationChannel == null) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    notificationChannel = new NotificationChannel(channelID, "Reminder", importance);
                    notificationChannel.setLightColor(Color.GREEN);
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);

                }
            }

            notificationManager.notify(scholarshipNum, builder.build());
            Log.d("start", "Scholarship: " + title + ", Deadline: " + scholarshipNum);

    }

    // Set the bookmark image based on saved status
    private void setBookmarkImage(boolean isSaved) {
        if (isSaved) {
            bookmark.setImageResource(R.drawable.img_bookmark_checked);
        } else {
            bookmark.setImageResource(R.drawable.img_bookmark_unchecked);
        }
    }

    // Update the saved status in Firestore based on user interactions
    private void updateSavedStatusInFirestore(boolean isSaved, String userId, String scholarshipId) {
        // Get the reference to the user_details document
        DocumentReference userRef = db.collection("USER_DETAILS").document(userId);

        // Update the saved_scholarship array
        if (isSaved) {
            // Add scholarship ID to the array
            userRef.update("saved_scholarship", FieldValue.arrayUnion(scholarshipId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update successful
                            Log.d("Firestore", "Saved scholarship ID added to user_details");

                            // Schedule the notification when saved
                            scheduleNotification(deadline.getTime() - 8*60*60*1000 - System.currentTimeMillis());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle error
                            Log.e("Firestore", "Error adding saved scholarship ID to user_details", e);
                        }
                    });
        } else {
            // Remove scholarship ID from the array
            userRef.update("saved_scholarship", FieldValue.arrayRemove(scholarshipId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update successful
                            Log.d("Firestore", "Saved scholarship ID removed from user_details");

                            // Cancel the scheduled notification when unsaved
                            cancelNotification();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle error
                            Log.e("Firestore", "Error removing saved scholarship ID from user_details", e);
                        }
                    });
        }
    }

    // Schedule a notification for a scholarship deadline
    private void scheduleNotification(long timeDifference) {

        if ((timeDifference - 24 * 60 * 60 * 1000 )>0) {


            // Calculate the time for the notification one day before the deadline
            long notificationTimeMillis = System.currentTimeMillis() + timeDifference - 24 * 60 * 60 * 1000;

            // Create an Intent to be triggered when the alarm fires
            Intent notificationIntent = new Intent(this, NotificationReceiver.class);
            notificationIntent.putExtra("scholarshipTitle", title);

            // Create a PendingIntent for the Intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    scholarshipNum,
                    notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );

            // Get the AlarmManager service
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            // Schedule the alarm
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notificationTimeMillis,
                    pendingIntent
            );
            Log.d("Notification", "Reminder set");
            Log.d("Notification", "Scheduling notification for scholarship: " + title + ", Deadline: " + notificationTimeMillis);
            Toast.makeText(getApplicationContext(), "Scholarship saved! Reminder set!", LENGTH_SHORT).show();


        } else {
            // Handle the case where the calculated time is in the past
            Log.d("Notification", "Invalid reminder time");
            Toast.makeText(getApplicationContext(), "Deadline is less than 24 hours", LENGTH_SHORT).show();

        }
    }

    // Cancel a scheduled notification
    private void cancelNotification() {

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                scholarshipNum,
                notificationIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Get the AlarmManager service and cancel the scheduled notification
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("Notification", "Reminder cancelled");
            Toast.makeText(getApplicationContext(), "Scholarship unsaved! Reminder cancelled!", LENGTH_SHORT).show();


        }
    }


}