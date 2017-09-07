package com.example.himasha.workhub;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddJobActivity extends AppCompatActivity {

    private EditText addJobName;
    private EditText addJobDesc;
    private EditText addJobBudget;
    private Button submitjob;
    private AutoCompleteTextView addjobSkills;

    private String addJobLocation;

    private FirebaseAuth auth;
    private DatabaseReference workhub;
    private DatabaseReference workhubUsers;

    private Double tripLong ;
    private Double tripLat;

    private AlertDialog.Builder builder;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        builder = new AlertDialog.Builder(this);
        auth = FirebaseAuth.getInstance();
        workhub = FirebaseDatabase.getInstance().getReference().child("jobs");
        workhubUsers = FirebaseDatabase.getInstance().getReference().child("users");

        String[] skills = {"Home Cleaner", "Nanny", "Baker", "Cook", "Maid", "Fllor technician", "Pet Sitter", "Plumber"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, skills);

        addJobName = (EditText)findViewById(R.id.addjobJobNameET);
        addJobDesc = (EditText)findViewById(R.id.addjobDescET);
        addJobBudget = (EditText)findViewById(R.id.addjobBudgetET);
        submitjob = (Button)findViewById(R.id.submitjobBTN);
        addjobSkills = (AutoCompleteTextView)findViewById(R.id.addjobSkilltET);
        addjobSkills.setThreshold(1);
        addjobSkills.setAdapter(adapter);

        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("LK")
                .build();

        places.setFilter(typeFilter);

        places.setHint("e.g Malabe");
        ((View)findViewById(R.id.place_autocomplete_search_button)).setVisibility(View.GONE);
        ((EditText)findViewById(R.id.place_autocomplete_search_input)).setBackgroundResource(R.drawable.input_outline);
        ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextSize(18);
        ((EditText)findViewById(R.id.place_autocomplete_search_input)).setPadding(32,32,32,32);

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                tripLat = place.getLatLng().latitude;
                tripLong = place.getLatLng().longitude;
                addJobLocation = place.getName().toString();

            }
            @Override
            public void onError(Status status) {

            }

        });

        workhubUsers.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = (String) dataSnapshot.child("userName").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submitjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String jname = addJobName.getText().toString();
                final String jdesc = addJobDesc.getText().toString();
                final String jbudget = addJobBudget.getText().toString();
                final String skill = addjobSkills.getText().toString();
                if (!TextUtils.isEmpty(jname) && !TextUtils.isEmpty(jdesc) && !TextUtils.isEmpty(jbudget) && !TextUtils.isEmpty(addJobLocation) && !TextUtils.isEmpty(skill))
                {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to post this job?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {




                            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                            final String jdate = sdf.format(new Date());

                            String juid = auth.getCurrentUser().getUid();

                            String jobId = workhub.push().getKey();

                            Job newJob = new Job();
                            newJob.setJobName(jname);
                            newJob.setJobDesc(jdesc);
                            newJob.setJobBudget(jbudget);
                            newJob.setJobLocationName(addJobLocation);
                            newJob.setJobLocationLat(tripLat);
                            newJob.setJobLocationLong(tripLong);
                            newJob.setJobPostedDate(jdate);
                            newJob.setJobPostedUserId(juid);
                            newJob.setJobKeyWord(skill);
                            newJob.setJobPostedUserName(userName);
                            newJob.setJobStatus("Available");

                            workhub.child(jobId).setValue(newJob);

                            Toast.makeText(AddJobActivity.this,"Job added successfuly",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            startActivity(new Intent(AddJobActivity.this,FeedActivity.class));
                            finish();
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
                else
                {
                    Toast.makeText(AddJobActivity.this,"You cannot have one or more empty fields!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
