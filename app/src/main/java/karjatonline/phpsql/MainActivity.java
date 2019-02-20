package karjatonline.phpsql;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etName;
    String url="http://thekarjat.com/addRKarjat.php";
//    String url = "https://wwwkarjatonlinecom.000webhostapp.com/addc.php";
//    String url2="http://192.168.0.107/school/get.php";
    String url2="http://thekarjat.com/getKarjat.php";
    //String url2 = "https://wwwkarjatonlinecom.000webhostapp.com/getdata.php";

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    ListView lv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        btnSubmit = findViewById(R.id.btnSubmit);
        lv=findViewById(R.id.lv);

        getJSON(url2);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList nameValuePairs = new ArrayList();
                String str=etName.getText().toString();
                if(str.contains("'")){
                    str=str.replace("'","''");
                }


                nameValuePairs.add(new BasicNameValuePair("name", str));


                StrictMode.setThreadPolicy(policy);

//        Log.d(“well3”, “msg”);
//http post
                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost(url);

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();


                    Toast.makeText(MainActivity.this, "Successful !", Toast.LENGTH_SHORT).show();
                    getJSON(url2);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
                }
                etName.setText("");
            }
        });
    }

    public void loadIntoListView(String[] s){
        ArrayAdapter<String> adp=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,s);
        lv.setAdapter(adp);
    }

    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //           Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                //  tv.setText(s);
                Log.d( "*/postexecute",s);
                String[] heroes;
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    heroes = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
//                        heroes[i] = obj.getString("srno")+" "+obj.getString("name");
                        heroes[i] = obj.getString("bizname");
                    }
                    for(int i=0;i<heroes.length;i++){
                    //    Log.d("*/ forloop",heroes[i]);
                    }
                    loadIntoListView(heroes);
                } catch (JSONException e) {
                 //   Log.d("*/try block",e.getLocalizedMessage());
                    e.printStackTrace();
                }




            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {


                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();


                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                   //     Log.d("*/",json);
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();


    }
}