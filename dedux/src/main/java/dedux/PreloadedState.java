package dedux;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class PreloadedState {

    private final Map<Class<? extends StateItem>, StateItem> map = new HashMap<>();

    public <S extends StateItem> PreloadedState add(@Nonnull S stateItem) {
        map.put(stateItem.getClass(), stateItem);
        return this;
    }

    public Map<Class<? extends StateItem>, StateItem> build() {
        return new HashMap<>(map);
    }
}
