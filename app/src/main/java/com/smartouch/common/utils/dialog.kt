package com.smartouch.common.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.widget.TextView
import com.smartouch.R
import com.smartouch.common.interfaces.DialogAskListener

/**
 * Created by Jignesh Dangar on 20-04-2021.
 */
object dialog {

    private var dialog: Dialog? = null

    fun askAlert(
        activity: Activity,
        title: String,
        strYes: String,
        strNo: String,
        onClick: DialogAskListener?
    ) {

        hideDialog()
        dialog = Dialog(activity)
        dialog?.setContentView(R.layout.dialog_layout_ask)
        dialog?.setCancelable(false)

        val tvTitle = dialog?.findViewById(R.id.tv_dialog_title) as TextView
        val btnCancel = dialog?.findViewById(R.id.tv_cancel) as TextView
        val btnOk = dialog?.findViewById(R.id.tv_ok) as TextView

        tvTitle.text = title
        btnCancel.text = strNo
        btnOk.text = strYes

        btnCancel.setOnClickListener {
            dialog?.dismiss()
            onClick?.onNoClicked()
        }

        btnOk.setOnClickListener {
            dialog?.dismiss()
            onClick?.onYesClicked()
        }

        val displayMetrics = DisplayMetrics()
        activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * Constants.COMMON_DIALOG_WIDTH)
        val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(width.toInt(), height.toInt())
        dialog?.show()
    }

    fun hideDialog() {
        dialog?.let {
            if (it.isShowing){
                it.dismiss()
            }
        }
    }
}