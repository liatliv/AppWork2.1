package com.example.amirl2.appwork.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amirl2.appwork.Accessories.DBHelper;
import com.example.amirl2.appwork.Accessories.FoodItemObj;
import com.example.amirl2.appwork.R;

import java.util.ArrayList;

public class FoodItemsListActivity extends AppCompatActivity {

    Button btnAddItemToLog;
    EditText etSearchFood;
    ListView lvFoodItems;
    DBHelper dbHelper;
    FoodItemsListAdapter adapter;
    ArrayList<FoodItemObj> foodItemsArrayListObj, searchedResultList;
    ArrayList<FoodItemViewHolder> viewHoldersList;
    ArrayList<Integer> itemsSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_items_list);

        btnAddItemToLog = (Button) findViewById(R.id.btn_add_items_to_logs);
        etSearchFood = (EditText) findViewById(R.id.et_search_food);
        lvFoodItems = (ListView) findViewById(R.id.lv_food_items);
        dbHelper = new DBHelper(this);
        foodItemsArrayListObj = dbHelper.getAllFoodItems();
        searchedResultList = new ArrayList<FoodItemObj>(foodItemsArrayListObj);

        viewHoldersList = new ArrayList<>();
        itemsSelected = new ArrayList<>();
        adapter = new FoodItemsListAdapter(this, searchedResultList);
        lvFoodItems.setAdapter(adapter);
        lvFoodItems.setTextFilterEnabled(true);


        btnAddItemToLog.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                dbHelper.insertFoodsListToDailyLogs(itemsSelected);

                Intent mainActivityIntent = new Intent(FoodItemsListActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);

            }
        });


        etSearchFood.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                String searchString = etSearchFood.getText().toString();
                int textLength=searchString.length();

                //clear the initial data set
                searchedResultList.clear();

                for(int i = 0; i< foodItemsArrayListObj.size(); i++)
                {
                    String foodName= foodItemsArrayListObj.get(i).name;
                    if(textLength<=foodName.length()){
                        //compare the String in EditText with Names in the ArrayList
                        if(searchString.equalsIgnoreCase(foodName.substring(0,textLength)))
                            searchedResultList.add(foodItemsArrayListObj.get(i));
                    }
                }

               adapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                adapter.notifyDataSetChanged();
            }
        });


    }

    public class FoodItemsListAdapter extends ArrayAdapter<FoodItemObj> {

        public FoodItemsListAdapter(Context context, ArrayList<FoodItemObj> foodItemObjs) {
            super(context, 0, foodItemObjs);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final FoodItemViewHolder viewHolder;


            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list, null);
                viewHolder = new FoodItemViewHolder();

                viewHolder.tvFoodName = (TextView) convertView.findViewById(R.id.tv_food_name);
                viewHolder.tvServing = (TextView) convertView.findViewById(R.id.tv_serving);
                viewHolder.tvCalories = (TextView) convertView.findViewById(R.id.tv_calories);
                viewHolder.chbx = (CheckBox) convertView.findViewById(R.id.chbx_item);

                convertView.setTag(viewHolder);

                viewHolder.chbx.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        FoodItemObj foodItemObj = (FoodItemObj) cb.getTag();

                        if (cb.isChecked()) {
                            itemsSelected.add(foodItemObj.id);
                            Toast.makeText(getApplicationContext(),
                                    "itemsSelected: " + itemsSelected.size(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (itemsSelected.contains(foodItemObj.id)) {
                                itemsSelected.remove(itemsSelected.indexOf(foodItemObj.id));
                                Toast.makeText(getApplicationContext(),
                                        "itemsSelected: " + itemsSelected.size(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

            } else {
                viewHolder = (FoodItemViewHolder) convertView.getTag();
            }

            FoodItemObj foodItemObj = getItem(position);

            if (foodItemObj != null) {
                viewHolder.id = foodItemObj.id;
                viewHolder.tvFoodName.setText(foodItemObj.name);
                viewHolder.tvServing.setText(foodItemObj.serving);
                viewHolder.tvCalories.setText("" + foodItemObj.calories);
                viewHolder.chbx.setTag(foodItemObj);
            }


            return convertView;

        }


    }

    public static class FoodItemViewHolder {
        int id;
        TextView tvFoodName;
        TextView tvServing;
        TextView tvCalories;
        CheckBox chbx;
    }
}