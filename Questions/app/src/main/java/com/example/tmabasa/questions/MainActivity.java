package com.example.tmabasa.questions;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<HashMap<String,String>> listData;

    ListView listview;
    TextView answerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listData = new ArrayList<>();


        listview = (ListView)findViewById(R.id.list_view);
         answerCount = (TextView)findViewById(R.id.label);

        //Invoking StackOverFlowQuestions
         new StackOverflowQuestion().execute();
    }

    private class StackOverflowQuestion extends AsyncTask<Void, Void, String>
    {
        //Class variables Initialization
        private String StackOverflowWebApi = "https://api.stackexchange.com/2.2/questions?pagesize=50&tagged=android&site=stackoverflow";

        private BufferedReader bufferedReader = null;
        private StringBuilder stringBuilder = null;
        private String line;

        private URL stackAPi_URL = null;
        private HttpURLConnection urlConnection = null;


        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                stackAPi_URL = new URL(StackOverflowWebApi);

                urlConnection = (HttpURLConnection)stackAPi_URL.openConnection();

                try
                {
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    stringBuilder = new StringBuilder();


                    while ((line = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    return stringBuilder.toString();

                }finally
                {
                    // urlConnection.disconnect();
                }
            }
            catch (Exception e)
            {
                return null;
            }
        }

        public void onPostExecute(String respond)
        {
            if(respond != null)
            {
                try
                {
                    //JSONObject for storing returned json data
                    JSONObject object = new JSONObject(respond);
                    JSONArray jArray = object.getJSONArray("items");

                    //populating our listview with data from json
                    for(int i = 0; i < jArray.length(); i++)
                    {
                        JSONObject json_data = jArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();
                        String answerTxt = json_data.get("answer_count").toString();
                        String titleTxt = json_data.get("title").toString();
                        String arr = json_data.get("tags").toString().replace("[","").replace("]","").replace("\"","").replace(",", ",  ");
                        String date = json_data.get("creation_date").toString();
                        boolean acceptedAnswer = json_data.isNull("accepted_answer_id");

                        //time formatting
                        Calendar calendar = Calendar.getInstance();
                        Long longTime = Long.parseLong(date);
                        calendar.setTimeInMillis(longTime);

                        DateFormat formatter = new SimpleDateFormat("HH:MM:SS");
                        String dateFormatted = formatter.format(calendar.getTime());

                        //changing answer background to green when answer is accepted
                       if (acceptedAnswer == false)
                        {
                          //  answerCount.setBackgroundResource(R.drawable.circle);
                        }

                        //Inserting retrieved data into hashmap
                        map.put("answer",answerTxt);
                        map.put("titleText", titleTxt);
                        map.put("tags",arr);
                        map.put("dateText", dateFormatted);

                        listData.add(map);
                    }

                    //our list adapter data
                    ListAdapter Listadapter = new SimpleAdapter(MainActivity.this,listData,R.layout.activity_listview,
                            new String[]{"answer", "titleText", "tags", "dateText"},
                            new int[]{R.id.label, R.id.titleText, R.id.tag1,R.id.editText});

                    listview.setAdapter(Listadapter);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

        }

    }

}
