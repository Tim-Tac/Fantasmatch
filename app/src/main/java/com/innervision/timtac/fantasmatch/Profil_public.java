package com.innervision.timtac.fantasmatch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Profil_public extends Activity {
    private String ligne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_public);

        Button boutonoui = (Button) findViewById(R.id.boutonoui);
        Button boutonnon = (Button) findViewById(R.id.boutonnon);
        TextView lastname = (TextView)findViewById(R.id.nom);
        TextView firstname = (TextView)findViewById(R.id.prenom);
        TextView email = (TextView)findViewById(R.id.email);
        TextView naissance = (TextView)findViewById(R.id.naissance);
        TextView genre = (TextView)findViewById(R.id.genre);
        TextView interest = (TextView)findViewById(R.id.interest);
        TextView status = (TextView)findViewById(R.id.status);
        TextView id = (TextView)findViewById(R.id.id);
        new DownloadImageTask((ImageView) findViewById(R.id.im_profil)).execute("https://graph.facebook.com/10205118409574247/picture");

        Intent intent = getIntent();

        lastname.setText(intent.getStringExtra("lastname"));
        firstname.setText(intent.getStringExtra("firstname"));
        email.setText(intent.getStringExtra("email"));
        naissance.setText(intent.getStringExtra("birthday"));
        genre.setText(intent.getStringExtra("gender"));
        interest.setText(intent.getStringExtra("interest"));
        status.setText(intent.getStringExtra("status"));
        id.setText(intent.getStringExtra("id"));


        String url = "http://192.168.1.23:8000/api/v1/user";

        try {
            new Script().execute(url,intent.getStringExtra("lastname").toString(),intent.getStringExtra("firstname").toString(),intent.getStringExtra("email").toString(),intent.getStringExtra("gender").toString(),intent.getStringExtra("id").toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(),ligne,Toast.LENGTH_LONG).show();





        boutonoui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "publique", Toast.LENGTH_SHORT).show();
            }
        });


        boutonnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "pas publique", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /***************************Récupération de l'image*********************************/
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



    /************************Classe thread qui intéragit avec le serveur***************************/
    public class Script extends AsyncTask <String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try{

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(params[0]);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                nameValuePairs.add(new BasicNameValuePair("last_name", params[1]));
                nameValuePairs.add(new BasicNameValuePair("first_name", params[2]));
                nameValuePairs.add(new BasicNameValuePair("email", params[3]));
                nameValuePairs.add(new BasicNameValuePair("gender", params[4]));
                nameValuePairs.add(new BasicNameValuePair("facebook_id", params[5]));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(request);

                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));
                ligne = in.readLine();
                in.close();

            }catch(Exception e){
                Log.e("log_tag", "Error in http connection "+e.toString());
            }
            return ligne;

        }

        @Override
        protected void onPostExecute(String ligne){
        }
    }

}
