package com.example.conceptsandroidstudio;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MenuActivity extends AppCompatActivity{

    ProductsFragment productsFragment = new ProductsFragment();
    ShoppingCartFragment shoppingCartFragment = new ShoppingCartFragment();
    AccountFragment accountFragment = new AccountFragment();
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        loadFragment(productsFragment);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() == R.id.productsFragment) {
                loadFragment(productsFragment);
                return true;
            } else if (item.getItemId() == R.id.shoppingCartFragment) {
                loadFragment(shoppingCartFragment);
                return true;
            } else if (item.getItemId() == R.id.accountFragment) {
                loadFragment(accountFragment);
                return true;
            }
            return false;
        }
    };

    public void loadFragment (Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    public void navigateToShoppingCart() {
        navigation.setSelectedItemId(R.id.shoppingCartFragment);
    }
}
