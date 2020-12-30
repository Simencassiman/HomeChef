package com.simencassiman.homechef.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.simencassiman.homechef.AuthActivity;
import com.simencassiman.homechef.R;
import com.simencassiman.homechef.viewmodels.MainViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import javax.inject.Inject;


public class MainActivity extends AuthActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "MainActivity";
    private MainViewModel viewModel;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, providerFactory).get(MainViewModel.class);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        init();
    }

    private void init(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

//            case R.id.signout:
//                //sessionManager.logOut();
//                return true;

            case android.R.id.home:{
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                else{
                    return false;
                }
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

//            case R.id.nav_home:{
//
//                NavOptions navOptions = new NavOptions.Builder()
//                        .setPopUpTo(R.id.main_navigation, true)
//                        .build();
//
//                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
//                        R.id.homeFragment,
//                        null,
//                        navOptions
//                );
//
//                break;
//            }
//
//            case R.id.nav_recipes:{
//
//                if(isValidDestination(R.id.recipeListFragment)) {
//                    HomeFragmentDirections.GoToRecipeListAction action =
//                            HomeFragmentDirections.goToRecipeListAction();
//                    action.setListMode(RecipeListViewModel.ALL_RECIPIES_MODE);
//
//                    Navigation.findNavController(this, R.id.nav_host_fragment)
//                            .navigate(action);
//                }
//
//                break;
//            }
//
//            case R.id.nav_add_recipe:
//                if(isValidDestination(R.id.addEditRecipeFragment)) {
//                    RecipeFragmentDirections.EditAction action =
//                            RecipeFragmentDirections.editAction();
//                    Navigation.findNavController(this, R.id.nav_host_fragment)
//                            .navigate(R.id.addEditRecipeFragment);
//                }
//                break;
//
//            case R.id.nav_favorite_recipes:
//                if(isValidDestination(R.id.recipeListFragment)){
//                    HomeFragmentDirections.GoToRecipeListAction action =
//                            HomeFragmentDirections.goToRecipeListAction();
//                    action.setListMode(RecipeListViewModel.FAVORITE_RECIPIES_MODE);
//
//                    Navigation.findNavController(this, R.id.nav_host_fragment)
//                            .navigate(action);
//                }

        }

        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isValidDestination(int destination){
//        if(destination == R.id.recipeListFragment)
            //Check if the same mode

        return destination != Navigation.findNavController(this, R.id.nav_host_fragment)
                .getCurrentDestination().getId();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }
}
