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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Adjustment;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.rest.model.RetrievalItem;
import com.sa45team7.lussis.ui.adapters.RetrievalAdapter;
import com.sa45team7.lussis.ui.detailsscreen.StationeryDetailActivity;
import com.sa45team7.lussis.ui.dialogs.AdjustDialog;
import com.sa45team7.lussis.utils.ErrorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.sa45team7.lussis.ui.dialogs.AdjustDialog.REQUEST_ADJUST;

/**
 * {@link Fragment} shows the list of items to be retrieved.
 */
public class RetrievalListFragment extends Fragment
        implements RetrievalAdapter.OnRetrievalListInteractionListener {

    private String selectedItemNum;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView retrievalListView;
    private Spinner binNumSpinner;

    private RetrievalAdapter retrievalAdapter;

    public RetrievalListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retrieval_list, container, false);
        refreshLayout = view.findViewById(R.id.retrieval_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getList();
            }
        });

        retrievalListView = view.findViewById(R.id.retrieval_list);
        retrievalAdapter = new RetrievalAdapter(new ArrayList<RetrievalItem>(),
                RetrievalListFragment.this);
        retrievalListView.setAdapter(retrievalAdapter);

        binNumSpinner = view.findViewById(R.id.bin_num_spinner);
        binNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String binNum = (String) binNumSpinner.getAdapter().getItem(position);
                retrievalAdapter.getFilter().filter(binNum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                retrievalAdapter.filterByDescription(query);
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
                retrievalAdapter.filterByDescription("");
                return false;
            }
        });

        getList();

        return view;
    }

    private void getList() {
        Call<List<RetrievalItem>> call = LUSSISClient.getApiService().getRetrievalList();
        call.enqueue(new Callback<List<RetrievalItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<RetrievalItem>> call, @NonNull Response<List<RetrievalItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //populate retrieval list
                    retrievalAdapter = new RetrievalAdapter(response.body(),
                            RetrievalListFragment.this);
                    retrievalListView.setAdapter(retrievalAdapter);

                    //populate spinner
                    HashSet<String> bins = new HashSet<>();
                    for (RetrievalItem r : response.body()) {
                        bins.add(r.getBinNum());
                    }
                    ArrayList<String> binList = new ArrayList<>(bins);
                    Collections.sort(binList);
                    binList.add(0, "All");
                    ArrayAdapter<String> sAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, binList);
                    binNumSpinner.setAdapter(sAdapter);

                    checkListEmpty();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<RetrievalItem>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void adjustStock(Adjustment adjustment) {
        Call<LUSSISResponse> call = LUSSISClient.getApiService().stockAdjust(adjustment);
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

    @Override
    public void onSelectRetrievalItem(RetrievalItem item) {
        Intent intent = new Intent(getContext(), StationeryDetailActivity.class);
        intent.putExtra("itemNum", item.getItemNum());
        startActivity(intent);
    }

    @Override
    public void onSelectAdjust(RetrievalItem item) {
        selectedItemNum = item.getItemNum();

        AdjustDialog dialog = new AdjustDialog();
        dialog.setTargetFragment(this, REQUEST_ADJUST);
        dialog.show(getFragmentManager(), "adjust_dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADJUST && resultCode == RESULT_OK) {
            Adjustment adjustment = new Adjustment();
            adjustment.setItemNum(selectedItemNum);
            adjustment.setQuantity(data.getIntExtra("quantity", 0));
            adjustment.setReason(data.getStringExtra("reason"));
            adjustment.setRequestEmpNum(UserManager.getInstance().getCurrentEmployee().getEmpNum());
            adjustStock(adjustment);
        }
    }

    private void checkListEmpty() {
        //if list is empty, then show text
        boolean isEmpty = retrievalListView.getAdapter().getItemCount() == 0;
        retrievalListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

}
