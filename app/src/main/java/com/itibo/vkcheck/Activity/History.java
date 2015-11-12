package com.itibo.vkcheck.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itibo.vkcheck.Activity.adapter.HistoryAdapter;
import com.itibo.vkcheck.Activity.api.Hostinger;
import com.itibo.vkcheck.Activity.models.HostingerModel;
import com.itibo.vkcheck.Activity.models.PostModel.HistoryModel;
import com.itibo.vkcheck.Activity.models.SearchModel;
import com.itibo.vkcheck.R;

import java.util.ArrayList;

/**
 * Created by erick on 22.10.15.
 */
public class History extends AppCompatActivity {
    Intent intent;
    SearchModel searchModel;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    TextView historyError;

    Button fixHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        intent = getIntent();

        searchModel = (SearchModel) intent.getSerializableExtra("user");

        setToolbar();
        setButton();
        setTextView();
        setProgressBar();
        setRecycler();
    }

    private void setButton() {
        fixHistoryButton = (Button) findViewById(R.id.fixHistoryButton);
        fixHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hostinger.asyncAddUser(String.valueOf(searchModel.getUid()), new Hostinger.CallbackResponse() {
                    @Override
                    public void onSuccess(HostingerModel model) {
                        finish();
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });
    }

    private void setTextView() {
        historyError = (TextView) findViewById(R.id.historyError);

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.history_toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            setTitle(searchModel.getFirst_name() + " " + searchModel.getLast_name());
        }
    }

    private void setRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.historyRecycler);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);


        Hostinger.asyncGetUser(String.valueOf(searchModel.getUid()), new Hostinger.CallbackResponse() {
            @Override
            public void onSuccess(HostingerModel model) {
                if (model.getCode().equals("201")) {
                    Hostinger.asyncGetHistory(String.valueOf(searchModel.getUid()), new Hostinger.CallbackHistory() {
                        @Override
                        public void onSuccess(ArrayList<HistoryModel> model) {
                            HistoryAdapter historyAdapter = new HistoryAdapter(model, History.this);
                            if(historyAdapter.getItemCount() == 0) {
                                historyError.setText(getResources().getString(R.string.historyEmpty));
                            }
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(historyAdapter);
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                } else {
                    historyError.setText(getResources().getString(R.string.errorHistoryEmpty));
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    fixHistoryButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {

            }
        });

    }

    private void setProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
