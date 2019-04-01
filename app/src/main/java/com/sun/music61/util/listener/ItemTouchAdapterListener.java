package com.sun.music61.util.listener;

public interface ItemTouchAdapterListener {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemMoved();
}
