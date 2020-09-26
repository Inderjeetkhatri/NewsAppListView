package com.example.connectingtoapibasics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NextActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private ImageView image;
    ArrayList<HashMap<String, String>> articlesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        articlesList = new ArrayList<>();
        lv = findViewById(R.id.list);

        new GetArticle().execute();

    }

    private class GetArticle extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(NextActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String newsUrl = "https://newsapi.org/v2/top-headlines?country=in&apiKey=7c64e1b4cb01427e837f67cf94b921c3";
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

                        // Source node is JSON Object
                        JSONObject source = c.getJSONObject("source");
                        String id = source.getString("id");

                        HashMap<String, String> article = new HashMap<>();

                        // adding each child node to HashMap key => value
                        article.put("id", id);
                        article.put("author", author);
                        article.put("title", title);
                        article.put("url", url);
                        article.put("urlToImage", urlToImage);
                        article.put("publishedAt", publishedAt);
                        //article.put("imageUrl",imageUrl);
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
            ListAdapter adapter = new SimpleAdapter(NextActivity.this, articlesList,
                    R.layout.list_item, new String[]{ "id","author","title"},
                    new int[]{R.id.id,R.id.author,R.id.title});
            image=findViewById(R.id.image);
            Picasso.get().load("imageUrl").into(image);
            lv.setAdapter(adapter);
        }
    }
}