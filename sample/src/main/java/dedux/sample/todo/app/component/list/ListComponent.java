package dedux.sample.todo.app.component.list;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.androidcomponent.DeduxComponent;
import dedux.sample.todo.R;
import dedux.sample.todo.model.Todo;
import dedux.sample.todo.store.state.ListScrollState;
import dedux.sample.todo.store.action.ScrollAction;
import dedux.sample.todo.store.state.TodosState;
import dedux.sample.todo.store.action.ToggleTodoAction;
import dedux.sample.todo.utils.CollectionUtils;
import ru.noties.vt.ViewTypesAdapter;

public class ListComponent extends DeduxComponent {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ViewTypesAdapter<Item> adapter;

    private boolean selfChange;

    public ListComponent(Context context) {
        super(context);
    }

    public ListComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreated(@Nonnull Context context, @Nullable AttributeSet set) {

        inflate(context, R.layout.view_list, this);

        adapter = ViewTypesAdapter.builder(Item.class)
                .register(Item.TodoItem.class, new TodoItemViewType(), ((item, holder) -> {
                    store().dispatch(new ToggleTodoAction(((Item.TodoItem) item).id));
                }))
                .setHasStableIds(true)
                .build(context);

        recyclerView = findView(R.id.list_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onAttached() {

        subscribeTo(TodosState.class, this::render);
        subscribeTo(ListScrollState.class, this::renderScroll);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                recyclerView.removeCallbacks(scrollChangedRunnable);
                recyclerView.postDelayed(scrollChangedRunnable, 1000L);
            }
        });
    }

    private void render(@Nonnull TodosState state) {

        adapter.setItems(createItems(state));

        final boolean scrollToLast = state.scrollToLast();
        if (scrollToLast) {
            // todo, we should indicate that we have scrolled
            recyclerView.post(() -> {
                selfChange = true;
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                selfChange = false;
            });
        }
    }

    private void renderScroll(@Nonnull ListScrollState state) {
        if (!selfChange) {
            layoutManager.scrollToPositionWithOffset(state.position(), state.offset());
        }
    }

    private final Runnable scrollChangedRunnable = () -> {

        final int outPosition;
        final int outOffset;

        final int position = layoutManager.findFirstVisibleItemPosition();
        if (position > -1) {
            outPosition = position;

            final RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                final View view = holder.itemView;
                if (view != null) {
                    outOffset = view.getTop();
                } else {
                    outOffset = 0;
                }
            } else {
                outOffset = 0;
            }

        } else {
            outPosition = 0;
            outOffset = 0;
        }

        selfChange = true;

        store().dispatch(new ScrollAction(outPosition, outOffset));

        selfChange = false;
    };

    private static List<Item> createItems(TodosState state) {
        final List<Item> list;
        if (state == null
                || CollectionUtils.isEmpty(state.todos())) {
            list = null;
        } else {
            final List<Todo> todos = state.todos();
            final int size = todos.size();
            list = new ArrayList<>(size);
            Todo todo;
            for (int i = 0; i < size; i++) {
                todo = todos.get(i);
                list.add(new Item.TodoItem(todo.id(), todo.getName(), todo.isDone()));
            }
        }
        return list;
    }


}
