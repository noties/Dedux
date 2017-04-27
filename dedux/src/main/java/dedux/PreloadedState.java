//package dedux;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Nonnull;
//
//public class PreloadedState {
//
//    private final List<StateItem> list = new ArrayList<>();
//
//    public <S extends StateItem> PreloadedState add(@Nonnull S stateItem) {
//        list.add(stateItem);
//        return this;
//    }
//
//    public PreloadedState addAll(@Nonnull Collection<? extends StateItem> collection) {
//        list.addAll(collection);
//        return this;
//    }
//
//    public List<StateItem> build() {
//        return new ArrayList<>(list);
//    }
//}
