package com.gigatech.trackerpassenger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gigatech.trackerpassenger.models.RouteResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RouteActivity extends AppCompatActivity {

    public static final String ROUTE_DATA = "route_data";
    private ListView routeList;
    private ArrayAdapter<String> rAdapter;

    private List<String> routes;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.action_bar, null);
            actionBar.setCustomView(v);
        }

        routes = new ArrayList<>();
        routeList = (ListView) findViewById(R.id.route_list);

        rAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routes);
        routeList.setAdapter(rAdapter);
        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String r = routes.get(i);
                if (r.equals(getString(R.string.all_near_me))) {
                    r = "";
                }
                setResultAndExit(r);
            }
        });

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RetrofitService.EasyCallback<RouteResponse> routeCallback = new RetrofitService.EasyCallback<RouteResponse>() {
            @Override
            public void onSuccess(RouteResponse response) {
                rAdapter.clear();
                rAdapter.add(getString(R.string.all_near_me));
                for (String r : response.routes) {
                    rAdapter.add(r);
                }
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                rAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int responseCode, String responseString, Throwable t) {
                showRouteError();
            }
        };

        Call<RouteResponse> call = ApiService.get().getRoutes();
        call.enqueue(routeCallback);
    }

    private void showRouteError() {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        mDialog = MainApplication.getDialogWithPositiveButton(RouteActivity.this, getString(R.string.error_in_fetching_routes), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mDialog.show();
    }

    private void setResultAndExit(String res) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ROUTE_DATA, res);
        setResult(Activity.RESULT_OK, resultIntent);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }
}
