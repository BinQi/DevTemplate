package wbq.frame.arch.mvvm;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Jerry on 2019-09-11 20:22
 */
public class SubViewController extends ViewController {
    private final View mContentView;

    public SubViewController(@NonNull ViewController parent, @NonNull View contentView) {
        super(parent);
        mContentView = contentView;
    }

    public View getContentView() {
        return mContentView;
    }
}
