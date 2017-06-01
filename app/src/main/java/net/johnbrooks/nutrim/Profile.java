package net.johnbrooks.nutrim;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by John on 5/30/2017.
 */

public class Profile
{
    private static Profile profile;
    public static Profile getProfile()
    {
        return profile;
    }
    public static Profile createProfile(String name, Date birthday, int weightKg, int heightCm)
    {
        profile = new Profile(name, birthday, weightKg, heightCm);
        return profile;
    }

    private String name;
    private Date birthday;
    private int weightKg;
    private int heightCm;

    private int caloriesToday;

    private Profile(String name, Date birthday, int weightKg, int heightCm)
    {
        this.name = name;
        this.birthday = birthday;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.caloriesToday = 0;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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

    public void setCaloriesToday(int caloriesToday)
    {
        this.caloriesToday = caloriesToday;
    }

    public void addCaloriesToday(int calories)
    {
        caloriesToday += calories;
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
}
