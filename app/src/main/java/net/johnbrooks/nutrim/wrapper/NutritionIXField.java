package net.johnbrooks.nutrim.wrapper;

/**
 * Created by John on 5/30/2017.
 */

public enum NutritionIXField
{
    ITEM_NAME("item_name"), ITEM_ID("item_id"), ITEM_BRAND("brand_name"), CALORIES("nf_calories"), TOTAL_FAT("nf_total_fat"), SERVING_SIZE("nf_serving_size_unit");

    private final String value;

    NutritionIXField(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
