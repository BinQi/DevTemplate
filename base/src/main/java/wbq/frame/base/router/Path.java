package wbq.frame.base.router;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author jerry
 * @created 2020/6/3 20:04
 */
@StringDef({
        Path.activityMain
})
@Retention(RetentionPolicy.SOURCE)
public @interface Path {
    String activityMain = PathType.activity + "/main";
}
