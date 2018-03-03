package com.mavzapps.soccerdemo;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoadingFragment.OnFragmentInteractionListener, GameListFragment.OnListFragmentInteractionListener{

    private final String ARG_GAME_DATA = "game-data";

    //URLs
    String urlLogin = "http://fxservicesstaging.nunchee.com/api/1.0/auth/users/login/anonymous";
    String urlGames = "http://fxservicesstaging.nunchee.com/api/1.0/sport/events";

    //Access Token
    String accessToken = "";

    //Games Variables
    String tournament = "";
    List<GameObject> gameObjectList;

    //Fragments
    LoadingFragment loadingFragment;
    GameListFragment gameListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState!=null){
            gameObjectList = savedInstanceState.getParcelableArrayList(ARG_GAME_DATA);
            if(gameObjectList!=null)
                setGamesFragment(gameObjectList);
            else{
                setLoadingFragment();
                loginRequest();
            }
        }else{
            setLoadingFragment();
            loginRequest();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    /*@Override
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
    }*/

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setLoadingFragment(){
        loadingFragment = LoadingFragment.newInstance();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.push_left_in,R.anim.push_left_out);
        transaction.add(R.id.content_layout,loadingFragment);
        transaction.commit();
    }

    public void setGamesFragment(List<GameObject> gameObjectList){
        gameListFragment = GameListFragment.newInstance(1,(ArrayList<GameObject>) gameObjectList);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.push_left_in,R.anim.push_left_out);
        transaction.replace(R.id.content_layout,gameListFragment);
        transaction.commit();
    }

    public void loginRequest(){

        try{
            JSONObject loginBodyPost = new JSONObject();
            //User
            JSONObject user = new JSONObject();
            JSONObject profile = new JSONObject();
            profile.put("language","es");
            user.put("profile",profile);
            loginBodyPost.put("user",user);
            //Device
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            JSONObject device = new JSONObject();
            device.put("deviceId","12345678");
            device.put("name","MyPhone");
            device.put("version", Build.VERSION.RELEASE);
            device.put("width",metrics.widthPixels);
            device.put("height",metrics.heightPixels);
            device.put("model",Build.MODEL);
            device.put("platform","android");
            loginBodyPost.put("device",device);
            //app
            JSONObject app = new JSONObject();
            app.put("version",BuildConfig.VERSION_NAME);
            loginBodyPost.put("app",app);

            Log.d("bodyRequest",loginBodyPost.toString());

            //RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlLogin, loginBodyPost, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("onResponse",response.toString());
                    try {
                        accessToken = response.getJSONObject("data").getString("accessToken");
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGames, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("onResponse",response.toString());
                                gameObjectList = parseGamesResponse(response);
                                setGamesFragment(gameObjectList);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("onError", "An error has ocurred "+error.getMessage());
                                loadingFragment.setLoadingMessage("Ha ocurrido un error en la descarga de datos");
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                final Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer " + accessToken);
                                return headers;
                            }
                        };
                        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("onError", "An error has ocurred "+error.getMessage());
                    loadingFragment.setLoadingMessage("Ha ocurrido un error en la autenticación");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Basic " + "cHJ1ZWJhc2RldjpwcnVlYmFzZGV2U2VjcmV0");
                    headers.put("Content-Type","application/json");
                    return headers;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }catch (JSONException exception){
            exception.printStackTrace();
        }
    }

    public List<GameObject> parseGamesResponse(JSONObject response){
        List<GameObject> gameObjectList = new ArrayList<>();
        try {
            JSONObject data = response.getJSONObject("data");
            tournament = data.getJSONArray("sections").getJSONObject(0).getJSONObject("title").getString("original");
            JSONArray items = data.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                GameObject gameObject = new GameObject(i%2);
                gameObject.setGameName(items.getJSONObject(i).getJSONObject("matchDay").getJSONObject("name").getString("original"));
                gameObject.setGameStatus(items.getJSONObject(i).getJSONObject("eventStatus").getJSONObject("name").getString("es"));
                gameObject.setGameLocation(items.getJSONObject(i).getJSONObject("location").getString("original"));
                String startDateStr = items.getJSONObject(i).getString("startDate");
                String endDateStr = items.getJSONObject(i).getString("endDate");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                try {
                    Date startDate = format.parse(startDateStr);
                    gameObject.setStartDate(startDate);
                    Date endDate = format.parse(endDateStr);
                    gameObject.setEndDate(endDate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                gameObject.setHomeTeamName(items.getJSONObject(i).getJSONObject("homeTeam").getString("name"));
                gameObject.setAwayTeamName(items.getJSONObject(i).getJSONObject("awayTeam").getString("name"));
                gameObject.setHomeTeamScore(items.getJSONObject(i).getInt("homeScore"));
                gameObject.setAwayTeamScore(items.getJSONObject(i).getInt("awayScore"));

                gameObjectList.add(gameObject);
            }
            Log.d("Parse", "Finished parsing");
            return gameObjectList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(gameObjectList!=null && gameObjectList.size()>0){
            outState.putParcelableArrayList(ARG_GAME_DATA,(ArrayList<? extends Parcelable>) gameObjectList);
        }
    }

    @Override
    public void onListFragmentInteraction(GameObject item) {

    }
}
