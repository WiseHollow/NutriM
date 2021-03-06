package net.johnbrooks.nutrim.utilities;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import net.johnbrooks.nutrim.activities.HomeActivity;
import net.johnbrooks.nutrim.wrapper.NutritionIXItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    public static Profile createProfile(String name, Date birthday, float weightKg, float heightCm)
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

        String fullName;
        float weightKg;
        float heightCm;
        int calories;
        int caloriesThisWeek;
        String birthdayString;

        try
        {
            fullName = preferences.getString("fullName", null);
            weightKg = preferences.getFloat("weight", 0);
            heightCm = preferences.getFloat("height", 0);
            calories = preferences.getInt("calories", 0);
            caloriesThisWeek = preferences.getInt("caloriesThisWeek", 0);
            birthdayString = preferences.getString("birthday", null);
        }
        catch (Exception ex)
        {
            throw new ProfileLoadException("Could not load saved data from configuration.");
        }

        MeasurementSystem measurementSystem;
        try
        {
            measurementSystem = MeasurementSystem.valueOf(preferences.getString("measurement", "METRIC"));
        }
        catch (Exception ex)
        {
            throw new ProfileLoadException("Metric system saved is not valid.");
        }
        Theme theme;
        try
        {
            theme = Theme.valueOf(preferences.getString("theme", "LIGHT"));
        }
        catch (Exception ex)
        {
            throw new ProfileLoadException("Theme saved is not valid.");
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
            profile.setCaloriesToday(calories);
            Set<String> consumed = preferences.getStringSet("consumed", new HashSet<String>());
            for (String s: consumed)
            {
                Map.Entry<NutritionIXItem, Integer> entry = NutritionIXItem.serialize(s);
                if (entry != null && entry.getKey() != null)
                    profile.pushFoodEntry(entry.getKey(), entry.getValue());
            }
        }
        else
            profile.setCaloriesToday(0);


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        boolean monday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
        if (latestDayUsed == null || (!monday && dateFormat.format(new Date()).equalsIgnoreCase(latestDayUsed)))
        {
            profile.getLog().setCaloriesThisWeek(caloriesThisWeek);
            Set<String> consumed = preferences.getStringSet("consumedLog", new HashSet<String>());
            for (String s: consumed)
            {
                Map.Entry<NutritionIXItem, Integer> entry = NutritionIXItem.serialize(s);
                if (entry != null && entry.getKey() != null)
                    profile.getLog().pushFoodEntry(entry.getKey(), entry.getValue());
            }
        }
        else
            profile.getLog().setCaloriesThisWeek(0);

        if (latestDayUsed != null)
            profile.setLatestDayUsed(latestDayUsed);
        if (measurementSystem != null)
            profile.setMeasurementSystem(measurementSystem);
        if (theme != null)
            profile.setTheme(theme);
        return profile;
    }

    private String fullName;
    private Date birthday;
    private float weightKg;
    private float heightCm;

    private String latestDayUsed;

    //private List<NutritionIXItem> itemsConsumed;
    private HashMap<NutritionIXItem, Integer> itemsConsumed;
    private int caloriesToday;
    private int caloriesDailyMax;

    private MeasurementSystem measurementSystem;
    private Theme theme;

    private ProfileLog log;

    private Profile(String fullName, Date birthday, float weightKg, float heightCm)
    {
        this.fullName = fullName;
        this.birthday = birthday;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.caloriesToday = 0;
        this.caloriesDailyMax = calculateDailyCalorieNeeds();
        this.measurementSystem = MeasurementSystem.METRIC;
        this.theme = Theme.LIGHT;
        this.itemsConsumed = new HashMap<>();
        this.latestDayUsed = dateFormat.format(new Date());
        this.log = new ProfileLog();
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

    public HashMap<NutritionIXItem, Integer> getItemsConsumed() { return itemsConsumed; }

    public void pushFoodEntry(NutritionIXItem item, int amount)
    {
        itemsConsumed.put(item, amount);
    }

    public void setCaloriesToday(int caloriesToday)
    {
        this.caloriesToday = caloriesToday;
    }

    public void addCaloriesToday(NutritionIXItem item, int amount)
    {
        if (itemsConsumed.containsKey(item))
            itemsConsumed.put(item, amount + itemsConsumed.get(item));
        else
            itemsConsumed.put(item, amount);
        caloriesToday += item.getCalories() * amount;

        log.record(item, amount);
    }

    public float getWeightKg()
    {
        return weightKg;
    }

    public void setWeightKg(float weightKg)
    {
        this.weightKg = weightKg;
    }

    public void setWeightLbs(int weightLbs)
    {
        weightKg = ((float) weightLbs / 2.20462f);
    }

    public float getHeightCm()
    {
        return heightCm;
    }

    public void setHeightCm(float heightCm)
    {
        this.heightCm = heightCm;
    }

    public void setHeightInches(float heightInches)
    {
        float h = (heightInches * 2.54f);
        heightCm = (Math.round(h * 100.0f) / 100.0f);
    }

    public int[] getFeetAndInches()
    {
        int[] measures = new int[2];
        measures[0] = getHeightFeet();
        measures[1] = (measures[0] == 0) ? 0 : getHeightInches() % measures[0];
        return measures;
    }

    public int getHeightInches()
    {
        float i = (getHeightCm() * 0.393701f);
        return (int) (Math.round(i * 100.0) / 100.0);
    }

    public int getHeightFeet()
    {
        float h = (getHeightInches() * 0.0833333f);
        return (int) (Math.round(h * 100.0) / 100.0);
    }

    public int getWeightLbs()
    {
        float w = (getWeightKg() * 2.20462f);
        return (int) (Math.round(w * 100.0) / 100.0);
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

    public void delete(ContextWrapper contextWrapper)
    {
        if (MyApplicationContexts.getLatestContextWrapper(contextWrapper) == null)
        {
            Log.d("Profile", "Failed to delete profile. LatestContextWrapper is null.");
            return;
        }

        if (HomeActivity.getInstance() != null)
            HomeActivity.getInstance().finish();

        fullName = null;
        caloriesToday = 0;
        caloriesDailyMax = 0;
        itemsConsumed.clear();
        itemsConsumed = null;
        birthday = null;
        weightKg = 0;
        heightCm = 0;
        latestDayUsed = null;
        measurementSystem = null;
        theme = null;
        profile = null;

        SharedPreferences preferences = MyApplicationContexts.getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("fullName");
        editor.remove("height");
        editor.remove("weight");
        editor.remove("birthday");
        editor.remove("theme");
        editor.remove("measurement");
        editor.remove("calories");
        editor.remove("latestDayUsed");
        editor.remove("consumed");

        editor.commit();
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
        editor.putFloat("height", getHeightCm());
        editor.putFloat("weight", getWeightKg());
        editor.putString("birthday", birthdayString);
        editor.putString("measurement", measurementSystem.name());
        editor.putString("theme", theme.name());
        editor.putInt("calories", getCaloriesToday());
        editor.putInt("caloriesThisWeek", getLog().getCaloriesThisWeek());
        editor.putString("latestDayUsed", dateFormat.format(new Date()));

        Set<String> itemsConsumedStrings = new HashSet<>();
        //for (NutritionIXItem ixItem : itemsConsumed)
        //    itemsConsumedStrings.add(ixItem.toSaveString());
        for (NutritionIXItem ixItem : itemsConsumed.keySet())
        {
            Integer amount = itemsConsumed.get(ixItem);
            itemsConsumedStrings.add(ixItem.toSaveString() + "." + amount);
        }
        editor.putStringSet("consumed", itemsConsumedStrings);

        Set<String> itemsConsumedLogStrings = new HashSet<>();
        for (NutritionIXItem ixItem : getLog().getItemsConsumed().keySet())
        {
            Integer amount = itemsConsumed.get(ixItem);
            itemsConsumedLogStrings.add(ixItem.toSaveString() + "." + amount);
        }
        editor.putStringSet("consumedLog", itemsConsumedStrings);

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

    public int getPercentOfCaloriesConsumed()
    {
        float current = caloriesToday;
        float max = caloriesDailyMax;

        int percent = (int) ((current / max) * 100f);
        return percent;
    }

    public int getHoursLeftOfDay()
    {
        long timeNow = Calendar.getInstance().getTimeInMillis();

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);

        long timeLeft = tomorrow.getTimeInMillis() - timeNow;
        return (int) TimeUnit.MILLISECONDS.toHours(timeLeft);
    }

    public String getTip()
    {
        String tip = null;

        int hoursLeft = getHoursLeftOfDay();
        int percentLeft = 100 - getPercentOfCaloriesConsumed();

        if (itemsConsumed.isEmpty())
            tip = "You haven't eaten today? Get your metabolism started when you wake up. ";
        else if (hoursLeft >= 8 && percentLeft < 35)
            tip = "You're eating quite fast! You might want to slow down, the day is long.";

        return tip;
    }

    public ProfileLog getLog()
    {
        return log;
    }

    public Theme getTheme()
    {
        return theme;
    }

    public void setTheme(Theme theme)
    {
        this.theme = theme;
    }

    public enum MeasurementSystem
    {
        IMPERIAL, METRIC
    }

    public enum Theme
    {
        LIGHT, DARK
    }
}
