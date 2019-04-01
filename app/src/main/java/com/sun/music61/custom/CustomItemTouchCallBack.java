package com.sun.music61.custom;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.sun.music61.util.listener.ItemTouchAdapterListener;
import com.sun.music61.util.listener.ItemTouchViewHolderListener;

public class CustomItemTouchCallBack extends ItemTouchHelper.Callback {

    private static final float ALPHA_FULL = 1.0f;
    private final ItemTouchAdapterListener mListener;

    public CustomItemTouchCallBack(ItemTouchAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source,
            @NonNull RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        mListener.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder instanceof ItemTouchViewHolderListener) {
            ItemTouchViewHolderListener itemViewHolder = (ItemTouchViewHolderListener) viewHolder;
            itemViewHolder.onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        mListener.onItemMoved();
        if (viewHolder instanceof ItemTouchViewHolderListener) {
            ItemTouchViewHolderListener itemViewHolder = (ItemTouchViewHolderListener) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
}
