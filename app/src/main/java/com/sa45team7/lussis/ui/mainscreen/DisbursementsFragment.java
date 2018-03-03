package com.sa45team7.lussis.ui.mainscreen;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.ui.detailsscreen.GenerateQRActivity;
import com.sa45team7.lussis.ui.adapters.DisbursementAdapter;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Disbursement;
import com.sa45team7.lussis.utils.ErrorUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment shows the list of upcoming disbursements
 */
public class DisbursementsFragment extends Fragment implements DisbursementAdapter.OnDisbursementListInteractionListener {

    private static final int REQUEST_GENERATE = 7;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView disbursementListView;

    public DisbursementsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disbursements, container, false);

        refreshLayout = view.findViewById(R.id.disbursement_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getList();
            }
        });

        disbursementListView = view.findViewById(R.id.disbursement_list);

        getList();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GENERATE) {
            getList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getList() {
        Call<List<Disbursement>> call = LUSSISClient.getApiService().getDisbursements();
        call.enqueue(new Callback<List<Disbursement>>() {
            @Override
            public void onResponse(@NonNull Call<List<Disbursement>> call, @NonNull Response<List<Disbursement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DisbursementAdapter adapter = new DisbursementAdapter(response.body(),
                            DisbursementsFragment.this);

                    disbursementListView.setAdapter(adapter);
                    checkListEmpty();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Disbursement>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void checkListEmpty() {
        boolean isEmpty = disbursementListView.getAdapter().getItemCount() == 0;
        disbursementListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSelectDisbursement(int position, Disbursement item) {
        Intent intent = new Intent(getContext(), GenerateQRActivity.class);
        String data = new Gson().toJson(item);
        intent.putExtra("disbursement", data);
        startActivityForResult(intent, REQUEST_GENERATE);
    }
}
