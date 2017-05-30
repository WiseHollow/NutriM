package net.johnbrooks.nutrim.wrapper;

import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by John on 5/30/2017.
 */

public class NutritionIXSearchResponse
{
    private NutritionIXResponseHeaders headers;
    private ItemsResponseBody body;

    public NutritionIXSearchResponse(HttpResponse<JsonNode> response)
    {
        body = new ItemsResponseBody(response.getBody());
        headers = new NutritionIXResponseHeaders(response.getHeaders());
    }

    public ItemsResponseBody getBodyDetails() { return body; }
    public NutritionIXResponseHeaders getHeaderDetails() { return headers; }

    /**
     * Created by John on 5/16/2017.
     */
    static class ItemsResponseBody
    {
        private NutritionIXItem[] items;

        public ItemsResponseBody(JsonNode body)
        {
            try
            {
                JSONArray hits = body.getObject().getJSONArray("hits");
                if (hits.length() == 0)
                {
                    System.out.println("Search returned no items.");
                    return;
                }
                items = new NutritionIXItem[hits.length()];
                for (int i = 0; i < hits.length(); i++)
                {
                    JSONObject jsonItem = hits.getJSONObject(i);
                    if (jsonItem == null)
                        continue;
                    JSONObject fields = jsonItem.has("fields") ? jsonItem.getJSONObject("fields") : null;
                    if (fields == null)
                        continue;

                    String id = fields.has("item_id") ? fields.getString("item_id") : null;
                    String name = fields.has("item_name") ? fields.getString("item_name") : null;
                    String brand = fields.has("brand_name") ? fields.getString("brand_name") : null;

                    String servingSize = fields.has("nf_serving_size_unit") ? fields.getString("nf_serving_size_unit") : null;

                    int calories = fields.has("nf_calories") ? fields.getInt("nf_calories") : 0;

                    items[i] = new NutritionIXItem(id, name, brand, calories, servingSize);
                }
            } catch (JSONException e)
            {
                Log.d(NutritionIXSearchResponse.class.getSimpleName(), body.getArray().toString());
                e.printStackTrace();
                return;
            }


        }

        public NutritionIXItem[] getItems() { return items; }
    }
}
