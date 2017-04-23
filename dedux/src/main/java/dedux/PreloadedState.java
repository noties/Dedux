package dedux;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class PreloadedState {

    private final Map<String, Object> map = new HashMap<>();

    public <T> PreloadedState add(@Nonnull T object) {
        map.put(object.getClass().getName(), object);
        return this;
    }

    public <T> PreloadedState add(@Nonnull String key, T object) {
        map.put(key, object);
        return this;
    }

    public Map<String, Object> build() {
        return new HashMap<>(map);
    }
}
