package ru.noties.dedux.sample.state;

import javax.annotation.Nonnull;

import ru.noties.dedux.sample.state.core.Apply;
import ru.noties.dedux.sample.state.core.BaseState;

public class NavigatorState extends BaseState {

    public final boolean goBack;
    public final boolean finish;

    public NavigatorState(boolean goBack, boolean finish) {
        this.goBack = goBack;
        this.finish = finish;
    }

    @Nonnull
    @Override
    public <T extends BaseState> T clone(@Nonnull Apply<T> apply) {
        throw new RuntimeException("Should not clone this state");
    }

    @Override
    public String toString() {
        return "NavigatorState{" +
                "goBack=" + goBack +
                ", finish=" + finish +
                '}';
    }
}
