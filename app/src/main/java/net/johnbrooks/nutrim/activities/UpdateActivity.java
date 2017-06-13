package net.johnbrooks.nutrim.activities;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.MyApplicationContexts;
import net.johnbrooks.nutrim.utilities.Profile;
import net.johnbrooks.nutrim.wrapper.NutritionIXField;
import net.johnbrooks.nutrim.wrapper.NutritionIXItem;
import net.johnbrooks.nutrim.wrapper.NutritionIXQuery;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity
{
    public static List<NutritionIXItem> queryResults = new ArrayList<>();
    private NutritionIXItem selectedItem;

    private static UpdateActivity instance;
    public static UpdateActivity getInstance()
    {
        if (instance == null)
        {
            Log.d(UpdateActivity.class.getSimpleName(), "Null instance of class.");
        }

        return instance;
    }

    private EditText et_search;
    private EditText et_quantity;
    private LinearLayout ll_searchContents;

    private List<RadioButton> radioButtonList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        //
        // Prepare back button
        //

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //
        // Get required components and listeners
        //

        instance = this;
        radioButtonList = new ArrayList<>();
        selectedItem = null;

        et_search = (EditText) findViewById(R.id.updateActivity_editText_search);
        et_quantity = (EditText) findViewById(R.id.updateActivity_editText_quantity);
        ll_searchContents = (LinearLayout) findViewById(R.id.updateActivity_linearLayout_searchContents);
        findViewById(R.id.updateActivity_button_search).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                run();
            }
        });
        et_search.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    run();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_done:
                if (finishSelection())
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean finishSelection()
    {
        if (selectedItem != null)
        {
            Profile profile = Profile.getProfile();
            if (profile != null)
            {
                profile.addCaloriesToday(selectedItem, Integer.parseInt(et_quantity.getText().toString()));
                profile.save(MyApplicationContexts.getLatestContextWrapper(UpdateActivity.getInstance()));
            }
            return true;
        }

        return false;
    }

    public void refreshList()
    {
        ll_searchContents.removeAllViews();
        radioButtonList.clear();
        if (queryResults.isEmpty())
        {
            TextView tv_nothing_to_display = new TextView(UpdateActivity.this);
            tv_nothing_to_display.setText("Nothing was returned from your search. ");
            ll_searchContents.addView(tv_nothing_to_display);
        }
        else
        {
            //
            // Add widgets for each result returned from query.
            //
            for (int i = 0; i < queryResults.size(); i++)
            {
                final int index = i;
                NutritionIXItem item = queryResults.get(i);
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_updateresult, null);
                ImageView picture = (ImageView) layout.findViewById(R.id.widget_updateResult_imageView);
                TextView itemName = (TextView) layout.findViewById(R.id.widget_updateResult_itemName);
                TextView calories = (TextView) layout.findViewById(R.id.widget_updateResult_caloriesTextView);
                TextView brand = (TextView) layout.findViewById(R.id.widget_updateResult_brandTextView);
                String shortItemName = item.getName().substring(0, Math.min(item.getName().length(), 30));
                itemName.setText(shortItemName);
                calories.setText("Calories: " + item.getCalories());
                brand.setText("Brand: " + item.getBrand());
                picture.setImageResource(item.getPictureID());
                ll_searchContents.addView(layout);

                //
                // Keep track of all buttons and manage their click events.
                //
                final RadioButton button = (RadioButton) layout.findViewById(R.id.widget_updateResult_ratioButton);
                radioButtonList.add(button);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        for (RadioButton radioButton : radioButtonList)
                            if (radioButton != button)
                                radioButton.setChecked(false);
                        NutritionIXItem clickedItem = queryResults.get(index);
                        Log.d(UpdateActivity.class.getSimpleName(), "Clicked on " + clickedItem.getName() + "(" + index + ")");
                        selectedItem = clickedItem;
                    }
                });
            }
        }
    }

    private void run()
    {
        if (getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        String keyword = et_search.getText().toString();
        et_search.setText("");
        NutritionIXQuery.searchForItems(keyword, NutritionIXField.ITEM_NAME, NutritionIXField.CALORIES, NutritionIXField.ITEM_BRAND, NutritionIXField.SERVING_SIZE);
    }
}
