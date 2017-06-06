package net.johnbrooks.nutrim.utilities;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import net.johnbrooks.nutrim.wrapper.NutritionIXItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by John on 5/30/2017.
 */

public class Profile
{
    private static Profile profile;
    public static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    public static Profile getProfile()
    {
        return profile;
    }
    public static Profile createProfile(String name, Date birthday, int weightKg, int heightCm)
    {
        profile = new Profile(name, birthday, weightKg, heightCm);
        return profile;
    }
    public static Profile loadProfile() throws ProfileLoadException
    {
        if (MyApplicationContexts.getLatestContextWrapper(null) == null)
        {
            Log.d("Profile", "Failed to load profile. LatestContextWrapper is null.");
            return null;
        }
        SharedPreferences preferences = MyApplicationContexts.getSharedPreferences();

        String fullName = preferences.getString("fullName", null);
        int weightKg = preferences.getInt("weight", 0);
        int heightCm = preferences.getInt("height", 0);
        int calories = preferences.getInt("calories", 0);
        String birthdayString = preferences.getString("birthday", null);

        MeasurementSystem measurementSystem;
        try
        {
            measurementSystem = MeasurementSystem.valueOf(preferences.getString("measurement", "METRIC"));
        }
        catch (Exception ex)
        {
            throw new ProfileLoadException("Metric system saved is not valid.");
        }

        if (fullName == null || birthdayString == null)
        {
            throw new ProfileLoadException();
        }

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date birthday;
        try
        {
            birthday = dateFormat.parse(birthdayString);
        } catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }

        profile = new Profile(fullName, birthday, weightKg, heightCm);

        String latestDayUsed = preferences.getString("latestDayUsed", null);

        if (latestDayUsed == null || dateFormat.format(new Date()).equalsIgnoreCase(latestDayUsed))
        {
            Set<String> consumed = preferences.getStringSet("consumed", new HashSet<String>());
            for (String s: consumed)
            {
                NutritionIXItem ixItem = NutritionIXItem.serialize(s);
                if (ixItem != null)
                    profile.getItemsConsumed().add(ixItem);
            }
        }

        profile.setCaloriesToday(calories);
        if (latestDayUsed != null)
            profile.setLatestDayUsed(latestDayUsed);
        if (measurementSystem != null)
            profile.setMeasurementSystem(measurementSystem);
        return profile;
    }

    private String fullName;
    private Date birthday;
    private int weightKg;
    private int heightCm;

    private String latestDayUsed;

    private List<NutritionIXItem> itemsConsumed;
    private int caloriesToday;
    private int caloriesDailyMax;

    private MeasurementSystem measurementSystem;

    private Profile(String fullName, Date birthday, int weightKg, int heightCm)
    {
        this.fullName = fullName;
        this.birthday = birthday;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.caloriesToday = 0;
        this.caloriesDailyMax = calculateDailyCalorieNeeds();
        this.measurementSystem = MeasurementSystem.METRIC;
        this.itemsConsumed = new ArrayList<>();
        this.latestDayUsed = dateFormat.format(new Date());
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public int getCaloriesToday()
    {
        return caloriesToday;
    }

    public List<NutritionIXItem> getItemsConsumed() { return itemsConsumed; }

    public void setCaloriesToday(int caloriesToday)
    {
        this.caloriesToday = caloriesToday;
    }

    public void addCaloriesToday(NutritionIXItem item)
    {
        itemsConsumed.add(item);
        caloriesToday += item.getCalories();
    }

    public int getWeightKg()
    {
        return weightKg;
    }

    public void setWeightKg(int weightKg)
    {
        this.weightKg = weightKg;
    }

    public int getHeightCm()
    {
        return heightCm;
    }

    public void setHeightCm(int heightCm)
    {
        this.heightCm = heightCm;
    }

    public int[] getFeetAndInches()
    {
        int[] measures = new int[2];
        measures[0] = getHeightFeet();
        measures[1] = getHeightCm() % measures[0];
        return measures;
    }

    public int getHeightInches()
    {
        return (int) (getHeightCm() * 0.393701f);
    }

    public int getHeightFeet()
    {
        return (int) (getHeightCm() * 0.0328084f);
    }

    public int getWeightLbs()
    {
        return (int) (profile.getWeightKg() * 2.20462f);
    }

    public int getAge()
    {
        Date now = new Date();
        long diff = now.getTime() - getBirthday().getTime();
        int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        int years = days / 365;
        return years;
    }

    public int calculateDailyCalorieNeeds()
    {
        return (int) (10 * weightKg + 6.25f * heightCm - 5 * getAge() + 5);
    }

    public void save(ContextWrapper contextWrapper)
    {
        if (MyApplicationContexts.getLatestContextWrapper(contextWrapper) == null)
        {
            Log.d("Profile", "Failed to load profile. LatestContextWrapper is null.");
            return;
        }
        SharedPreferences preferences = MyApplicationContexts.getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();

        String birthdayString = dateFormat.format(birthday);

        editor.putString("fullName", getFullName());
        editor.putInt("height", getHeightCm());
        editor.putInt("weight", getWeightKg());
        editor.putString("birthday", birthdayString);
        editor.putString("measurement", measurementSystem.name());
        editor.putInt("calories", getCaloriesToday());
        editor.putString("latestDayUsed", getLatestDayUsed());

        Set<String> itemsConsumedStrings = new HashSet<>();
        for (NutritionIXItem ixItem : itemsConsumed)
            itemsConsumedStrings.add(ixItem.toSaveString());
        editor.putStringSet("consumed", itemsConsumedStrings);

        editor.commit();
    }

    public int getCaloriesDailyMax()
    {
        return caloriesDailyMax;
    }

    public MeasurementSystem getMeasurementSystem()
    {
        return measurementSystem;
    }

    public void setMeasurementSystem(MeasurementSystem measurementSystem)
    {
        this.measurementSystem = measurementSystem;
    }

    public String getLatestDayUsed()
    {
        return latestDayUsed;
    }

    public void setLatestDayUsed(String latestDayUsed)
    {
        this.latestDayUsed = latestDayUsed;
    }

    public enum MeasurementSystem
    {
        METRIC, IMPERIAL
    }
}
