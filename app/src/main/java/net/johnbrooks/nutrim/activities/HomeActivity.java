package net.johnbrooks.nutrim.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.MyApplicationContexts;
import net.johnbrooks.nutrim.utilities.Profile;
import net.johnbrooks.nutrim.utilities.TaskUpdateProgressBar;
import net.johnbrooks.nutrim.wrapper.NutritionIXItem;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static HomeActivity instance;
    public static HomeActivity getInstance()
    {
        return instance;
    }

    private TextView tv_caloriesToday;
    private TextView tv_percentCaloriesToday;
    private DonutProgress dp_caloriesProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        instance = this;
        MyApplicationContexts.getLatestContextWrapper(HomeActivity.this);

        tv_caloriesToday = (TextView) findViewById(R.id.homeActivity_textView_CaloriesToday);
        tv_percentCaloriesToday = (TextView) findViewById(R.id.homeActivity_textView_CaloriesTodayPercent);
        dp_caloriesProgress = (DonutProgress) findViewById(R.id.donut_progress);
        refreshCaloriesToday();

        findViewById(R.id.homeActivity_button_update).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.homeActivity_button_profile).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });

        if (Profile.getProfile() != null)
            Profile.getProfile().save(HomeActivity.this);
    }

    public void refreshCaloriesToday()
    {
        if (Profile.getProfile() == null)
        {
            if (dp_caloriesProgress != null)
                dp_caloriesProgress.setProgress(0);
            return;
        }

        int today = Profile.getProfile().getCaloriesToday();
        int max = Profile.getProfile().getCaloriesDailyMax();
        tv_caloriesToday.setText(today + " / " + max);
        if (today != 0 && max != 0)
        {
            int progress = (int) (100 * ((float)today / (float) max));
            if (progress > 100)
                progress = 100;
            //dp_caloriesProgress.setProgress(progress);
            tv_percentCaloriesToday.setText(progress + "%");
            applyProgress(progress);
        }
        else
            dp_caloriesProgress.setProgress(0);
        dp_caloriesProgress.invalidate();

        LinearLayout layout_consumed = (LinearLayout) findViewById(R.id.myProfileActivity_layout_foodIcons);
        layout_consumed.removeAllViews();
        for (final NutritionIXItem ixItem : Profile.getProfile().getItemsConsumed().keySet())
        {
            final int amount = Profile.getProfile().getItemsConsumed().get(ixItem);

            LinearLayout foodLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_food_icon, null);
            ImageView iv_food = (ImageView) foodLayout.findViewById(R.id.widget_food_icon_imageView);
            TextView tv_calories = (TextView) foodLayout.findViewById(R.id.widget_food_icon_textView);
            iv_food.setImageResource(ixItem.getPictureID());
            tv_calories.setText("" + (ixItem.getCalories() * amount));
            layout_consumed.addView(foodLayout);

            foodLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    openDetails(ixItem, amount);
                }
            });
        }

        refreshTip();
    }

    private void refreshTip()
    {
        TextView tv_tip = (TextView) findViewById(R.id.homeActivity_textView_tip);
        if (tv_tip != null && Profile.getProfile() != null)
        {
            tv_tip.setText(Profile.getProfile().getTip() != null ? Profile.getProfile().getTip() : "Take a look at what you've eaten today.\n\n");
        }
    }

    public void applyProgress(final int progress)
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            dp_caloriesProgress.setProgress(0);
                            TaskUpdateProgressBar performBackgroundTask = new TaskUpdateProgressBar(progress, dp_caloriesProgress);
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            performBackgroundTask.execute();
                        } catch (Exception e)
                        {
                            dp_caloriesProgress.setProgress(progress);
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0); //execute in every 50000 ms
    }

    private void openDetails(final NutritionIXItem item, final Integer amount)
    {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_item_details);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv_name = (TextView) dialog.findViewById(R.id.dialog_item_details_tv_foodName);
        TextView tv_brand = (TextView) dialog.findViewById(R.id.dialog_item_details_tv_brand);
        TextView tv_servingSize = (TextView) dialog.findViewById(R.id.dialog_item_details_tv_servingSize);
        TextView tv_calories = (TextView) dialog.findViewById(R.id.dialog_item_details_tv_calories);
        TextView tv_amount = (TextView) dialog.findViewById(R.id.dialog_item_details_tv_quantity);
        ImageView iv_food = (ImageView) dialog.findViewById(R.id.dialog_item_details_iv_food);

        String itemName = item.getName().substring(0, Math.min(item.getName().length(), 30));

        tv_name.setText(itemName);
        tv_brand.setText(item.getBrand());
        tv_servingSize.setText(item.getServingSize());
        tv_calories.setText("" + item.getCalories());
        tv_amount.setText("" + amount);
        iv_food.setImageResource(item.getPictureID());

        dialog.findViewById(R.id.dialog_item_details_button_okay).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed()
    {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //if (drawer.isDrawerOpen(GravityCompat.START)) {
        //    drawer.closeDrawer(GravityCompat.START);
        //} else {
        //    super.onBackPressed();
        //}
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
