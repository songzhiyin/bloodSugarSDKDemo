package com.cgmcare.bloodsugarsdkdemo

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity() : AppCompatActivity() {
    private val TAG = "BleCgmCare"
    protected open fun logIMessage(msg: String?) {
        Log.i(TAG, msg!!)
    }
}