package com.example.project3.uiComponents

import android.animation.LayoutTransition
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.IconCompat
import com.example.project3.Completion
import com.example.project3.R

class ProgressButton(
    text: String,
    changeText: String,
    icon: Drawable ,
    view: View,
    iconEnabled: Boolean = true,
    textColor: Int = Color.WHITE,
    completion: Completion? = null
) {
    private val text: String
    private val changeText: String
    private val icon: Drawable
    private val button: View
    private val cl: ConstraintLayout
    private var state: Boolean
    private val iconEnabled: Boolean
    private val textColor: Int
    private val comp: Completion?
    private val pb: ProgressBar


    init {
        this.text = text
        this.changeText = changeText
        this.icon = icon
        button = view
        cl = button.findViewById(R.id.cl)
        this.state = false
        this.iconEnabled = iconEnabled
        this.textColor = textColor
        comp = completion
        pb = view.findViewById(R.id.prg)
        cl.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        button.findViewById<TextView>(R.id.text).text = text
        button.findViewById<TextView>(R.id.text).setTextColor(textColor)
        button.findViewById<ImageView>(R.id.icon).setImageDrawable(icon)

        if (!iconEnabled)
            button.findViewById<ImageView>(R.id.icon).visibility = View.GONE


    }


    fun activate() {

        button.findViewById<TextView>(R.id.text).text = changeText
        animate(true)
        comp?.onComplete()

    }

    fun deactivate() {

        button.findViewById<TextView>(R.id.text).text = text
        animate(false)
        comp?.onCancelled(name = "button", message = "cancelled")

    }

    private fun animate(expand: Boolean) {
        pb.visibility = when (expand) {
            true -> View.VISIBLE
            else -> View.GONE
        }
        TransitionManager.beginDelayedTransition(cl, AutoTransition())
    }


}