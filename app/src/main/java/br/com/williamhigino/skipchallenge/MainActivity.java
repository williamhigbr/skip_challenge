package br.com.williamhigino.skipchallenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import static br.com.williamhigino.skipchallenge.PersistentDataManager.CURRENT_CHART;
import static br.com.williamhigino.skipchallenge.PersistentDataManager.CURRENT_CUSTOMER;


public class MainActivity extends AppCompatActivity {

    private String productsTAG = "productsBrowserFragment";
    private String chartTAG = "chartFragment";

    private Activity mActivity;

    private PersistentDataManager persistentDataManager;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private void SwitchFragment(int id)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String curFragTAG = null;
        Fragment newFrag = null;

        //hides all fragments other than id
        if(id != R.id.nav_products_browser && fragmentManager.findFragmentByTag(productsTAG) != null)
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(productsTAG)).commit();
        if(id != R.id.nav_chart && fragmentManager.findFragmentByTag(chartTAG) != null)
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(chartTAG)).commit();

        switch(id) {
            case R.id.nav_products_browser:
                curFragTAG = productsTAG;
                newFrag = new ProductsFragment();
                navigationView.setCheckedItem(R.id.nav_products_browser);
                break;
            case R.id.nav_chart:
                curFragTAG = chartTAG;
                newFrag = new ChartFragment();
                navigationView.setCheckedItem(R.id.nav_chart);
                break;
        }

        if(fragmentManager.findFragmentByTag(curFragTAG) != null) {
            //if the fragment exists, show it.
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(curFragTAG)).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            fragmentManager.beginTransaction().add(R.id.fragment_view, newFrag, curFragTAG).commit();
        }

        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        persistentDataManager = PersistentDataManager.getInstance(mActivity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        SwitchFragment(R.id.nav_products_browser);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int itemId = item.getItemId();

            if(itemId == R.id.nav_logout) {
                persistentDataManager.SaveModel(null, CURRENT_CUSTOMER);
                persistentDataManager.SaveModel(null, CURRENT_CHART);
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
                finish();
            }
            else {
                SwitchFragment(itemId);
            }

            return true;
        }
    };

}
