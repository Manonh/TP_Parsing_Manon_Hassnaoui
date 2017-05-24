package com.example.manon.myfirstjson;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    //URL pour le contact avec le JSON
    private static String url = "http://api.androidhive.info/contacts/";

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
    }

    //rajout d'une class asyncTask, GetContacts pour récupérer le JSON

  private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //progression du dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(Void...arg0){
            HttpHandler sh = new HttpHandler();

            //requête à l'url pour une réponse
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null){
                try{
                    JSONObject jsonobj = new JSONObject(jsonStr);

                    // JSON ARRAY
                    JSONArray contacts = jsonobj.getJSONArray("contacts");

                    //boucle sur tous les contacts
                    for (int i = 0; i < contacts.length(); i++){
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        //phone is a JSONObject
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        //hashMap
                        HashMap<String, String> contact = new HashMap<>();
                        //on rajoute les enfants aux clefs du HashMap => valeur
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);
                        contact.put("gender", gender);
                        contact.put("address", address);

                        //et on les met dans la liste
                        contactList.add(contact);
                    }
                }catch (final JSONException e){
                    Log.e(TAG, "Json Parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }else{
                Log.e(TAG, "Couldn't get json from server");
                runOnUiThread((new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors !", Toast.LENGTH_LONG).show();
                    }
                }));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // progression dialogue
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
           // Mise à jour du parsing des données JSON dans la listView
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList, R.layout.list_item, new  String[]{"name", "email", "mobile", "gender", "address"}, new int[]{R.id.name, R.id.email, R.id.mobile, R.id.gender, R.id.address});

            lv.setAdapter(adapter);
        }
    }


}
