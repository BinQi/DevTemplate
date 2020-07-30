package wbq.frame.base.app

/**
 * Created by Jerry on 2020/5/20 18:37
 */
interface AppFactory {
    fun create(processName: String): CommonApp
}