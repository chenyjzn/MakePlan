package com.yuchen.makeplan.ext

import android.view.View

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (this.visibility == View.GONE && !value) return
        if (this.visibility == View.VISIBLE && value) return
        visibility = if (value) View.VISIBLE else View.GONE
    }