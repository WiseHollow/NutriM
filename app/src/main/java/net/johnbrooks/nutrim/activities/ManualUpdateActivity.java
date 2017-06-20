package net.johnbrooks.nutrim.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.Profile;
import net.johnbrooks.nutrim.wrapper.NutritionIXItem;

import java.util.Objects;

public class ManualUpdateActivity extends AppCompatActivity
{
    private EditText et_foodName;
    private EditText et_brand;
    private EditText et_servingSize;
    private EditText et_calories;
    private EditText et_quantity;

    private ImageView iv_food;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_update);

        //
        // Prepare back button
        //

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Gets

        et_foodName = (EditText) findViewById(R.id.manualUpdateActivity_editText_foodName);
        et_brand = (EditText) findViewById(R.id.manualUpdateActivity_editText_brand);
        et_servingSize = (EditText) findViewById(R.id.manualUpdateActivity_editText_servingSize);
        et_calories = (EditText) findViewById(R.id.manualUpdateActivity_editText_calories);
        et_quantity = (EditText) findViewById(R.id.manualUpdateActivity_editText_quantity);

        iv_food = (ImageView) findViewById(R.id.manualUpdateActivity_imageView_food);
    }

    private boolean onClickAdd()
    {
        if (Objects.equals(et_foodName.getText().toString(), "") || Objects.equals(et_quantity.getText().toString(), "0"))
        {
            Toast toast = Toast.makeText(ManualUpdateActivity.this, "Please fill in required fields", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        Profile profile = Profile.getProfile();
        if (profile != null)
        {
            String name = et_foodName.getText().toString();
            String brand = et_brand.getText().toString();
            String servingSize = et_servingSize.getText().toString();
            int calories = Integer.parseInt(et_calories.getText().toString());
            int quantity = Integer.parseInt(et_quantity.getText().toString());

            NutritionIXItem item = new NutritionIXItem("manual", name, brand, calories, servingSize);
            profile.addCaloriesToday(item, quantity);
            profile.save(ManualUpdateActivity.this);
        }

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
                return onClickAdd();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
