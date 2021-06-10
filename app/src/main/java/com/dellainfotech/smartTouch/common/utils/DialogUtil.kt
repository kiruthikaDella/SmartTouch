package com.dellainfotech.smartTouch.common.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.interfaces.DialogEditListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Utils.toEditable
import com.google.android.material.button.MaterialButton

/**
 * Created by Jignesh Dangar on 20-04-2021.
 */
object DialogUtil {

    private var dialog: Dialog? = null

    fun askAlert(
        activity: Activity,
        title: String,
        strYes: String,
        strNo: String,
        onClick: DialogAskListener? = null
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

    fun deviceOfflineAlert(
        activity: Activity,
        onClick: DialogShowListener?
    ) {

        hideDialog()
        dialog = Dialog(activity)
        dialog?.setContentView(R.layout.dialog_layout_device_offline)
        dialog?.setCancelable(false)

        val btnOk = dialog?.findViewById(R.id.tv_ok) as TextView

        btnOk.setOnClickListener {
            onClick?.onClick()
        }

        val displayMetrics = DisplayMetrics()
        activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * Constants.COMMON_DIALOG_WIDTH)
        val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(width.toInt(), height.toInt())
        dialog?.show()
    }

    fun editDialog(
        activity: Activity,
        title: String,
        strEditText: String,
        strYes: String,
        strNo: String,
        onClick: DialogEditListener? = null
    ) {

        dialog = Dialog(activity)
        dialog?.setContentView(R.layout.dialog_room_screen_edit)
        dialog?.setCancelable(false)

        val tvTitle = dialog?.findViewById(R.id.tv_title) as TextView
        val btnCancel = dialog?.findViewById(R.id.btn_cancel) as MaterialButton
        val btnSave = dialog?.findViewById(R.id.btn_save) as MaterialButton
        val editText = dialog?.findViewById(R.id.edt_edit) as EditText

        tvTitle.text = title
        editText.text = strEditText.toEditable()
        btnCancel.text = strNo
        btnSave.text = strYes

        btnCancel.setOnClickListener {
            onClick?.onNoClicked()
        }

        btnSave.setOnClickListener {
            onClick?.onYesClicked(editText.text.toString().trim())
        }

        val displayMetrics = DisplayMetrics()
        activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.85.toFloat())
        val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(width.toInt(), height.toInt())
        dialog?.show()
    }

    fun loadingAlert(
        activity: Activity,
        title: String? = null,
        isCancelable: Boolean = false
    ) {

        hideDialog()
        dialog = Dialog(activity)
        dialog?.setContentView(R.layout.dialog_loading)
        dialog?.setCancelable(isCancelable)

        val tvTitle = dialog?.findViewById(R.id.tv_dialog_title) as TextView

        if (title == null) {
            tvTitle.isVisible = false
        } else {
            tvTitle.text = title
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
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}