package com.stp.volleydemo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.stp.volleydemo.Utils.ApplicationConstant;
import com.stp.volleydemo.Utils.Util;
import com.stp.volleydemo.model.BasicResponce;
import com.stp.volleydemo.model.InsideData;
import com.stp.volleydemo.network.NetworkResponseHelper;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    Map<String, String> params;
    String ApiType;
    protected RequestQueue mRequestQueue;
    List<String> areaRecsname = new ArrayList<>();
    List<InsideData> areaRecs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Util.isConnected(MainActivity.this)) {
            params = new HashMap<>();
            params.put("controller", "masterData");
            ApiType = "masterData";
            getSubmit(MainActivity.this);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }


    }

    private void getSubmit(Context mContext) {
        Log.e("Login", "@@URL" + ApplicationConstant.URL);
        mRequestQueue = Volley.newRequestQueue(mContext);
        NetworkResponseHelper<BasicResponce> myReq = new NetworkResponseHelper<>(
                Request.Method.POST,
                ApplicationConstant.URL,
                BasicResponce.class,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        myReq.setRetryPolicy(new DefaultRetryPolicy(
                ApplicationConstant.SOCKET_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }


    protected Response.ErrorListener createMyReqErrorListener() {
        return error -> {
//            if (mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
            Log.e("Not found data ", error.toString());
        };

    }

    private Response.Listener<BasicResponce> createMyReqSuccessListener() {
        return response -> {
            Log.d("Login Response", "@@@Ak response" + response);
            try {
                if (response.getSuccess().equals(ApplicationConstant.RESPONSE_SUCCESS)) {
                    Log.d("@@AK ApiType", ApiType);
                    if (ApiType.equals("masterData")) {
                        areaRecs.clear();
                        areaRecsname.clear();
                        areaRecs.addAll(response.getDropdown().getAreaRecs());
                        for (InsideData insideData : response.getDropdown().getAreaRecs()) {
                            areaRecsname.add(insideData.getArea_name());
                        }

                        listView = findViewById(R.id.list);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, areaRecsname);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}