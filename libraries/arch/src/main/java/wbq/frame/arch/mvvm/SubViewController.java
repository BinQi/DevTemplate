package wbq.frame.arch.mvvm;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Jerry on 2019-09-11 20:22
 */
public class SubViewController extends ViewController {
    private @Nullable final View mContentView;

    public SubViewController(@NonNull ViewController parent, @Nullable View contentView) {
        super(parent);
        mContentView = contentView;
    }

    public final View getContentView() {
        return mContentView;
    }

    public final <T extends View> T findViewById(@IdRes int id) {
        return mContentView != null ? mContentView.findViewById(id) : null;
    }
}
