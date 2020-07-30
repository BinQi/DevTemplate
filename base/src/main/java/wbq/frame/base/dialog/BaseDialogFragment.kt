package wbq.frame.base.dialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 *
 * @author jerry
 * @created 2020/7/23 18:05
 */
open class BaseDialogFragment: DialogFragment() {
    override fun show(manager: FragmentManager, tag: String?) {
        if (!manager.isStateSaved) {
            super.show(manager, tag)
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int { // TODO
        return super.show(transaction, tag)
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (!manager.isStateSaved) {
            super.showNow(manager, tag)
        }
    }

    override fun dismiss() {
        val fm = parentFragmentManager
        if (fm != null && fm.isStateSaved) {
            return
        }
        super.dismiss()
    }
}