package net.johnbrooks.nutrim.wrapper;

import java.util.Map;

/**
 * Created by John on 5/30/2017.
 */

public class NutritionIXResponseHeaders
{
    private String date;
    private String status;
    private String connection;
    private String server;

    public NutritionIXResponseHeaders(Map<String, String> headers)
    {
        date = headers.get("date");
        status = headers.get("status");
        connection = headers.get("connection");
        server = headers.get("server");

        System.out.println("Received: " + status);
        //for (String key : headers.keySet())
        //    System.out.println(key + " | " + headers.get(key).toString());
    }

    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getConnection() { return connection; }
    public String getServer() { return server; }
}
