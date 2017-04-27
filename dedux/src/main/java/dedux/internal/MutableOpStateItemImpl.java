package dedux.internal;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.StateItem;

class MutableOpStateItemImpl<S extends StateItem> extends MutableOpImpl<S> {

    private final MutableState.Storage storage;

    MutableOpStateItemImpl(@Nonnull MutableState.Storage storage, @Nonnull S s) {
        super(s);
        this.storage = storage;
    }

    @Override
    public void set(@Nonnull S s) {
        storage.set(s);
        super.set(s);
    }
}
