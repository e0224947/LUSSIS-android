package com.sa45team7.lussis.ui.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.ui.detailsscreen.ScanQRActivity;
import com.sa45team7.lussis.rest.model.Employee;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnHomeFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        Employee employee = UserManager.getInstance().getCurrentEmployee();
        String role = employee.getJobTitle();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switch (role) {
            case "clerk":
                navigationView.inflateMenu(R.menu.clerk_drawer);
                break;
            case "supervisor":
                navigationView.inflateMenu(R.menu.supervisor_drawer);
                break;
            case "manager":
                navigationView.inflateMenu(R.menu.manager_drawer);
                break;
            case "staff":
                if (employee.isDelegated())
                    navigationView.inflateMenu(R.menu.staff_delegate_drawer);
                else
                    navigationView.inflateMenu(R.menu.staff_drawer);
                break;
            case "rep":
                navigationView.inflateMenu(R.menu.rep_drawer);
                break;
            case "head":
                navigationView.inflateMenu(R.menu.head_drawer);
                break;
            default:
                break;
        }

        View headerView = navigationView.getHeaderView(0);

        TextView nameText = headerView.findViewById(R.id.employee_name);
        nameText.setText(employee.getFullName());

        String displayRole = role.substring(0, 1).toUpperCase() + role.substring(1);
        if(UserManager.getInstance().getCurrentEmployee().isDelegated()){
            displayRole = displayRole.concat(" (delegated)");
        }
        TextView deptText = headerView.findViewById(R.id.employee_role);
        deptText.setText(displayRole);

        fragmentManager = getSupportFragmentManager();

        //To prevent lost save instance when change orientation
        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, homeFragment, homeFragment.getClass().toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //return to Home fragment, which has id of 0
            if (fragmentManager.findFragmentByTag(HomeFragment.class.toString()) == null) {
                MenuItem item = navigationView.getMenu().getItem(0);
                navigationView.setCheckedItem(item.getItemId());
                onNavigationItemSelected(item);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                if (homeFragment == null) homeFragment = new HomeFragment();
                displayFragment(homeFragment);
                break;
            case R.id.nav_pendingreq:
                displayFragment(new PendingReqFragment());
                break;
            case R.id.nav_delegate:
                displayFragment(new MyDelegateFragment());
                break;
            case R.id.nav_collection:
                displayFragment(new CollectionPointFragment());
                break;
            case R.id.nav_scan:
                Intent intent = new Intent(BaseActivity.this, ScanQRActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_newreq:
                displayFragment(new StationeriesFragment());
                break;
            case R.id.nav_myreq:
                displayFragment(new MyReqFragment());
                break;
            case R.id.nav_stationery:
                displayFragment(new StationeriesFragment());
                break;
            case R.id.nav_retrieve:
                displayFragment(new RetrievalListFragment());
                break;
            case R.id.nav_disbursement:
                displayFragment(new DisbursementsFragment());
                break;
            case R.id.nav_logout:
                UserManager.getInstance().clear();
                intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        //set title of activity base on the title on the menu
        toolbar.setTitle(item.getTitle());

        return true;
    }

    /**
     * Display fragment by replace the current one, if same fragment then do nothing
     * @param fragment fragment to change to
     */
    private void displayFragment(Fragment fragment) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (!fragment.getClass().toString().equals(currentFragment.getTag())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragment.getClass().toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    @Override
    public void onPanelIconClick(MenuItem item) {
        navigationView.setCheckedItem(item.getItemId());
        onNavigationItemSelected(item);
    }

}
