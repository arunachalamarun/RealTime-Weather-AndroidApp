package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
  private  ImageView setBack;
    private TextView clouds;
    private TextView cloudy;
    private TextView temperature;
    private TextView humid;
    private TextView Name;
    private EditText get;
    private String city = "Bengaluru";

    public class download extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpsURLConnection connection;
            String result = "";
            try {
                url = new URL(urls[0]);
                connection = (HttpsURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
        after the process completes the background execution this method gets calls
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject weather = new JSONObject(s);
                String showWeather = weather.getString("weather");
                String temps = weather.getString("main");
                String nmae = weather.getString("name");
                String main = null, description = null, feelsTemp = null, humidity = null;
                JSONArray showWeatherArr = new JSONArray(showWeather);
                JSONObject n1 = new JSONObject(temps);
                feelsTemp = n1.getString("temp");
                humidity = n1.getString("humidity");
                clouds = findViewById(R.id.clouds);
                cloudy = findViewById(R.id.cloudy);
                temperature = findViewById(R.id.tempearture);

                for (int i = 0; i < showWeatherArr.length(); i++) {
                    JSONObject change = showWeatherArr.getJSONObject(i);
                    main = change.getString("main");
                    description = change.getString("description");
                }
                if (main != null && description != null && feelsTemp != null && humidity != null) {
                    clouds.setText("clouds are:" + main);
                    cloudy.setText(description);
                    temperature.setText("Temperatue is:" + feelsTemp);
                    humid.setText("Humidity is:" + humidity);
                    Name.setText("City: " + nmae);
                } else {

                    clouds.setText("Empty");
                    cloudy.setText("empty");
                    temperature.setText("empty");
                    humid.setText("Empty");
                }

            } catch (
                    JSONException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "Invalid value", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
    class used to download the image from the url
     */
    public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpsURLConnection connection;
            try {
                url = new URL(urls[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(in);
                return image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    /*
    method used to create a image from the internet and display on the screen
     */
    public void createRandom() {

        ImageLoader in = new ImageLoader();//user defined method
        Bitmap backUp;
        setBack = (ImageView) findViewById(R.id.imageView3);
        String[] link = {"https://www.maketecheasier.com/assets/uploads/2013/11/WeatherWallpaper-Beautiful-Live-Wallpaper.jpg",
                "https://androidhdwallpapers.com/media/uploads/2017/03/Fall-Weather-Flower-Nature-Sunset.jpg",
                "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80",
                "https://www.elsetge.cat/imagepost/b/67/677191_rain-wallpaper-hd-for-mobile.jpg",
                "https://preppywallpapers.com/wp-content/uploads/2018/10/14-Summer-iPhone-Wallpapers-by-Preppy-Wallpapers.jpg",
                "https://www.mordeo.org/files/uploads/2017/12/Sunny-Desert-Stone-HD-Mobile-Wallpaper-950x1520.jpg",
        };
        Random rand = new Random();
        int selectImage = rand.nextInt(6);
        try {
            backUp = in.execute(link[selectImage]).get();
            setBack.setImageBitmap(backUp);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /*
    method written to get executed when the button is pressed
    it executed the download execute method which is used to download the data from the source

     */
    public void onClick(View view) {
        humid = findViewById(R.id.humidity);
        Name = findViewById(R.id.Name);
        download dn = new download();
        String show;
        createRandom();


        try {
            city = get.getText().toString();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (city.isEmpty()) {
                Toast.makeText(this, "Input a city name", Toast.LENGTH_SHORT).show();
            } else {
                show = dn.execute("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=bbbc5c5e8661c8c05de3653c9da14794&units=Imperial").get();
                Log.i("content", show);
            }//end of if

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(this, "Input a city name", Toast.LENGTH_SHORT).show();
        }
    }//end of onclick

    /*
    main method used to
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createRandom();
        get = (EditText) findViewById(R.id.cityName);
    }
}


