package dedux.androidcomponent.debug;

import dedux.StateItemBase;

public class DebugState extends StateItemBase {

    private String name;
    private int count;
    private boolean isTrue;

    public String name() {
        return name;
    }

    public int count() {
        return count;
    }

    public boolean isTrue() {
        return isTrue;
    }
}
