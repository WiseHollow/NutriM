package net.johnbrooks.nutrim.wrapper;

import net.johnbrooks.nutrim.R;

/**
 * Created by John on 5/30/2017.
 */

public class NutritionIXItem
{
    public static NutritionIXItem serialize(String serial)
    {
        String[] data = serial.split("\\.");

        String id = data[0];
        String name = data[1];
        String brand = data[2];
        int calories = Integer.parseInt(data[3]);
        String servingSize = data[4];

        NutritionIXItem ixItem = new NutritionIXItem(id, name, brand, calories, servingSize);
        return ixItem;
    }

    private String id;
    private String name;
    private String brand;

    private String servingSize;

    private int calories;

    public NutritionIXItem(String id, String name, String brand, int calories, String servingSize)
    {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.servingSize = servingSize;
        this.calories = calories;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getServingSize() { return servingSize; }
    public int getCalories() { return calories; }

    public int getPictureID()
    {
        if (name != null)
        {
            String nameLowerCase = name.toLowerCase();

            if (nameLowerCase.contains("pizza"))
                return R.drawable.pizza_slice;
            else if (nameLowerCase.contains("rice"))
                return R.drawable.chicken_rice;
            else if (nameLowerCase.contains("fruit") || nameLowerCase.contains("pear"))
                return R.drawable.pear;
            else if (nameLowerCase.contains("steak"))
                return R.drawable.steak;
            else if (nameLowerCase.contains("watermelon"))
                return R.drawable.food_watermelon;
            else if (nameLowerCase.contains("muffin"))
                return R.drawable.food_muffin;
            else if (nameLowerCase.contains("pancake"))
                return R.drawable.food_pancakes;
            else if (nameLowerCase.contains("bread"))
                return R.drawable.food_bread;
        }

        return R.drawable.food_flat;
    }

    public String toSaveString()
    {
        return id + "." + name + "." + brand + "." + calories + "." + servingSize;
    }

    @Override
    public String toString()
    {
        return "ID: " + id + '\n' + "Name: " + name + '\n' + "Brand: " + brand + '\n' + "Calories: " + calories + '\n' + "Serving Size: " + servingSize;
    }
}
