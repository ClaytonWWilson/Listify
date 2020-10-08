package com.example.listify;

import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.listify.model.Product;
import com.example.listify.model.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ItemDetails extends AppCompatActivity implements ListPickerDialogFragment.OnListPickListener, CreateListDialogFragment.OnNewListListener {
    private Product curProduct;
    private LinearLayout linAddItem;
    private LinearLayout linCreateList;
    private TextView tvCreateNew;
    private TextView tvAddItem;
    private TextView tvItemName;
    private TextView tvStoreName;
    private ImageView itemImage;
    private TextView tvItemPrice;
    private TextView tvItemDesc;
    private ImageButton backToSearchbutton;

    ArrayList<ShoppingList> shoppingLists = new ArrayList<>();

    private boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load Product object from search results activity
        curProduct = (Product) getIntent().getSerializableExtra("SelectedProduct");

        // Set up floating action buttons
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        linAddItem = (LinearLayout) findViewById(R.id.lin_add_item);
        linCreateList = (LinearLayout) findViewById(R.id.lin_create_list);
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

        linAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();

                // Hardcode shopping lists to demonstrate displaying lists
                for (int i = 0; i < 10; i++) {
                    shoppingLists.add(new ShoppingList(Integer.toString(i)));
                }

                ListPickerDialogFragment listPickerDialog = new ListPickerDialogFragment(shoppingLists);
                listPickerDialog.show(getSupportFragmentManager(), "User Lists");
            }
        });

        linCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();

                CreateListDialogFragment createListDialogFragment = new CreateListDialogFragment();
                createListDialogFragment.show(getSupportFragmentManager(), "Create New List");
            }
        });

        // Set data
        tvItemName = (TextView) findViewById(R.id.item_name);
        tvItemName.setText(curProduct.getItemName());

        itemImage = (ImageView) findViewById(R.id.item_image);
        Glide.with(this).load(curProduct.getImageUrl()).into(itemImage);

        tvStoreName = (TextView) findViewById(R.id.store_name);
        tvStoreName.setText(curProduct.getChainName());

        tvItemPrice = (TextView) findViewById(R.id.item_price);
        tvItemPrice.setText(String.format("$%.2f", curProduct.getPrice()));

        tvItemDesc = (TextView) findViewById(R.id.item_desc);
        tvItemDesc.setText(curProduct.getDescription());

        tvCreateNew = (TextView) findViewById(R.id.create_new_list);
        tvCreateNew.setVisibility(View.INVISIBLE);

        tvAddItem = (TextView) findViewById(R.id.add_item_to_list);
        tvAddItem.setVisibility(View.INVISIBLE);

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
        linAddItem.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        linCreateList.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        tvAddItem.setVisibility(View.VISIBLE);
        tvCreateNew.setVisibility(View.VISIBLE);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        linAddItem.animate().translationY(0);
        linCreateList.animate().translationY(0);
        tvAddItem.setVisibility(View.INVISIBLE);
        tvCreateNew.setVisibility(View.INVISIBLE);
    }


    @Override
    public void sendListSelection(int selectedListIndex, int quantity) {
        Toast.makeText(this, String.format("%d of Item added to %s", quantity, shoppingLists.get(selectedListIndex).getName()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendNewListName(String name) {
        Toast.makeText(this, String.format("%s created", name), Toast.LENGTH_LONG).show();
    }
}