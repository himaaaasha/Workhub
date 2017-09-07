package com.example.himasha.workhub;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SingleJobView extends AppCompatActivity {

    private String job_key = null;
    private String jobAddedUser = null;

    private TextView jobName;
    private TextView jobDesc;
    private TextView jobLocation;
    private TextView jobBudget;
    private TextView jobPostedDate;
    private TextView jobPostedUser;
    private TextView jobPostedMap;

    private Button deletejob;
    private Button editjob;

    private CardView locationMapCardview;
    private CardView editRemoveJobcardview;
    private CardView postedUserCardview;

    private DatabaseReference workhubUsers;
    private DatabaseReference workhubJobs;
    private DatabaseReference workhubsuges;
    private FirebaseAuth auth;

    private AlertDialog.Builder builder;
    private String job_location;

    private Double jobLat;
    private Double jobLong;
    private String posteduserid;

    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_job_view);
        pref = this.getSharedPreferences("Users",0);
        job_key = getIntent().getExtras().getString("job_id");
        auth = FirebaseAuth.getInstance();
        workhubJobs = FirebaseDatabase.getInstance().getReference().child("jobs");
        workhubUsers = FirebaseDatabase.getInstance().getReference().child("users");

        builder = new AlertDialog.Builder(this);

        jobName = (TextView) findViewById(R.id.singlejobName);
        jobDesc = (TextView) findViewById(R.id.singlejobDesc);
        jobLocation = (TextView) findViewById(R.id.singlejobLocation);
        jobBudget = (TextView) findViewById(R.id.singlejobBudget);
        jobPostedDate = (TextView) findViewById(R.id.singlejobPostedDate);
        jobPostedUser = (TextView) findViewById(R.id.singlejobPostedUser);
        jobPostedMap = (TextView) findViewById(R.id.singlejobMapName);

        deletejob = (Button) findViewById(R.id.singlejobDeleteBTN);
        editjob = (Button) findViewById(R.id.singlejobEditBTN);

        locationMapCardview = (CardView)findViewById(R.id.locationMapCard);
        editRemoveJobcardview = (CardView)findViewById(R.id.editremoveJobCard);
        postedUserCardview = (CardView) findViewById(R.id.postedUserCard);


        workhubJobs.child(job_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                final String job_name = (String) dataSnapshot1.child("jobName").getValue();
                final String job_desc = (String) dataSnapshot1.child("jobDesc").getValue();
                job_location = (String) dataSnapshot1.child("jobLocationName").getValue();
                final String job_budget = (String) dataSnapshot1.child("jobBudget").getValue();
                final String job_postedDate = (String) dataSnapshot1.child("jobPostedDate").getValue();
                final String job_postedUser = (String) dataSnapshot1.child("jobPostedUserId").getValue();
                posteduserid = (String) dataSnapshot1.child("jobPostedUserId").getValue();
                final String job_postedUserName = (String) dataSnapshot1.child("jobPostedUserName").getValue();
                jobLat = (Double) dataSnapshot1.child("jobLocationLat").getValue();
                jobLong = (Double) dataSnapshot1.child("jobLocationLong").getValue();

                jobName.setText(job_name);
                jobDesc.setText(job_desc);
                jobLocation.setText(job_location);
                jobBudget.setText("Rs."+job_budget);
                jobPostedDate.setText("Posted on "+job_postedDate);
                if (posteduserid.equals(auth.getCurrentUser().getUid()))
                {
                    jobPostedUser.setText("Posted by You");
                }
                else
                {
                    jobPostedUser.setText("Posted by "+job_postedUserName);
                }

                jobPostedMap.setText("get directions to "+job_location);


                if (auth.getCurrentUser().getUid().equals(job_postedUser))
                {
                    editRemoveJobcardview.setVisibility(View.VISIBLE);
                }
                else {
                    editRemoveJobcardview.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        postedUserCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posteduserid.equals(auth.getCurrentUser().getUid()))
                {

                }
                else {
                    Intent intent = new Intent(SingleJobView.this, SingleProfileActivity.class);
                    intent.putExtra("user_id", posteduserid);
                    startActivity(intent);
                }

            }
        });

        locationMapCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(SingleJobView.this, MapLoadActivity.class);
                intent.putExtra("job_lat", jobLat);
                intent.putExtra("job_long",jobLong);
                startActivity(intent);
            }
        });


        editjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleJobView.this, EditJobActivity.class);
                intent.putExtra("job_id", job_key);
                startActivity(intent);
            }
        });

        deletejob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this job?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if(job_key != null) {
                            workhubJobs.child(job_key).removeValue();

                            Intent intent2 = new Intent(SingleJobView.this, FeedActivity.class);
                            startActivity(intent2);
                            finish();
                        }

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });



    }

}
