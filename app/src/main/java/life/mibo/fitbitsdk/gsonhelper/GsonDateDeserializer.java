package life.mibo.fitbitsdk.gsonhelper;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import life.mibo.fitbitsdk.gsonhelper.time.FastDateFormat;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GsonDateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private static final String TAG = GsonDateDeserializer.class.getSimpleName();
    private static final String UNKNOWN_FORMAT_ERROR_LOG = "No matching date format found";

    private static final FastDateFormat ISO_8601 =
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final SimpleDateFormat TOKEN_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    public static final SimpleDateFormat ISO_DATETIME_SPLIT_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
    public static final SimpleDateFormat ISO_DATETIME_TIME_ZONE_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.US);
    public static final SimpleDateFormat ISO_DATETIME_TIME_ZONE_SPLIT_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ", Locale.US);
    public static final SimpleDateFormat ISO_DATETIME_NO_MILLI
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    public static final FastDateFormat GSON_SERIALIZED_FORMAT
            = FastDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);

    private static Format[] DATE_FORMATS = {
            ISO_DATETIME_TIME_ZONE_SPLIT_FORMAT,
            TOKEN_TIMESTAMP_FORMAT,
            ISO_DATETIME_SPLIT_FORMAT,
            ISO_DATETIME_TIME_ZONE_FORMAT,
            ISO_DATETIME_NO_MILLI,
            GSON_SERIALIZED_FORMAT
    };

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String j = json.getAsJsonPrimitive().getAsString();
        return parseDate(j);
    }

    public static Date parseDate(String dateString) {
        if (dateString != null && dateString.trim().length() > 0) {
            for (Format format : DATE_FORMATS) {
                try {
                    if (format instanceof SimpleDateFormat) {
                        return ((SimpleDateFormat) format).parse(dateString);
                    } else {
                        return ((FastDateFormat) format).parse(dateString);
                    }
                } catch (ParseException ignored) {
                }
            }
        }
        Log.w(TAG, UNKNOWN_FORMAT_ERROR_LOG);
        return null;
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(ISO_8601.format(src.getTime()));
    }
}


