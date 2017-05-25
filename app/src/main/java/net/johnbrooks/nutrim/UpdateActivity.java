package net.johnbrooks.nutrim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.johnbrooks.NW.NutritionIXItem;
import net.johnbrooks.NW.NutritionIXQuery;

public class UpdateActivity extends AppCompatActivity
{
    private EditText et_search;
    private LinearLayout ll_searchContents;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        et_search = (EditText) findViewById(R.id.updateActivity_editText_search);
        ll_searchContents = (LinearLayout) findViewById(R.id.updateActivity_linearLayout_searchContents);
        findViewById(R.id.updateActivity_button_search).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ll_searchContents.removeAllViews();

                String keyword = et_search.getText().toString();
                NutritionIXItem[] items = NutritionIXQuery.searchForItems(keyword);

                for (NutritionIXItem item : items)
                {
                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_updateresult, null);

                    TextView itemName = (TextView) layout.findViewById(R.id.widget_updateResult_itemName);
                    TextView calories = (TextView) layout.findViewById(R.id.widget_updateResult_caloriesTextView);

                    itemName.setText(item.getName());
                    calories.setText("Calories: " + item.getCalories());

                    ll_searchContents.addView(layout);
                }
            }
        });
    }
}
