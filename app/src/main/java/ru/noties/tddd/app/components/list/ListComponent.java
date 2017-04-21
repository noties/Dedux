package ru.noties.tddd.app.components.list;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ru.noties.debug.Debug;
import ru.noties.tddd.app.components.ComponentHelper;
import ru.noties.tddd.app.model.TodosState;
import ru.noties.tddd.app.model.ToggleTodoAction;
import ru.noties.tddd.data.Todo;
import ru.noties.tddd.sample.R;
import ru.noties.tddd.utils.CollectionUtils;
import ru.noties.tddd.utils.ViewUtils;
import ru.noties.vt.ViewTypesAdapter;

public class ListComponent extends FrameLayout {

    private ComponentHelper helper;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ViewTypesAdapter<Item> adapter;

    private boolean selfChange;

    public ListComponent(Context context) {
        super(context);
        init(context, null);
    }

    public ListComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        helper = ComponentHelper.install(context);
        if (helper == null) {
            if (!isInEditMode()) {
                throw new IllegalStateException();
            }
        }

        inflate(context, R.layout.view_list, this);

        adapter = ViewTypesAdapter.builder(Item.class)
                .register(Item.TodoItem.class, new TodoItemViewType(), ((item, holder) -> {
                    helper.store().dispatch(new ToggleTodoAction(((Item.TodoItem) item).id));
                }))
                .setHasStableIds(true)
                .build(context);

        recyclerView = ViewUtils.findView(this, R.id.list_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (helper != null) {
            // for now, then we will create standalone state for list (to show also filtered items)
            helper.attach(TodosState.class, this::render);
            helper.attach(ListScrollState.class, this::renderScroll);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    recyclerView.removeCallbacks(scrollChangedRunnable);
                    recyclerView.postDelayed(scrollChangedRunnable, 1000L);
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (helper != null) {
            helper.detach();
        }
    }

    private void render(@Nullable TodosState state) {

        adapter.setItems(createItems(state));

        final boolean scrollToLast = state != null && state.scrollToLast();
        if (scrollToLast) {
            recyclerView.post(() -> recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1));
        }
    }

    private void renderScroll(@Nullable ListScrollState state) {
        if (state != null && !selfChange) {
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

        helper.store().dispatch(new ScrollAction(outPosition, outOffset));

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
