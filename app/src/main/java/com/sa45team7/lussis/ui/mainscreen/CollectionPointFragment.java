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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.ui.detailsscreen.ScanQRActivity;
import com.sa45team7.lussis.ui.adapters.ReqDetailAdapter;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Disbursement;
import com.sa45team7.lussis.utils.DateConvertUtil;
import com.sa45team7.lussis.utils.ErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment shows the upcoming disbursement with the location and time
 */
public class CollectionPointFragment extends Fragment {

    private static final int REQUEST_SCAN = 6;
    private RecyclerView disDetailListView;
    private SwipeRefreshLayout refreshLayout;
    private TextView departmentText;
    private TextView dateText;
    private TextView timeText;
    private TextView collectionName;
    private View containerLayout;

    private Disbursement currentDisbursement;

    public CollectionPointFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_point, container, false);

        refreshLayout = view.findViewById(R.id.collection_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUpcomingCollection();
            }
        });

        containerLayout = view.findViewById(R.id.collection_container_layout);

        departmentText = view.findViewById(R.id.department_text);
        dateText = view.findViewById(R.id.date_text);
        timeText = view.findViewById(R.id.time_text);
        collectionName = view.findViewById(R.id.collection_text);
        disDetailListView = view.findViewById(R.id.dis_detail_list);

        Button acknowledgeButton = view.findViewById(R.id.scan_button);
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ScanQRActivity.class);
                startActivityForResult(intent, REQUEST_SCAN);
            }
        });

        getUpcomingCollection();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            currentDisbursement = null;
            checkCollectionEmpty();
        }

    }

    private void getUpcomingCollection() {
        String deptCode = UserManager.getInstance().getCurrentEmployee().getDeptCode();
        Call<Disbursement> call = LUSSISClient.getApiService().getUpcomingCollection(deptCode);

        call.enqueue(new Callback<Disbursement>() {
            @Override
            public void onResponse(@NonNull Call<Disbursement> call, @NonNull Response<Disbursement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDisbursement = response.body();

                    if (currentDisbursement != null) {
                        departmentText.setText(currentDisbursement.getDepartmentName());
                        String date = DateConvertUtil.convertForDetail(currentDisbursement.getCollectionDate());
                        dateText.setText(date);

                        timeText.setText(currentDisbursement.getCollectionTime());

                        collectionName.setText(currentDisbursement.getCollectionPoint());

                        ReqDetailAdapter adapter = new ReqDetailAdapter(currentDisbursement.getDisbursementDetails());
                        disDetailListView.setAdapter(adapter);
                    }

                } else {
                    if(response.code() == 404){
                        Toast.makeText(getContext(), "No upcoming collection", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = ErrorUtil.parseError(response).getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
                checkCollectionEmpty();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<Disbursement> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void checkCollectionEmpty() {
        containerLayout.setVisibility(currentDisbursement == null ? View.GONE : View.VISIBLE);
    }
}
