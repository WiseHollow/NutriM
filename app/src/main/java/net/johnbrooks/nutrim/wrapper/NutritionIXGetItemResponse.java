package net.johnbrooks.nutrim.wrapper;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by John on 5/30/2017.
 */

public class NutritionIXGetItemResponse
{
    private NutritionIXResponseHeaders headers;
    private ItemResponseBody body;

    public NutritionIXGetItemResponse(HttpResponse<JsonNode> response)
    {
        body = new ItemResponseBody(response.getBody());
        headers = new NutritionIXResponseHeaders(response.getHeaders());
    }

    public ItemResponseBody getBodyDetails() { return body; }
    public NutritionIXResponseHeaders getHeaderDetails() { return headers; }

    /**
     * Created by John on 5/16/2017.
     */
    static class ItemResponseBody
    {
        private NutritionIXItem item;

        public ItemResponseBody(JsonNode body)
        {
            try
            {
                JSONObject jsonItem = body.getObject();
                if (jsonItem == null)
                    return;

                String id = jsonItem.has("item_id") ? jsonItem.getString("item_id") : null;
                String name = jsonItem.has("item_name") ? jsonItem.getString("item_name") : null;
                String brand = jsonItem.has("brand_name") ? jsonItem.getString("brand_name") : null;

                String servingSize = jsonItem.has("nf_serving_size_unit") ? jsonItem.getString("nf_serving_size_unit") : null;

                int calories = jsonItem.has("nf_calories") ? jsonItem.getInt("nf_calories") : 0;

                item = new NutritionIXItem(id, name, brand, calories, servingSize);
            } catch (JSONException e)
            {
                e.printStackTrace();
                return;
            }
        }

        public NutritionIXItem getItem() { return item; }
    }
}
