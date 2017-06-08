package net.johnbrooks.nutrim.utilities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.github.lzyzsd.circleprogress.DonutProgress;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.activities.HomeActivity;

/**
 * Created by John on 6/6/2017.
 */

public class TaskUpdateProgressBar extends AsyncTask<String, Void, Void>
{
    private int progress;
    private DonutProgress progressBar;

    public TaskUpdateProgressBar(int progress, DonutProgress progressBar)
    {
        this.progress = progress;
        this.progressBar = progressBar;
    }

    @Override
    protected Void doInBackground(String... params)
    {
        long nextTick = System.currentTimeMillis() + 500;

        while (System.currentTimeMillis() < nextTick)
        {

        }

        nextTick = System.currentTimeMillis() + 25;

        for (int i = 0; i < progress; i++)
        {
            while(System.currentTimeMillis() < nextTick)
            {

            }

            publishProgress();
            nextTick = System.currentTimeMillis() + 25;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... voids)
    {
        progressBar.setProgress(progressBar.getProgress() + 1);
        if (progressBar.getProgress() >= 90)
           progressBar.setFinishedStrokeColor(ResourcesCompat.getColor(HomeActivity.getInstance().getResources(), R.color.progressBad, null));
        else
            progressBar.setFinishedStrokeColor(ResourcesCompat.getColor(HomeActivity.getInstance().getResources(), R.color.progressGood, null));
    }

    @Override
    protected void onPostExecute(Void v)
    {
        progressBar.setProgress(progress);
    }
}
