package com.example.ettdemoproject.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ettdemoproject.Fragments.HomeFragments.MainFragment;
import com.example.ettdemoproject.Fragments.NotificationsFragment;
import com.example.ettdemoproject.Fragments.ProfileFragment;
import com.example.ettdemoproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.branch.referral.Branch;


/**
 * @author : Afaf Hanbali
 * Created on 2020-Oct-5
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BRANCH SDK";
    public static final String APP_TITLE = "HOME";
    public static final String DATABASE_NAME = "name";
    public static final String DATABASE_EMAIL = "email";
    public static final String LOGOUT_TITLE = "Logout";
    public static final String LOGOUT_MSG = "Do you really want to logout?";


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Users");
    private FirebaseUser firebaseUser;

    @BindView(R.id.toolbar)
    Toolbar mainToolBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_drawer_view)
    NavigationView navigationView;
    @BindView(R.id.fabLayout_user)
    LinearLayout userFabLayout;
    @BindView(R.id.fabLayout_album)
    LinearLayout albumFabLayout;
    @BindView(R.id.fabLayout_post)
    LinearLayout postFabLayout;
    @BindView(R.id.fab_add)
    FloatingActionButton addFab;
    @BindView(R.id.fab_user)
    FloatingActionButton userFab;
    @BindView(R.id.fab_album)
    FloatingActionButton albumFab;
    @BindView(R.id.fab_post)
    FloatingActionButton postFab;

    TextView navHeaderName;
    TextView navHeaderEmail;

    private ActionBarDrawerToggle toggle;

    Fragment oldFragment;

    private MainFragment mainFragment = new MainFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private NotificationsFragment notificationsFragment = new NotificationsFragment();
    private String token;
    private boolean isFABOpen = false;

    private Branch.BranchReferralInitListener branchReferralInitListener;
    private int id = -1;
    private String type = null;
    private int rowPosition;
    private boolean isLinkClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle(APP_TITLE);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

        setFromDbFields();

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        setDefaultFragment();

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });


        /*
        branchReferralInitListener = new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject linkProperties, BranchError error) {
                if (error == null) {
                    try {
                        id = linkProperties.getInt("$canonical_identifier");
                        type = linkProperties.getString("$og_description");
                        if (type.equals(TYPE_ALBUM)) {
                            viewPager.setCurrentItem(0);
                            bottomNavigationMenuView.setSelectedItemId(R.id.page_albums);
                            isLinkClicked = true;
                            rowPosition = id - 1;
                            pagerAdapter.createFragment(0);
                            //albumsFragment.setPosition(id - 1);
                        } else if (type.equals(TYPE_USER)) {
                            viewPager.setCurrentItem(1);
                            bottomNavigationMenuView.setSelectedItemId(R.id.page_users);
                            isLinkClicked = true;
                            rowPosition = id - 1;
                            //  usersFragment.setPosition(id - 1);
                        } else if (type.equals(TYPE_POST)) {
                            viewPager.setCurrentItem(2);
                            bottomNavigationMenuView.setSelectedItemId(R.id.page_posts);
                            isLinkClicked = true;
                            rowPosition = id - 1;
                            //postsFragment.setPosition(id - 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, error.getMessage());
                }
            }
        };*/

    }


    private void setFromDbFields() {
        firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    navHeaderName.setText(dataSnapshot.child(userId).child(DATABASE_NAME).getValue(String.class));
                    navHeaderEmail.setText(dataSnapshot.child(userId).child(DATABASE_EMAIL).getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        // Log
                        Log.d(TAG, getString(R.string.token_msg) + token);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }


    private void setDefaultFragment() {
        Fragment fragment = mainFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_drawer_fragment, fragment).commit();
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_home);
        item.setCheckable(true);
        item.setChecked(true);
        oldFragment = fragment;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean isTransaction = true;
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = mainFragment;
                break;
            case R.id.nav_profile:
                fragment = profileFragment;
                break;
            case R.id.nav_notifications:
                fragment = notificationsFragment;
                break;
            case R.id.nav_logout:
                isTransaction = false;
                logOut();
                break;

        }
        if (isTransaction) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_drawer_fragment, fragment).commit();
            item.setCheckable(true);
            getSupportActionBar().setTitle(item.getTitle());
            drawerLayout.closeDrawers();
        }
        return true;
    }

    private void logOut() {
        new AlertDialog.Builder(this)
                .setTitle(LOGOUT_TITLE)
                .setMessage(LOGOUT_MSG)
                .setIcon(R.drawable.logout_icon)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null).show();

    }


    private void showFABMenu() {
        isFABOpen = true;
        userFabLayout.setVisibility(View.VISIBLE);
        albumFabLayout.setVisibility(View.VISIBLE);
        postFabLayout.setVisibility(View.VISIBLE);
        addFab.animate().rotationBy(180);
        userFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        albumFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_110));
        postFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_160));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        addFab.animate().rotation(0);
        userFabLayout.animate().translationY(0);
        albumFabLayout.animate().translationY(0);
        postFabLayout.animate().translationY(0);
        postFabLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    userFabLayout.setVisibility(View.GONE);
                    albumFabLayout.setVisibility(View.GONE);
                    postFabLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

}

