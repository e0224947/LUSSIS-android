package com.sa45team7.lussis.ui.mainscreen;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.ui.adapters.MyReqAdapter;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.utils.ErrorUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link Fragment} shows the requisitions history from current user
 */
public class MyReqFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView myReqListView;

    public MyReqFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_req, container, false);

        refreshLayout = view.findViewById(R.id.my_req_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyRequisitions();
            }
        });

        myReqListView = view.findViewById(R.id.my_req_list);

        getMyRequisitions();

        return view;
    }

    private void getMyRequisitions() {
        int empNum = UserManager.getInstance().getCurrentEmployee().getEmpNum();

        Call<List<Requisition>> call = LUSSISClient.getApiService().getMyRequisitions(empNum);
        call.enqueue(new Callback<List<Requisition>>() {
            @Override
            public void onResponse(@NonNull Call<List<Requisition>> call, @NonNull Response<List<Requisition>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyReqAdapter adapter = new MyReqAdapter(response.body());
                    myReqListView.setAdapter(adapter);
                    checkListEmpty();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Requisition>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void checkListEmpty() {
        boolean isEmpty = myReqListView.getAdapter().getItemCount() == 0;
        myReqListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
