package ir.ayantech.whygoogle.custom;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.alirezabdn.whyfinal.adapter.FragmentStateAdapter;

/**
 * {@link ViewHolder} implementation for handling {@link Fragment}s. Used in
 * {@link FragmentStateAdapter}.
 */
public final class MyFragmentViewHolder extends ViewHolder {
    private MyFragmentViewHolder(@NonNull FrameLayout container) {
        super(container);
    }

    @NonNull static MyFragmentViewHolder create(@NonNull ViewGroup parent) {
        FrameLayout container = new FrameLayout(parent.getContext());
        container.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        container.setId(ViewCompat.generateViewId());
        container.setSaveEnabled(false);
        return new MyFragmentViewHolder(container);
    }

    @NonNull FrameLayout getContainer() {
        return (FrameLayout) itemView;
    }
}
