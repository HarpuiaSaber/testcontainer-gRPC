package com.toannq.test.commons.util;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import com.toannq.test.commons.exception.DecodeException;
import com.toannq.test.commons.exception.EncodeException;
import com.toannq.test.commons.exception.JsonException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;

public final class Json {
    private static final byte[] NULL_VALUE = new byte[] {'n', 'u', 'l', 'l'};
    private static final DslJson<Object> dslJson =
            new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());

    private static final ThreadLocal<JsonReader<Object>> localJsonReader = ThreadLocal.withInitial(dslJson::newReader);
    private static final ThreadLocal<JsonWriter> localJsonWriter = ThreadLocal.withInitial(dslJson::newWriter);

    private static final MethodHandle RESETTER;

    static {
        try {
            var lookup = MethodHandles.privateLookupIn(JsonReader.class, MethodHandles.lookup());
            RESETTER = lookup.findVirtual(JsonReader.class, "reset", MethodType.methodType(void.class));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new JsonException("can't init RESETTER", e);
        }
    }

    private Json() {
    }

    @SuppressWarnings("unchecked")
    public static <T> JsonWriter.WriteObject<T> findWriter(Type type) {
        return (JsonWriter.WriteObject<T>) dslJson.tryFindWriter(type);
    }

    public static <T> byte[] encode(T obj, JsonWriter.WriteObject<T> objectWriter) {
        var jsonWriter = localJsonWriter.get();
        try {
            objectWriter.write(jsonWriter, obj);
            return jsonWriter.toByteArray();
        } finally {
            jsonWriter.reset();
        }
    }

    public static byte[] encode(Object obj) {
        if (obj == null) {
            return NULL_VALUE.clone();
        }
        var jsonWriter = localJsonWriter.get();
        try {
            if (dslJson.serialize(jsonWriter, obj.getClass(), obj)) {
                return jsonWriter.toByteArray();
            }
        } finally {
            jsonWriter.reset();
        }
        throw new EncodeException("Can't encode json object of type: " + obj.getClass());
    }

    public static void encode(Object obj, OutputStream outputStream) {
        if (obj == null) {
            try {
                outputStream.write(NULL_VALUE);
                return;
            } catch (IOException e) {
                throw new EncodeException("Can't encode json object", e);
            }
        }
        var jsonWriter = localJsonWriter.get();
        jsonWriter.reset(outputStream);
        try {
            if (dslJson.serialize(jsonWriter, obj.getClass(), obj)) {
                jsonWriter.flush();
                return;
            }
        } finally {
            jsonWriter.reset();
        }
        throw new EncodeException("Can't encode json object of type: " + obj.getClass());
    }

    public static String encodeToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        var jsonWriter = localJsonWriter.get();
        try {
            if (dslJson.serialize(jsonWriter, obj.getClass(), obj)) {
                return jsonWriter.toString();
            }
        } finally {
            jsonWriter.reset();
        }
        throw new EncodeException("Can't encode json object of type: " + obj.getClass());
    }

    public static <T> String encodeToString(T obj, JsonWriter.WriteObject<T> objectWriter) {
        if (obj == null) {
            return "null";
        }
        var jsonWriter = localJsonWriter.get();
        try {
            objectWriter.write(jsonWriter, obj);
            return jsonWriter.toString();
        } finally {
            jsonWriter.reset();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> JsonReader.ReadObject<T> findReader(Type type) {
        return (JsonReader.ReadObject<T>) dslJson.tryFindReader(type);
    }

    public static <T> T decode(InputStream inputStream, Class<T> clazz) {
        try {
            return dslJson.deserialize(clazz, inputStream);
        } catch (IOException e) {
            throw new DecodeException("can't decode json", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T decode(InputStream inputStream, Type type) {
        try {
            return (T) dslJson.deserialize(type, inputStream);
        } catch (IOException e) {
            throw new DecodeException("can't decode json", e);
        }
    }

    public static <T> T decode(byte[] data, Class<T> clazz) {
        try {
            return dslJson.deserialize(clazz, data, data.length);
        } catch (IOException e) {
            throw new DecodeException("can't decode json", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T decode(byte[] data, Type type) {
        try {
            return (T) dslJson.deserialize(type, data, data.length);
        } catch (IOException e) {
            throw new DecodeException("can't decode json", e);
        }
    }

    public static <T> T decode(InputStream inputStream, final JsonReader.ReadObject<T> objectReader) {
        var jsonReader = localJsonReader.get();
        try {
            jsonReader.process(inputStream);
            jsonReader.getNextToken();
            return objectReader.read(jsonReader);
        } catch (IOException e) {
            throw new DecodeException("can't decode json", e);
        } finally {
            reset(jsonReader);
        }
    }

    public static <T> T decode(byte[] data, final JsonReader.ReadObject<T> objectReader) {
        var jsonReader = localJsonReader.get();
        try {
            jsonReader.process(data, data.length);
            jsonReader.getNextToken();
            return objectReader.read(jsonReader);
        } catch (IOException e) {
            throw new DecodeException("can't decode json", e);
        } finally {
            reset(jsonReader);
        }
    }

    private static void reset(JsonReader<?> jsonReader) {
        try {
            RESETTER.invokeExact(jsonReader);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * fix sonar-hint
     */
    public static void unload() {
        localJsonReader.remove();
        localJsonWriter.remove();
    }
}
