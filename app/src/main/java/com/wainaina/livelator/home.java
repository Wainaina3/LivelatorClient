package com.wainaina.livelator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.*;
import com.ibm.mobilefirstplatform.clientsdk.android.security.mca.api.MCAAuthorizationManager;

import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;


public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,Translate_Text.OnFragmentInteractionListener,
        Translated_Speech.OnFragmentInteractionListener,Profile.OnFragmentInteractionListener {
    //Define objects
    protected DrawerLayout drawer;
    public static boolean audioOn = false;
    protected static Context hContext = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hContext = this.getApplicationContext();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(findViewById(R.id.fragment_container)!=null){
            if(savedInstanceState!=null){
                return;
            }

            //initialize speech fragment
            Translated_Speech translated_speech_frag = new Translated_Speech();

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, translated_speech_frag).commit();

            getSupportActionBar().setTitle(R.string.live_translation_title);

            //Initialize BMS client

            initializeBMSClient();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Get models
    private static class initializeBMSClientClass extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... none) {
            Boolean loginResult;
            try {

                //Initialize BMS client

                BMSClient.getInstance().initialize(hContext, BMSClient.REGION_US_SOUTH);

                BMSClient.getInstance().setAuthorizationManager(
                        MCAAuthorizationManager.createInstance(hContext, "b28db5ce-e312-4518-a3fd-34bdd43cc970"));

                Request request = new Request("https://livelator.mybluemix.net/audio", Request.GET);
                request.send(hContext, new ResponseListener() {
                    @Override
                    public void onSuccess (Response response) {
                        Log.d("Myapp", "onSuccess :: " + response.getResponseText());

                    }
                    @Override
                    public void onFailure (Response response, Throwable t, JSONObject extendedInfo) {
                        if (null != t) {
                            Log.d("Myapp", "onFailure :: " + t.getMessage());
                        } else if (null != extendedInfo) {
                            Log.d("Myapp", "onFailure :: " + extendedInfo.toString());
                        } else {
                            Log.d("Myapp", "onFailure :: " + response.getResponseText());
                        }
                        Log.d("Livelator", "Failure :: " + response.getResponseText());
                    }
                });
                //end of authorization trial
                Log.d("Livelator", "Send a request for authorization");
                loginResult = Boolean.TRUE;
            }
            catch (Exception ioe) {
                Log.e("VS", "IOException");
                loginResult = Boolean.FALSE;
            }

            return loginResult;

        }
    }

    public void initializeBMSClient() {

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
              new initializeBMSClientClass().doInBackground();
            }
        });

        streamThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        if (id == R.id.nav_speech) {
            Translated_Speech translated_speech_frag = new Translated_Speech();
            fragmentTransaction.replace(R.id.fragment_container,translated_speech_frag).commit();
            getSupportActionBar().setTitle(R.string.live_translation_title);


        } else if (id == R.id.nav_subtitles) {
            Translate_Text translate_text_frag = new Translate_Text();
            fragmentTransaction.replace(R.id.fragment_container,translate_text_frag).commit();
            getSupportActionBar().setTitle(R.string.translated_text_title);

        } else if (id == R.id.nav_profile) {
            Profile profile_frag = new Profile();
            fragmentTransaction.replace(R.id.fragment_container,profile_frag).commit();
            getSupportActionBar().setTitle(R.string.profile_title);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //implement private OnFragmentInteractionListener mListener
    public void translated_Speech_interface_callback() {
        //do some stuff
    }

    //implement private OnFragmentInteractionListener mListener
    public void translated_text_interface_callback() {
        //do some stuff
    }

    //implement private OnFragmentInteractionListener mListener
    public void profile_interface_callback() {
        //do some stuff
    }

}
