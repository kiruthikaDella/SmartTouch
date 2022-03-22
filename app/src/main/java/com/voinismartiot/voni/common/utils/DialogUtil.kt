package com.voinismartiot.voni.common.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.DisplayMetrics
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.voinismartiot.voni.R
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.interfaces.DialogEditListener
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.Utils.toEditable

private var dialog: Dialog? = null

fun Activity.askAlert(
    title: String,
    strYes: String,
    strNo: String,
    onClick: DialogAskListener? = null
) {

    hideDialog()
    dialog = Dialog(this)
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
    this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val width = (displayMetrics.widthPixels * Constants.COMMON_DIALOG_WIDTH)
    val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.window?.setLayout(width.toInt(), height.toInt())
    dialog?.show()
}

fun Activity.deviceOfflineAlert(
    title: String? = null,
    onClick: DialogShowListener? = null
) {

    hideDialog()
    dialog = Dialog(this)
    dialog?.setContentView(R.layout.dialog_layout_device_offline)
    dialog?.setCancelable(false)

    val tvTitle = dialog?.findViewById(R.id.tv_dialog_title) as TextView
    val btnOk = dialog?.findViewById(R.id.tv_ok) as TextView

    title?.let {
        tvTitle.text = it
    }

    btnOk.setOnClickListener {
        if (onClick == null) {
            hideDialog()
        } else {
            onClick.onClick()
        }
    }

    val displayMetrics = DisplayMetrics()
    this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val width = (displayMetrics.widthPixels * Constants.COMMON_DIALOG_WIDTH)
    val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.window?.setLayout(width.toInt(), height.toInt())
    dialog?.show()
}

fun Activity.editDialog(
    title: String,
    strEditText: String,
    strYes: String,
    strNo: String,
    inputType: String = this.getString(R.string.dialog_input_type_text),
    isLimitedText: Boolean = true,
    onClick: DialogEditListener? = null
) {

    dialog = Dialog(this)
    dialog?.setContentView(R.layout.dialog_room_screen_edit)
    dialog?.setCancelable(false)

    val tvTitle = dialog?.findViewById(R.id.tv_title) as TextView
    val btnCancel = dialog?.findViewById(R.id.btn_cancel) as MaterialButton
    val btnSave = dialog?.findViewById(R.id.btn_save) as MaterialButton
    val editText = dialog?.findViewById(R.id.edt_edit) as EditText
    val editTextNoLimit = dialog?.findViewById(R.id.edt_edit_no_limit) as EditText

    tvTitle.text = title
    editText.text = strEditText.toEditable()
    editTextNoLimit.text = strEditText.toEditable()
    btnCancel.text = strNo
    btnSave.text = strYes

    if (isLimitedText) {
        editText.isVisible = true
        editTextNoLimit.isVisible = false
    } else {
        editText.isVisible = false
        editTextNoLimit.isVisible = true
    }

    if (inputType == this.getString(R.string.dialog_input_type_phone)) {
        editText.setRawInputType(InputType.TYPE_CLASS_PHONE)
        editTextNoLimit.setRawInputType(InputType.TYPE_CLASS_PHONE)
        editText.keyListener = DigitsKeyListener.getInstance("0123456789")
        editTextNoLimit.keyListener = DigitsKeyListener.getInstance("0123456789")
    }

    btnCancel.setOnClickListener {
        onClick?.onNoClicked()
    }

    btnSave.setOnClickListener {
        if (isLimitedText) {
            onClick?.onYesClicked(editText.text.toString().trim())
        } else {
            onClick?.onYesClicked(editTextNoLimit.text.toString().trim())
        }
    }

    val displayMetrics = DisplayMetrics()
    this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val width = (displayMetrics.widthPixels * 0.85.toFloat())
    val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.window?.setLayout(width.toInt(), height.toInt())
    dialog?.show()
}

fun Activity.featureDetailAlert(
    title: String,
    detail: String,
    onClick: DialogShowListener? = null
) {

    hideDialog()
    dialog = Dialog(this)
    dialog?.setContentView(R.layout.dialog_feature_detail)
    dialog?.setCancelable(false)

    val tvTitle = dialog?.findViewById(R.id.tv_title) as TextView
    val tvDetail = dialog?.findViewById(R.id.tv_detail) as TextView
    val tvClose = dialog?.findViewById(R.id.tv_close) as TextView

    if (title.isNotEmpty()) {
        tvTitle.text = title
    } else {
        tvTitle.visibility = View.GONE
    }

    tvDetail.text = detail

    tvClose.setOnClickListener {
        if (onClick == null) {
            hideDialog()
        } else {
            onClick.onClick()
        }
    }

    val displayMetrics = DisplayMetrics()
    this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val width = (displayMetrics.widthPixels * Constants.COMMON_DIALOG_WIDTH)
    val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.window?.setLayout(width.toInt(), height.toInt())
    dialog?.show()
}

fun Activity.loadingDialog(
    title: String? = null,
    isCancelable: Boolean = false
) {

    this.runOnUiThread {
        hideDialog()
        dialog = Dialog(this)
        dialog?.setContentView(R.layout.dialog_loading)
        dialog?.setCancelable(isCancelable)

        val tvTitle = dialog?.findViewById(R.id.tv_dialog_title) as TextView

        if (title == null) {
            tvTitle.isVisible = false
        } else {
            tvTitle.text = title
        }

        val displayMetrics = DisplayMetrics()
        this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * Constants.COMMON_DIALOG_WIDTH)
        val height = (displayMetrics.heightPixels * Constants.COMMON_DIALOG_HEIGHT)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(width.toInt(), height.toInt())
        dialog?.window?.setDimAmount(0.0f)
        dialog?.show()
    }

}

fun hideDialog() {
    dialog?.let {
        if (it.isShowing) {
            it.dismiss()
        }
    }
}