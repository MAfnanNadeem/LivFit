package life.mibo.hexa.view.backdrop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;

// scroll issue with backdrop behaviour while child is recyclerview #FIXED
// https://stackoverflow.com/questions/32404979/dont-collapse-toolbar-when-recyclerview-fits-the-screen/32923226#32923226
public class ToolbarBehavior extends AppBarLayout.Behavior {

    private boolean scrollableRecyclerView = false;
    private int count;

    public ToolbarBehavior() {
    }

    public ToolbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return scrollableRecyclerView && super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        updatedScrollable(directTargetChild);
        return scrollableRecyclerView && super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        return scrollableRecyclerView && super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    private void updatedScrollable(View directTargetChild) {
        if (directTargetChild instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) directTargetChild;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                if (adapter.getItemCount() != count) {
                    scrollableRecyclerView = false;
                    count = adapter.getItemCount();
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int lastVisibleItem = 0;
                        if (layoutManager instanceof LinearLayoutManager) {
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                            lastVisibleItem = Math.abs(linearLayoutManager.findLastCompletelyVisibleItemPosition());
                        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                            int[] lastItems = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(new int[staggeredGridLayoutManager.getSpanCount()]);
                            lastVisibleItem = Math.abs(lastItems[lastItems.length - 1]);
                        }
                        scrollableRecyclerView = lastVisibleItem < count - 1;
                    }
                }
            }
        } else scrollableRecyclerView = true;
    }
}