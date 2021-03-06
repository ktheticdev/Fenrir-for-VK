package dev.ragnarok.fenrir.api.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import dev.ragnarok.fenrir.api.model.VkApiMarket;

public class MarketDtoAdapter extends AbsAdapter implements JsonDeserializer<VkApiMarket> {

    @Override
    public VkApiMarket deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        VkApiMarket dto = new VkApiMarket();
        dto.id = optInt(root, "id");
        dto.owner_id = optInt(root, "owner_id");
        dto.weight = optInt(root, "weight");
        dto.availability = optInt(root, "availability");
        dto.date = optInt(root, "date");
        dto.title = optString(root, "title");
        dto.description = optString(root, "description");
        dto.thumb_photo = optString(root, "thumb_photo");
        dto.sku = optString(root, "sku");
        dto.access_key = optString(root, "access_key");
        dto.is_favorite = optBoolean(root, "is_favorite");
        if (root.has("dimensions")) {
            JsonObject dimensions = root.get("dimensions").getAsJsonObject();
            dto.dimensions = optInt(dimensions, "length") + "x" + optInt(dimensions, "width") + "x" + optInt(dimensions, "height") + " mm";
        }
        if (root.has("price")) {
            JsonObject price = root.get("price").getAsJsonObject();
            dto.price = optString(price, "text");
        }

        return dto;
    }
}
