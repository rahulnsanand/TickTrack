package com.theflopguyproductions.ticktrack.ui.utils.deletehelper;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.timer.data.TimerAdapter;


public class TimerSlideDeleteHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public TimerSlideDeleteHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        System.out.println("<<<<<<<<<<<onMove<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<,,,,,");

        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            if(!((TimerAdapter.timerDataViewHolder) viewHolder).isRunning){
                final View foregroundView = ((TimerAdapter.timerDataViewHolder) viewHolder).timerLayout;

                getDefaultUIUtil().onSelected(foregroundView);
            }

        }
    }

    @Override
    public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if(((TimerAdapter.timerDataViewHolder) viewHolder).isRunning) return 0;

        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        if(!((TimerAdapter.timerDataViewHolder) viewHolder).isRunning){
            final View foregroundView = ((TimerAdapter.timerDataViewHolder) viewHolder).timerLayout;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((TimerAdapter.timerDataViewHolder) viewHolder).timerLayout;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if(!((TimerAdapter.timerDataViewHolder) viewHolder).isRunning){
            final View foregroundView = ((TimerAdapter.timerDataViewHolder) viewHolder).timerLayout;

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX/5, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(!((TimerAdapter.timerDataViewHolder) viewHolder).isRunning){
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
