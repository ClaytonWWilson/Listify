package com.example.listify;

import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.listify.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetails extends AppCompatActivity {
    private Product curProduct;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private TextView tvItemName;
    private TextView tvStoreName;
    private ImageView itemImage;
    private TextView tvItemDesc;
    private ImageButton backToSearchbutton;

    private boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        curProduct = (Product) getIntent().getSerializableExtra("SelectedProduct");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        tvItemName = (TextView) findViewById(R.id.item_name);
        tvItemName.setText(curProduct.getItemName());

        itemImage = (ImageView) findViewById(R.id.item_image);
        Glide.with(this).load(curProduct.getImageUrl()).into(itemImage);

        tvStoreName = (TextView) findViewById(R.id.store_name);
        tvStoreName.setText(curProduct.getChainName());

        tvItemDesc = (TextView) findViewById(R.id.item_desc);
        tvItemDesc.setText(curProduct.getDescription());

        backToSearchbutton = (ImageButton) findViewById(R.id.back_to_search_results_button);
        backToSearchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
    }


}