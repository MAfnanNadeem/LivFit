package life.mibo.fitbitsdk.gsonhelper.serializer;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class BigDecimalSerializer implements JsonSerializer<BigDecimal> {
    @Override
    public JsonElement serialize(final BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
        Number n = new Number() {
            @Override
            public long longValue() {
                return src.longValue();
            }

            @Override
            public int intValue() {
                return src.intValue();
            }

            @Override
            public float floatValue() {
                return src.floatValue();
            }

            @Override
            public double doubleValue() {
                return src.doubleValue();
            }

            @Override
            public String toString() {
                return src.toPlainString();
            }

        };

        return new JsonPrimitive(n);
    }
}
