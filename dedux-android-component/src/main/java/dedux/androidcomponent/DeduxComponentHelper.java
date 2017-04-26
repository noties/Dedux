package dedux.androidcomponent;

import javax.annotation.Nonnull;

import dedux.StateItem;
import dedux.Store;

interface DeduxComponentHelper {

    Store store();

    <T extends StateItem> void subscribeTo(
            @Nonnull Class<T> state,
            @Nonnull DeduxComponent.OnStateListener<? super T> listener
    );

    void unsubscribe();
}
