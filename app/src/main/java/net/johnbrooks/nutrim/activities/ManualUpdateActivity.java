package net.johnbrooks.nutrim.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        findViewById(R.id.manualUpdateActivity_button_add).setOnClickListener(onClickAdd());
    }

    private View.OnClickListener onClickAdd()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Objects.equals(et_foodName.getText().toString(), "") || Objects.equals(et_quantity.getText().toString(), "0"))
                {
                    Toast toast = Toast.makeText(ManualUpdateActivity.this, "Please fill in required fields", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
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
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
