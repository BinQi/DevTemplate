package wbq.frame.base.router;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author jerry
 * @created 2020/6/3 20:04
 */
@StringDef({
        PathType.activity,
        PathType.service,
        PathType.interfaces,
})
@Retention(RetentionPolicy.SOURCE)
public @interface PathType {
    String activity = "/activity";
    String service = "/service";
    String interfaces = "/interfaces";
}
