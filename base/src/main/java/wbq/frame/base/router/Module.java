package wbq.frame.base.router;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author jerry
 * @created 2020/6/3 20:00
 */
@StringDef({
        Module.yw_main
})
@Retention(RetentionPolicy.SOURCE)
public @interface Module {
    String yw_main = "/yw_main";
}
