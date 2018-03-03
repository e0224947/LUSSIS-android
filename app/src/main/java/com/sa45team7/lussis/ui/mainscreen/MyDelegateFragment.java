package com.sa45team7.lussis.ui.mainscreen;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Delegate;
import com.sa45team7.lussis.rest.model.Employee;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.utils.DateConvertUtil;
import com.sa45team7.lussis.utils.ErrorUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.currentTimeMillis;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDelegateFragment extends Fragment {

    private String deptCode;
    private SwipeRefreshLayout refreshLayout;
    private AutoCompleteTextView empNameView;
    private TextInputLayout startDateLayout;
    private TextInputLayout endDateLayout;
    private TextInputEditText startDateView;
    private TextInputEditText endDateView;
    private Employee chosenEmployee;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private Button assignButton;
    private Button revokeButton;

    private List<Employee> employees = new ArrayList<>();

    private Delegate currentDelegate;

    public MyDelegateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        deptCode = UserManager.getInstance().getCurrentEmployee().getDeptCode();

        View view = inflater.inflate(R.layout.fragment_my_delegate, container, false);

        refreshLayout = view.findViewById(R.id.delegate_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDelegate();
            }
        });

        empNameView = view.findViewById(R.id.employee_name_auto_complete);
        empNameView.setThreshold(0);
        fetchEmployeeList();

        startDateLayout = view.findViewById(R.id.start_date_layout);
        endDateLayout = view.findViewById(R.id.end_date_layout);

        startDateView = view.findViewById(R.id.start_date_text);
        endDateView = view.findViewById(R.id.end_date_text);
        setUpDatePicker(startDateView, startCalendar);
        setUpDatePicker(endDateView, endCalendar);

        assignButton = view.findViewById(R.id.assign_new_button);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid())
                    assignNewDelegate();
            }
        });

        revokeButton = view.findViewById(R.id.revoke_button);
        revokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeDelegate();
            }
        });

        getDelegate();
        return view;
    }

    private boolean isValid() {
        boolean isValid = true;

        empNameView.setError(null);
        startDateLayout.setError(null);
        endDateLayout.setError(null);

        if (empNameView.getText().toString().isEmpty()) {
            empNameView.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (chosenEmployee == null) {
            //Double check if employee name is in the employee list
            String name = empNameView.getText().toString().toLowerCase();
            for (Employee e: employees){
                if(e.getFullName().toLowerCase().equals(name)){
                    chosenEmployee = e;
                    break;
                }
            }
            if(chosenEmployee == null){
                empNameView.setError("Wrong employee name.");
                isValid = false;
            }
        }

        if (startCalendar.after(endCalendar)) {
            startDateLayout.setError("Start date must be before end date.");
            endDateLayout.setError("End date must be after start date.");
            isValid = false;
        }

        return isValid;
    }

    private void setUpDatePicker(final EditText dateView, final Calendar calendar) {
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateView.setText(DateConvertUtil.convertForDetail(calendar.getTime()));
            }

        };

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), dateListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(currentTimeMillis());
                dialog.show();
            }
        });
    }

    private void getDelegate() {
        Call<Delegate> call = LUSSISClient.getApiService().getDelegate(deptCode);

        call.enqueue(new Callback<Delegate>() {
            @Override
            public void onResponse(@NonNull Call<Delegate> call, @NonNull Response<Delegate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDelegate = response.body();
                    empNameView.setText(currentDelegate.getEmployee().getFullName());

                    String startDate = DateConvertUtil.convertForDetail(currentDelegate.getStartDate());
                    startDateView.setText(startDate);

                    String endDate = DateConvertUtil.convertForDetail(currentDelegate.getEndDate());
                    endDateView.setText(endDate);

                } else {
                    currentDelegate = null;
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                showButtons();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<Delegate> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchEmployeeList() {
        Call<List<Employee>> call = LUSSISClient.getApiService()
                .getEmployeeListForDelegate(deptCode);

        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(@NonNull Call<List<Employee>> call, @NonNull Response<List<Employee>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    employees = response.body();

                    ArrayAdapter<Employee> adapter = new ArrayAdapter<Employee>(getContext(),
                            android.R.layout.simple_dropdown_item_1line, employees);

                    empNameView.setAdapter(adapter);

                    empNameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            chosenEmployee = (Employee) parent.getItemAtPosition(position);
//                            empNameView.setText(chosenEmployee.getFullName());
                        }
                    });

                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Employee>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void assignNewDelegate() {
        updateCurrentDelegate();
        int empNum = currentDelegate.getEmployee().getEmpNum();
        Call<Delegate> call = LUSSISClient.getApiService().postDelegate(empNum, currentDelegate);
        call.enqueue(new Callback<Delegate>() {
            @Override
            public void onResponse(@NonNull Call<Delegate> call, @NonNull Response<Delegate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDelegate = response.body();
                    Toast.makeText(getContext(), "Added new delegate", Toast.LENGTH_SHORT).show();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }

                showButtons();
            }

            @Override
            public void onFailure(@NonNull Call<Delegate> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void revokeDelegate() {
        Call<LUSSISResponse> call = LUSSISClient.getApiService().deleteDelegate(currentDelegate);
        call.enqueue(new Callback<LUSSISResponse>() {
            @Override
            public void onResponse(@NonNull Call<LUSSISResponse> call, @NonNull Response<LUSSISResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    currentDelegate = null;

                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                showButtons();
            }

            @Override
            public void onFailure(@NonNull Call<LUSSISResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateCurrentDelegate() {
        if (currentDelegate == null) currentDelegate = new Delegate();
        currentDelegate.setEmployee(chosenEmployee);
        currentDelegate.setStartDate(startCalendar.getTime());
        currentDelegate.setEndDate(endCalendar.getTime());
    }

    private void showButtons() {
        revokeButton.setVisibility(currentDelegate == null ? View.GONE : View.VISIBLE);
        empNameView.setEnabled(currentDelegate == null);
        startDateView.setEnabled(currentDelegate == null);
        endDateView.setEnabled(currentDelegate == null);
    }
}
