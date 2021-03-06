package com.byteshaft.doosra;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.byteshaft.doosra.accounts.AccountManager;
import com.byteshaft.doosra.accounts.EditProfile;
import com.byteshaft.doosra.fragments.Dashboard;
import com.byteshaft.doosra.fragments.History;
import com.byteshaft.doosra.fragments.MyProfile;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity sInstance;

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AccountManager.getInstance() != null) {
            AccountManager.getInstance().finish();
        }
//        if (!AppGlobals.isLogin()) {
//            startActivity(new Intent(MainActivity.this, AccountManager.class));
//        }
        sInstance = this;
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View headerView;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.name);
        TextView email = headerView.findViewById(R.id.email);

        CircleImageView kitchenImage = headerView.findViewById(R.id.nav_imageView);
        name.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FIRST_NAME) + " " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LAST_NAME));
        email.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));

        if (AppGlobals.isLogin() && AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE) != null) {
            String url = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE);
            Helpers.getBitMap(url, kitchenImage);
        }
        loadFragment(new Dashboard());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragment(new Dashboard());
        } else if (id == R.id.nav_my_profile) {
            if (AppGlobals.isLogin()) {
                loadFragment(new MyProfile());
            } else {
                loginRequiredDialog();
            }
        } else if (id == R.id.nav_history) {
            if (AppGlobals.isLogin()) {
                loadFragment(new History());
            } else {
                loginRequiredDialog();
            }
        } else if (id == R.id.nav_edit_save) {
            if (AppGlobals.isLogin()) {
                loadFragment(new EditProfile());
            } else {
                loginRequiredDialog();
            }

        } else if (id == R.id.nav_logout) {
            if (AppGlobals.isLogin()) {
                logOutDialog();
            } else {
                loginRequiredDialog();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("Do you really want to logout?")
                .setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppGlobals.clearSettings();
                        AppGlobals.firstTimeLaunch(true);
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment_container, fragment);
        tx.commit();
    }

    private void loginRequiredDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Login Required!");
        alertDialogBuilder.setMessage("You are not Logged in. Do you want to Login?")
                .setCancelable(false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(MainActivity.this, AccountManager.class));
                    }
                });
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
