package com.sa45team7.lussis.ui.mainscreen;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Employee;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.rest.model.RequisitionDetail;
import com.sa45team7.lussis.rest.model.Stationery;
import com.sa45team7.lussis.ui.adapters.StationeryAdapter;
import com.sa45team7.lussis.ui.detailsscreen.StationeryDetailActivity;
import com.sa45team7.lussis.ui.dialogs.NewRequisitionDialog;
import com.sa45team7.lussis.utils.ErrorUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.sa45team7.lussis.ui.dialogs.NewRequisitionDialog.REQUEST_NEW_REQUISITION;

/**
 * {@link Fragment} shows the list of stationeries.
 */
public class StationeriesFragment extends Fragment implements StationeryAdapter.OnStationeryListInteractionListener {

    private Stationery selectedStationery;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView stationeryListView;
    private Spinner categorySpinner;

    private StationeryAdapter stationeryAdapter;

    public StationeriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stationeries, container, false);

        refreshLayout = view.findViewById(R.id.stationery_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getList();
            }
        });

        stationeryListView = view.findViewById(R.id.stationery_list);
        stationeryAdapter = new StationeryAdapter(new ArrayList<Stationery>(), this);
        stationeryListView.setAdapter(stationeryAdapter);

        categorySpinner = view.findViewById(R.id.category_spinner);
        categorySpinner.setSelection(0);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) categorySpinner.getAdapter().getItem(position);
                stationeryAdapter.getFilter().filter(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                stationeryAdapter.filterByDescription(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                stationeryAdapter.filterByDescription("");
                return false;
            }
        });

        getList();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_REQUISITION && resultCode == RESULT_OK) {
            Requisition requisition = new Requisition();
            requisition.setStatus("pending");
            requisition.setRequisitionEmpNum(UserManager.getInstance().getCurrentEmployee().getEmpNum());
            requisition.setRequisitionEmp(UserManager.getInstance().getCurrentEmployee());
            requisition.setRequestRemarks(data.getStringExtra("reason"));

            RequisitionDetail detail = new RequisitionDetail();
            detail.setItemNum(selectedStationery.getItemNum());
            detail.setQuantity(data.getIntExtra("quantity", 0));
            ArrayList<RequisitionDetail> list = new ArrayList<>();
            list.add(detail);

            requisition.setRequisitionDetails(list);

            makeRequest(requisition);
        }
    }

    @Override
    public void onSelectStationery(Stationery item) {
        selectedStationery = item;

        Employee employee = UserManager.getInstance().getCurrentEmployee();

        if (employee.getJobTitle().equals("staff") || employee.getJobTitle().equals("rep")) {
            NewRequisitionDialog dialog = new NewRequisitionDialog();

            Bundle bundle = new Bundle();
            bundle.putString("uom", item.getUnitOfMeasure());
            dialog.setArguments(bundle);
            dialog.setTargetFragment(this, REQUEST_NEW_REQUISITION);
            dialog.show(getFragmentManager(), "new_requisition_dialog");
        } else if (employee.getJobTitle().equals("clerk")) {
            Intent intent = new Intent(getContext(), StationeryDetailActivity.class);
            String data = new Gson().toJson(item);
            intent.putExtra("stationery", data);
            getContext().startActivity(intent);
        }

    }

    private void getList() {
        Call<List<Stationery>> call = LUSSISClient.getApiService().getAllStationeries();
        call.enqueue(new Callback<List<Stationery>>() {
            @Override
            public void onResponse(@NonNull Call<List<Stationery>> call, @NonNull Response<List<Stationery>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stationeryAdapter = new StationeryAdapter(response.body(), StationeriesFragment.this);
                    stationeryListView.setAdapter(stationeryAdapter);

                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Stationery>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void makeRequest(Requisition requisition) {
        Call<LUSSISResponse> call = LUSSISClient.getApiService().createNewRequisition(requisition);
        call.enqueue(new Callback<LUSSISResponse>() {
            @Override
            public void onResponse(@NonNull Call<LUSSISResponse> call, @NonNull Response<LUSSISResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(),
                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LUSSISResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
