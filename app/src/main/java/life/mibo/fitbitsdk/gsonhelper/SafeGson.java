package life.mibo.fitbitsdk.gsonhelper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import life.mibo.fitbitsdk.gsonhelper.serializer.BigDecimalSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This wrapper should suppress errors from JSON
 * syntax parsing exceptions.
 */
public class SafeGson {

    public static final String TAG = "SafeGSON";
    private Gson gsonInstance;
    public Map<Type, Object> typeAdapterMap = new HashMap<>();
    private static SafeGson singleton;

    public Gson getGsonInstance() {
        if (gsonInstance == null) {
            build();
        }
        return gsonInstance;
    }

    public SafeGson build() {
        GsonBuilder builder = new GsonBuilder();

        for (Type t : getSingleton().typeAdapterMap.keySet()) {
            builder.registerTypeAdapter(t, getSingleton().typeAdapterMap.get(t));
        }

        //Add in the defaults that are used often
        if (!getSingleton().typeAdapterMap.containsKey(Date.class)) {
            builder.registerTypeAdapter(Date.class, new GsonDateDeserializer());
        }
        if (!getSingleton().typeAdapterMap.containsKey(BigDecimal.class)) {
            builder.registerTypeAdapter(BigDecimal.class, new BigDecimalSerializer());
        }

        getSingleton().gsonInstance = builder.create();
        return getSingleton();
    }

    /**
     * Pass in a type adapter for special serialization / deserialization cases
     *
     * @see "https://github.com/google/gson/blob/master/UserGuide.md#custom-serialization-and-deserialization"
     *
     * @param type the type of the object
     * @param typeAdapter Must implement JsonDeserializer, JsonSerializer, InstanceCreator, or a
     *                    combination of the three.  If implementing more than one, they will
     *                    be registered for all 3 types.
     * @return this SafeGson singleton object
     */
    public SafeGson registerTypeAdapter(Type type, Object typeAdapter) {
        getSingleton().typeAdapterMap.put(type, typeAdapter);
        return getSingleton();
    }

    public static <T> T fromJson(String json, Class<T> inClass) {
        try {
            return getSingleton().getGsonInstance().fromJson(json, inClass);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Failed to parse " + (inClass == null ? "null" : inClass.getName()) + " JSON: " + json);
        }
        return null;
    }


    public static <T> T fromJson(String json, Type inType) {
        try {
            return getSingleton().getGsonInstance().fromJson(json, inType);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Failed to parse " + (inType == null ? "null" : inType.toString()) + " JSON: " + json);
        }
        return null;
    }

    public static String toJson(Object obj) {
        return getSingleton().getGsonInstance().toJson(obj);
    }

    public static SafeGson getSingleton() {
        if (singleton == null) {
            singleton = new SafeGson();
        }
        return singleton;
    }

    public static SafeGson getInstance() {
        return new SafeGson();
    }
}
