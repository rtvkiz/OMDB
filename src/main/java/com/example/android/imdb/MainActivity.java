package com.example.android.imdb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    String url1;
    private static  int splash=2000;
    private FirebaseAuth mfirebase;
    private FirebaseAuth.AuthStateListener mlistener;
    public static final int RC_SIGN_IN=1;
   // TsunamiAsyncTask task;
    //static String url1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    mfirebase=FirebaseAuth.getInstance();
       //task = new TsunamiAsyncTask();
        //task.execute();
        mlistener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user != null){
                    String name=user.getDisplayName();
                    Toast.makeText(MainActivity.this,name,Toast.LENGTH_LONG).show();
                }
                else{
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),

                                            new AuthUI.IdpConfig.GoogleBuilder().build()


                                           ))
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listty,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signout:
                AuthUI.getInstance().signOut(this);
                return true;
                default: return super.onOptionsItemSelected(item);

        }

    }

    public void value(View v){
    EditText text = (EditText)findViewById(R.id.editText);
    Editable input1 = text.getText();
    String input=input1.toString();
    TsunamiAsyncTask ap=new TsunamiAsyncTask();
    url1="http://www.omdbapi.com/?t="+input+"&apikey=3f9e318f";
    ap.execute();
    }



    private void updateUi(Event earthquake) {

    {
        // Display the earthquake title in the UI
        TextView titleTextView = (TextView) findViewById(R.id.name);
        titleTextView.setText(earthquake.name1);

        // Display the earthquake date in the UI
        TextView genre = (TextView) findViewById(R.id.genre);
        //dateTextView.setText(getDateString(earthquake.time));
        genre.setText(earthquake.genre);
        TextView plot=(TextView ) findViewById(R.id.plot);
        plot.setText(earthquake.plot);
        // Display whether or not there was a tsunami alert in the UI
        TextView runtime = (TextView) findViewById(R.id.runtime);
        //tsunamiTextView.setText(getTsunamiAlertString(earthquake.tsunamiAlert));
        runtime.setText(earthquake.runtime);
        ImageView img=(ImageView)findViewById(R.id.image1);
        /*try{
        URL url=new URL("https://goo.gl/images/kDgcdZ");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            img.setImageBitmap(bmp);}
        catch (Exception e){
            System.out.print(e);
        }*/
        Glide.with(getApplicationContext())
                .load(earthquake.url)
                .override(600,600)
                .into(img);
    }



    }


     private class TsunamiAsyncTask extends AsyncTask<URL, Void, Event>{

        @Override
        protected Event doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(url1);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            Event earthquake = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return earthquake;
        }

        protected void onPostExecute(Event earthquake) {
            if (earthquake == null) {
                return;
            }

            updateUi(earthquake);
        }
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                //Log( "Error with creating URL");
                System.out.println(exception);
                return null;
            }
            return url;
        }
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                // TODO: Handle the exception
                System.out.println(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
        private Event extractFeatureFromJson(String earthquakeJSON){
            try{
                JSONObject root=new JSONObject(earthquakeJSON);
                String response=root.getString("Response");
                if(response.length()==4) {
                    String Title = root.getString("Title");
                    String genre = root.getString("Genre");
                    String runtime = root.getString("imdbRating");
                    String plot = root.getString("Plot");
                    String url = root.getString("Poster");

                    return new Event(Title, runtime, genre, url, plot);
                }
                else{

                    return new Event("Invalid Movie Name",null,null,null,null);
                }

            }
            catch (JSONException e) {
                //Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
                System.out.println(e);
            }
            return null;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this,"Signed in",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED){

                finish();
            }

        }
    }
    public void sign(View view){

        AuthUI.getInstance().signOut(this);
        return;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mfirebase.addAuthStateListener(mlistener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mfirebase.removeAuthStateListener(mlistener);
    }
}
