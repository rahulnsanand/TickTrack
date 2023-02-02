package com.theflopguyproductions.ticktrack.ui.utils.deletehelper;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.counter.CounterAdapter;

import org.jetbrains.annotations.NotNull;


public class CounterSlideDeleteHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public CounterSlideDeleteHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }
    @Override
    public boolean onMove(@NotNull RecyclerView recyclerView,@NotNull  RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
        return false;
    }
    @Override
    public void onSelectedChanged(@NotNull RecyclerView.ViewHolder viewHolder, int actionState) {
        try{
            final View foregroundView = ((CounterAdapter.counterDataViewHolder) viewHolder).counterLayout;
            if (foregroundView != null) {
                getDefaultUIUtil().onSelected(foregroundView);
            }
        } catch (NullPointerException e){
            listener.onSwiped(viewHolder, 0);
        }
    }

    @Override
    public void onChildDrawOver(@NotNull Canvas c, @NotNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((CounterAdapter.counterDataViewHolder) viewHolder).counterLayout;
        if (foregroundView != null) {
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((CounterAdapter.counterDataViewHolder) viewHolder).counterLayout;
        if (foregroundView != null) {
            getDefaultUIUtil().clearView(foregroundView);
        }
    }

    @Override
    public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView,
                            @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((CounterAdapter.counterDataViewHolder) viewHolder).counterLayout;
        if(foregroundView!=null){
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX/5, dY,
                    actionState, isCurrentlyActive);
        }

    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int position);
    }
}
