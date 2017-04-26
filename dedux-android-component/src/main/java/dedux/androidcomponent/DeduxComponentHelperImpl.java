package dedux.androidcomponent;

import android.content.Context;

import javax.annotation.Nonnull;

import dedux.CompositeSubscription;
import dedux.Consumer;
import dedux.StateItem;
import dedux.Store;
import dedux.Subscription;

class DeduxComponentHelperImpl implements DeduxComponentHelper {

    static DeduxComponentHelper install(@Nonnull Context context) {
        final StoreHolder holder;
        if (context instanceof StoreHolder) {
            holder = (StoreHolder) context;
        } else if (context.getApplicationContext() instanceof StoreHolder) {
            holder = (StoreHolder) context.getApplicationContext();
        } else {
            throw new IllegalStateException("Context, nor application context implements the " + StoreHolder.class.getName());
        }
        return new DeduxComponentHelperImpl(holder.store());
    }

    private final Store store;
    private CompositeSubscription subscription;

    private DeduxComponentHelperImpl(@Nonnull Store store) {
        this.store = store;
    }

    @Override
    public Store store() {
        return this.store;
    }

    @Override
    public <T extends StateItem> void subscribeTo(@Nonnull Class<T> state, @Nonnull final DeduxComponent.OnStateListener<? super T> listener) {

        if (subscription == null
                || subscription.isUnsubscribed()) {
            subscription = new CompositeSubscription();
        }

        store.state()
                .get(state)
                .subscribe(true, subscription.compose(new Consumer<T>() {
                    @Override
                    public void apply(@Nonnull Subscription subscription, @Nonnull T t) {
                        listener.apply(t);
                    }
                }));
    }

    @Override
    public void unsubscribe() {
        if (subscription != null) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscription = null;
        }
    }
}
