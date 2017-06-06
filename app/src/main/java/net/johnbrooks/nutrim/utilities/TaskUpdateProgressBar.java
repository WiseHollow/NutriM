package net.johnbrooks.nutrim.utilities;

import android.os.AsyncTask;

import com.github.lzyzsd.circleprogress.DonutProgress;

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

        for (int i = 0; i < progress + 1; i++)
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
    }

    @Override
    protected void onPostExecute(Void v)
    {

    }
}
