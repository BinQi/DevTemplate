package wbq.frame.base.router

/**
 *
 * @author jerry
 * @created 2020/7/31 16:01
 */
open class A {
    open val bbbb = 1
    val aaaa = 1
    companion object {
        const val abbbbbbb = 1
        val cbbbbbbb = 1
        var dbbbbbbb = 1

        fun get(): Int {
            return abbbbbbb
        }
    }

    object FF {
        val aaaa = 1
        var bbbb = 1
        const val cccc = 1
        fun getFF(): Int {
            return 0
        }
    }
}