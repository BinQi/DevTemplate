package wbq.frame.base.router

import androidx.annotation.StringDef

/**
 *
 * @author jerry
 * @created 2020/7/31 16:01
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@MustBeDocumented
@StringDef(Module.yw_main, Path.activityMain)
@Retention(AnnotationRetention.SOURCE)
annotation class Ab