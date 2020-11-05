package com.alibaba.android.arouter.core

/**
 *
 * @author jerry
 * @created 2020/11/4 16:15
 */
object AccessMediator {
    fun getGroups(): Set<String>? = Warehouse.groupsIndex?.keys?.let { mutableSet -> HashSet<String>(mutableSet) }
}