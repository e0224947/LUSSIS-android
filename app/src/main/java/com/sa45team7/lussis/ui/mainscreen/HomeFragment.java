package com.sa45team7.lussis.ui.mainscreen;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.ui.adapters.HomeAdapter;

import java.util.ArrayList;

/**
 * A {@link Fragment} shows the list of features available as a grid view
 * Activities that contain this fragment must implement the
 * {@link OnHomeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {

    private OnHomeFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        GridView gridView = view.findViewById(R.id.gridview);

        //Add menu items to fit 6 grid items
        Menu menu = ((NavigationView) getActivity().findViewById(R.id.nav_view)).getMenu();
        ArrayList<MenuItem> list = new ArrayList<>();
        for (int i = 1; i < menu.size() - 1; i++) {
            list.add(menu.getItem(i));
        }
        for (int j = menu.size(); j <= 6; j++) {
            list.add(null);
        }
        list.add(menu.getItem(menu.size() - 1));

        final HomeAdapter adapter = new HomeAdapter(getContext(), R.layout.grid_item, list);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem item = adapter.getItem(position);
                if (mListener != null) {
                    mListener.onPanelIconClick(item);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHomeFragmentInteractionListener {

        void onPanelIconClick(MenuItem item);
    }
}
