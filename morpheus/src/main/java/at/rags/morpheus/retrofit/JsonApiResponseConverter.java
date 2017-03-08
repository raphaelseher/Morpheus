package at.rags.morpheus.retrofit;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import at.rags.morpheus.JsonApiObject;
import at.rags.morpheus.Morpheus;
import at.rags.morpheus.Resource;
import okhttp3.ResponseBody;
import retrofit2.Converter;


class JsonApiResponseConverter<T> implements Converter<ResponseBody, T> {

    private Morpheus morpheus;
    private Class<T> typeClass;

    JsonApiResponseConverter(Morpheus morpheus, Class<T> typeClass) {
        this.morpheus = morpheus;
        this.typeClass = typeClass;
        Log.d("JSONApi", "Converter type: " + typeClass);
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        BufferedReader bfr = new BufferedReader(value.charStream());
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = bfr.readLine()) != null) {
            sb.append(line);
        }
        try {
            JsonApiObject jsonApiObject = morpheus.parse(sb.toString());
            if (List.class.isAssignableFrom(typeClass)) {
                return (T) jsonApiObject.getResources();
            } else if (Resource.class.isAssignableFrom(typeClass)) {
                return (T) jsonApiObject.getResource();
            } else {
                return (T) jsonApiObject;
            }
        } catch (Exception e) {
            Log.d("JSONApi", "Failed parsing JsonApi response.", e);
        }
        return null;
    }
}
