package net.johnbrooks.nutrim.utilities;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.wrapper.NutritionIXItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by John on 6/15/2017.
 */

public class ProfileLog
{
    private HashMap<NutritionIXItem, Integer> itemsConsumed;
    private int caloriesThisWeek;

    public ProfileLog()
    {
        caloriesThisWeek = 0;
        itemsConsumed = new HashMap<>();
    }

    public void setCaloriesThisWeek(final int calories)
    {
        caloriesThisWeek = calories;
    }

    public HashMap<NutritionIXItem, Integer> getItemsConsumed() { return itemsConsumed; }

    public void pushFoodEntry(final NutritionIXItem item, final int amount)
    {
        itemsConsumed.put(item, amount);
    }

    public void record(final NutritionIXItem item, final int amount)
    {
        if (itemsConsumed.containsKey(item))
            itemsConsumed.put(item, amount + itemsConsumed.get(item));
        else
            itemsConsumed.put(item, amount);
        caloriesThisWeek += item.getCalories() * amount;
    }

    public int getCaloriesThisWeek()
    {
        return caloriesThisWeek;
    }

    public List<LinearLayout> getBreakdown()
    {
        List<LinearLayout> breakdown = new ArrayList<>();

        if (MyApplicationContexts.getLatestContextWrapper(null) == null)
        {
            Log.d(ProfileLog.class.getSimpleName(), "Latest context wrapper is null.");
            return breakdown;
        }

        for (NutritionIXItem item : itemsConsumed.keySet())
        {
            //Integer amount = itemsConsumed.get(item);

            LinearLayout layout = (LinearLayout) ((Activity) MyApplicationContexts.getLatestContextWrapper(null)).getLayoutInflater().inflate(R.layout.widget_updateresult, null);
            breakdown.add(layout);

            ImageView iv_icon = (ImageView) layout.findViewById(R.id.widget_updateResult_imageView);
            TextView tv_name = (TextView) layout.findViewById(R.id.widget_updateResult_itemName);
            TextView tv_brand = (TextView) layout.findViewById(R.id.widget_updateResult_brandTextView);
            TextView tv_calories = (TextView) layout.findViewById(R.id.widget_updateResult_caloriesTextView);
            RadioButton radioButton = (RadioButton) layout.findViewById(R.id.widget_updateResult_ratioButton);
            layout.removeView(radioButton);

            String shortItemName = item.getName().substring(0, Math.min(item.getName().length(), 20));
            String shortBrand = item.getBrand().substring(0, Math.min(item.getBrand().length(), 20));

            iv_icon.setImageResource(item.getPictureID());
            tv_name.setText(shortItemName);
            tv_calories.setText("Calories: " + item.getCalories());
            tv_brand.setText("Brand: " + shortBrand);

            //TODO: Click to get details...
        }

        return breakdown;
    }
}
