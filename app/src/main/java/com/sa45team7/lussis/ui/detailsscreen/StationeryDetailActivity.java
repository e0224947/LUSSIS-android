package com.sa45team7.lussis.ui.detailsscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Adjustment;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.rest.model.Stationery;
import com.sa45team7.lussis.ui.dialogs.AdjustDialog;
import com.sa45team7.lussis.utils.ErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sa45team7.lussis.ui.dialogs.AdjustDialog.REQUEST_ADJUST;

public class StationeryDetailActivity extends AppCompatActivity {

    private Stationery stationery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationery_detail);

        Button adjustButton = findViewById(R.id.adjustButton);

        adjustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdjustDialog dialog = new AdjustDialog();
                dialog.show(getSupportFragmentManager(), "adjust_dialog");
            }
        });

        if (getIntent().hasExtra("stationery")) {
            String data = getIntent().getStringExtra("stationery");
            stationery = new Gson().fromJson(data, Stationery.class);
            populateFields();
        } else {
            fetchStationery();
        }
    }

    private void fetchStationery() {
        String itemNum = getIntent().getStringExtra("itemNum");
        Call<Stationery> call = LUSSISClient.getApiService().getStationery(itemNum);
        call.enqueue((new Callback<Stationery>() {
            @Override
            public void onResponse(@NonNull Call<Stationery> call, @NonNull Response<Stationery> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stationery = response.body();
                    populateFields();

                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Stationery> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }

    private void adjustStock(Adjustment adjustment) {
        Call<LUSSISResponse> call = LUSSISClient.getApiService().stockAdjust(adjustment);
        call.enqueue(new Callback<LUSSISResponse>() {
            @Override
            public void onResponse(@NonNull Call<LUSSISResponse> call, @NonNull Response<LUSSISResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(StationeryDetailActivity.this,
                            response.body().getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(StationeryDetailActivity.this,
                            error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LUSSISResponse> call, @NonNull Throwable t) {
                Toast.makeText(StationeryDetailActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields() {
        ((EditText) findViewById(R.id.itemNum)).setText(stationery.getItemNum());
        ((EditText) findViewById(R.id.itemDesc)).setText(stationery.getDescription());
        ((EditText) findViewById(R.id.itemBin)).setText(stationery.getBinNum());

        String qty = String.valueOf(stationery.getAvailableQty());
        ((EditText) findViewById(R.id.itemQty)).setText(qty);

        String reorderQty = String.valueOf(stationery.getReorderQty());
        ((EditText) findViewById(R.id.itemReorderQty)).setText(reorderQty);

        String reorderLevel = String.valueOf(stationery.getReorderLevel());
        ((EditText) findViewById(R.id.itemReorderLvl)).setText(reorderLevel);

        Spinner categorySpinner = findViewById(R.id.itemCategory);
        categorySpinner.setSelection(getIndex(categorySpinner, stationery.getCategory()));
        categorySpinner.setEnabled(false);

        Spinner uomSpinner = findViewById(R.id.itemUnitOfMeasure);
        uomSpinner.setSelection(getIndex(uomSpinner, stationery.getUnitOfMeasure()));
        uomSpinner.setEnabled(false);
    }

    //helper for spinner
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADJUST && resultCode == RESULT_OK) {
            Adjustment adjustment = new Adjustment();
            adjustment.setItemNum(stationery.getItemNum());
            adjustment.setQuantity(data.getIntExtra("quantity", 0));
            adjustment.setReason(data.getStringExtra("reason"));
            adjustment.setRequestEmpNum(UserManager.getInstance().getCurrentEmployee().getEmpNum());
            adjustStock(adjustment);
        }
    }
}
