package net.johnbrooks.nutrim.wrapper;

/**
 * Created by John on 5/30/2017.
 */

public class NutritionIXItem
{
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

    @Override
    public String toString()
    {
        return "ID: " + id + '\n' + "Name: " + name + '\n' + "Brand: " + brand + '\n' + "Calories: " + calories + '\n' + "Serving Size: " + servingSize;
    }
}
