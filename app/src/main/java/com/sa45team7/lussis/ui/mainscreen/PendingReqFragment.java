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
import com.sa45team7.lussis.ui.detailsscreen.PendingReqDetailActivity;
import com.sa45team7.lussis.ui.adapters.PendingReqAdapter;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.ui.dialogs.ConfirmDialog;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.utils.ErrorUtil;
import com.sa45team7.lussis.utils.InternetConnection;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.sa45team7.lussis.ui.dialogs.ConfirmDialog.REQUEST_CONFIRM;

/**
 * {@link Fragment} shows the pending requisitions need to be approved
 */
public class PendingReqFragment extends Fragment
        implements PendingReqAdapter.OnPendingReqListInteractionListener {

    public static final int REQUEST_PROCESS = 5;

    private int selectedReqPosition = -1;
    private Requisition selectedReq;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView pendingReqListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PendingReqFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_req, container, false);

        refreshLayout = view.findViewById(R.id.pending_refresh_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequisitionsList();
            }
        });

        pendingReqListView = view.findViewById(R.id.pending_req_list);

        getRequisitionsList();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PROCESS && resultCode == RESULT_OK) {
            ((PendingReqAdapter) pendingReqListView.getAdapter()).removeItem(selectedReqPosition);
        } else if (requestCode == REQUEST_CONFIRM) {
            if (resultCode == RESULT_OK) {
                selectedReq.setApprovalRemarks(data.getStringExtra("reason"));
                processRequisition();
            } else {
                selectedReq = null;
                selectedReqPosition = -1;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectedReq = null;
    }

    private void getRequisitionsList() {
        if (InternetConnection.checkConnection(getContext())) {
            String deptCode = UserManager.getInstance().getCurrentEmployee().getDeptCode();
            Call<List<Requisition>> call = LUSSISClient.getApiService().getPendingRequisitions(deptCode);
            call.enqueue(new Callback<List<Requisition>>() {
                @Override
                public void onResponse(@NonNull Call<List<Requisition>> call, @NonNull Response<List<Requisition>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        pendingReqListView.setAdapter(new PendingReqAdapter(response.body(), PendingReqFragment.this));
                        checkListEmpty();
                    } else {
                        String error = ErrorUtil.parseError(response).getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull Call<List<Requisition>> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(),
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void processRequisition() {
        Call<LUSSISResponse> call = LUSSISClient.getApiService().processRequisition(selectedReq);
        call.enqueue(new Callback<LUSSISResponse>() {
            @Override
            public void onResponse(@NonNull Call<LUSSISResponse> call, @NonNull Response<LUSSISResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    ((PendingReqAdapter) pendingReqListView.getAdapter()).removeItem(selectedReqPosition);
                    checkListEmpty();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LUSSISResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSelectRequisition(int position, Requisition item) {
        selectedReqPosition = position;

        Intent intent = new Intent(getContext(), PendingReqDetailActivity.class);
        String data = new Gson().toJson(item);
        intent.putExtra("requisition", data);
        startActivityForResult(intent, REQUEST_PROCESS);
    }

    @Override
    public void onProcessRequisition(int position, String status, Requisition item) {
        selectedReqPosition = position;
        selectedReq = item;
        selectedReq.setStatus(status);
        selectedReq.setRequisitionEmpNum(selectedReq.getRequisitionEmp().getEmpNum());
        selectedReq.setApprovalEmpNum(UserManager.getInstance().getCurrentEmployee().getEmpNum());
        selectedReq.setApprovalEmp(UserManager.getInstance().getCurrentEmployee());

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTargetFragment(this, REQUEST_CONFIRM);
        dialog.show(getFragmentManager(), "confirm_dialog");

    }

    private void checkListEmpty() {
        boolean isEmpty = pendingReqListView.getAdapter().getItemCount() == 0;
        pendingReqListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
