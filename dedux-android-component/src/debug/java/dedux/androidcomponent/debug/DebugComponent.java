package dedux.androidcomponent.debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.androidcomponent.DeduxComponent;
import dedux.androidcomponent.R;

public class DebugComponent extends DeduxComponent {

    private TextView textView;

    public DebugComponent(Context context) {
        super(context);
    }

    public DebugComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreated(@Nonnull Context context, @Nullable AttributeSet set) {

        inflate(context, R.layout.debug_component, this);

        textView = (TextView) findViewById(R.id.debug_component_text);
    }

    @Override
    protected void onAttached() {
        subscribeTo(DebugState.class, new OnStateListener<DebugState>() {
            @Override
            public void apply(@Nonnull DebugState state) {
                render(state);
            }
        });
    }

    private void render(@Nonnull DebugState state) {
        @SuppressLint("DefaultLocale")
        final String text = String.format("%s : %d", state.name(), state.count());
        final int color = state.isTrue()
                ? 0xFFe91e63
                : 0xFF7986cb;
        textView.setText(text);
        ((View) textView.getParent()).setBackgroundColor(color);
    }
}
