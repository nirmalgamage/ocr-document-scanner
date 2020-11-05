package com.example.ocr_one;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity  {
    ///for navigation drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;

    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    Button startCam,openGallery;
    static final Integer CAMERA = 0x5;


    // Load library initially
    //static {
    //    System.loadLibrary("native");
   // }

    public static native int test();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout) ;
        mToggle=new ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(mToggle);
       // drawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nv=(NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch(id){
                    case R.id.nav_settings:

                        Intent intent=new Intent(MainActivity.this,Settings.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_about:

                        break;

                    case R.id.nav_userguide:

                        break;
                }


                return true;
            }
        });




        startCam = (Button) findViewById(R.id.start_cam);
        openGallery=(Button)findViewById(R.id.open_gallery);
        startCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do stuff here
                if(ask()) {
                    //for opencv camera
                Intent intent=new Intent(MainActivity.this,CameraActivity.class);
                   startActivity(intent);
                   //end opencv camera

                }
                ///for android camera
                ///**************************##############################################


            }
        });

       openGallery.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               // do stuff here

                   Intent intent=new Intent(MainActivity.this,ImageActivity.class);
                   startActivity(intent);

                   // startCam.setText("ddddddd");


               //else{

               //}

           }
       });

    }
//////These for navigation items


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            Log.d("item-selected","Here the item was selected!!!!");

            return true;
        }
        switch(item.getItemId()){
            case R.id.settings:
                Intent intent=new Intent(MainActivity.this,Settings.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);


//        int id = item.getItemId();
//
//        if (id == R.id.settings) {
//            Intent intent=new Intent(MainActivity.this,Settings.class);
//            startActivity(intent);
//            // Handle the settings action
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;

    }




    private boolean askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
            return false;
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean ask() {
        boolean answer = false;
        if (askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST)) {
            if (askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST)) {
                if (askForPermission(Manifest.permission.CAMERA, CAMERA)) {
                    answer = true;
                }
            }
        } else {
            answer = false;
        }
        return answer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }



}

