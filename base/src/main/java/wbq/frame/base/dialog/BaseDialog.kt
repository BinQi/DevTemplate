package wbq.frame.base.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context

/**
 *
 * @author jerry
 * @created 2020/7/23 18:06
 */
open class BaseDialog : Dialog {
    protected var mActivity: Activity? = null

    constructor(act: Activity): this(act, false)

    constructor(act: Activity, style: Int): this(act, style, false)

    constructor(act: Activity, cancelOutside: Boolean): this(act, 0, cancelOutside)

    constructor(act: Activity, style: Int, cancelOutside: Boolean): super(act, style) {
        mActivity = act
        setCanceledOnTouchOutside(cancelOutside)
    }

    override fun show() {
        if (mActivity!!.isFinishing || mActivity!!.isDestroyed) {
            return
        }
        super.show()
    }

    override fun dismiss() {
        super.dismiss()
    }

    /**
     * 设置Dialog的大小<br></br>
     *
     * @param width
     * @param height
     */
    open fun setSize(width: Int, height: Int) {
        window!!.setLayout(width, height)
    }

    /**
     * 设置Dialog的显示位置<br></br>
     *
     *
     * lp.x与lp.y表示相对于原始位置的偏移.
     * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
     * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
     * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
     * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
     * 当参数值包含Gravity.CENTER_HORIZONTAL时
     * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
     * 当参数值包含Gravity.CENTER_VERTICAL时
     * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向下移动,负值向上移动.
     * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
     * Gravity.CENTER_VERTICAL.
     *
     *
     * @param x
     * @param y
     * @param gravity
     */
    open fun setXY(x: Int, y: Int, gravity: Int) {
        val window = window
        val lp = window!!.attributes
        lp.x = x
        lp.y = y
        window.setGravity(gravity)
    }

    /**
     * 获取文本
     */
    protected open fun getString(resId: Int): String? {
        return mActivity!!.getString(resId)
    }
}