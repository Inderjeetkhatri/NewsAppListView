package com.example.connectingtoapibasics;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private ListView listNews;
    private ProgressBar loader;


    ArrayList<HashMap<String, String>> articlesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articlesList = new ArrayList<>();
        lv = findViewById(R.id.list);
        listNews = findViewById(R.id.listNews);
        loader = findViewById(R.id.loader);
        listNews.setEmptyView(loader);

        new GetArticles().execute();
    }

    private class GetArticles extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String newsUrl = "https://newsapi.org/v2/top-headlines?sources=cnn&apiKey=7c64e1b4cb01427e837f67cf94b921c3";
            String jsonStr = sh.makeServiceCall(newsUrl);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray allArticles = jsonObj.getJSONArray("articles");

                    // looping through All Articles
                    for (int i = 0; i < allArticles.length(); i++) {
                        JSONObject c = allArticles.getJSONObject(i);
                        //String id = c.getString("id");
                        String author = c.getString("author");
                        String title = c.getString("title");
                        String url = c.getString("url");
                        String urlToImage=c.getString("urlToImage");
                        String publishedAt = c.getString("publishedAt");

                        //String address = c.getString("address");
                        //String gender = c.getString("gender");

                        // Source node is JSON Object
                        JSONObject source = c.getJSONObject("source");
                        String id = source.getString("id");


        /*                String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");
*/

                        // tmp hash map for single contact
                        HashMap<String, String> article = new HashMap<>();

                        // adding each child node to HashMap key => value
                        article.put("id", id);
                        article.put("author", author);
                        article.put("title", title);
                        article.put("url", url);
                        article.put("urlToImage", urlToImage);
                        article.put("publishedAt", publishedAt);
                        //contact.put("mobile", mobile);

                        // adding contact to contact list
                        articlesList.add(article);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
      /*      ListAdapter adapter = new SimpleAdapter(MainActivity.this, articlesList,
                    R.layout.list_item, new String[]{ "id","author","title"},
                    new int[]{R.id.id,R.id.author,R.id.title});

            lv.setAdapter(adapter);
       */

            ListNewsAdapter adapter = new ListNewsAdapter(MainActivity.this,articlesList);
            listNews.setAdapter(adapter);

            listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                    i.putExtra("url", articlesList.get(+position).get("url"));
                    startActivity(i);
                }
            });

        }

    }
}