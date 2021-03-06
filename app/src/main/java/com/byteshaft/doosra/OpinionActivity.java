package com.byteshaft.doosra;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.doosra.accounts.AccountManager;
import com.byteshaft.doosra.gettersetter.DiagnosisOpinion;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class OpinionActivity extends AppCompatActivity {

    private TextView motoLineOne;
    private TextView motoLineTwo;
    private ListView listView;

    private ArrayList<DiagnosisOpinion> opinionArrayList;
    private OpinionAdapter opinionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);
        motoLineOne = (TextView) findViewById(R.id.text_line_one);
        motoLineTwo = (TextView) findViewById(R.id.text_line_two);
        listView = (ListView) findViewById(R.id.opinion_list);

        opinionArrayList = new ArrayList<>();

        // set typeface
        motoLineOne.setTypeface(AppGlobals.typeface);
        motoLineTwo.setTypeface(AppGlobals.typeface);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (AppGlobals.isLogin()) {
                    DiagnosisOpinion opinion = opinionArrayList.get(position);
                    Intent intent = new Intent(OpinionActivity.this, UserProfile.class);
                    intent.putExtra("id", opinion.getId());
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OpinionActivity.this);
                    alertDialogBuilder.setTitle("Login Required!");
                    alertDialogBuilder.setMessage("Please login to proceed further\nDo you want to login?")
                            .setCancelable(false).setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(OpinionActivity.this, AccountManager.class));
                                }
                            });
                    alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
        getOpinions();

    }


    private void getOpinions() {
        HttpRequest getStateRequest = new HttpRequest(getApplicationContext());
        getStateRequest.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        System.out.println("Test " + jsonArray.getJSONObject(i));
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        DiagnosisOpinion diagnosisOpinion = new DiagnosisOpinion();
                                        diagnosisOpinion.setId(jsonObject.getInt("id"));
                                        diagnosisOpinion.setOpinionName(jsonObject.getString("named_id"));
                                        opinionArrayList.add(diagnosisOpinion);
                                    }
                                    opinionAdapter = new OpinionAdapter(opinionArrayList);
                                    listView.setAdapter(opinionAdapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%sopinion-types", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    /// adapter

    class OpinionAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<DiagnosisOpinion> opinions;

        public OpinionAdapter(ArrayList<DiagnosisOpinion> opinions) {
            this.opinions = opinions;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_opinion_button, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.button = convertView.findViewById(R.id.opinion_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            DiagnosisOpinion opinion = opinions.get(position);
            viewHolder.button.setText(opinion.getOpinionName());
            viewHolder.button.setTypeface(AppGlobals.typeface);
            return convertView;
        }

        @Override
        public int getCount() {
            return opinions.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    // view holder

    class ViewHolder {
        public TextView button;
    }
}

