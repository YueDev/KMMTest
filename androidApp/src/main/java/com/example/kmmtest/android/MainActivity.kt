package com.example.kmmtest.android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kmmtest.android.x_collage.XBitmap
import com.example.kmmtest.android.x_collage.XBitmapSimple
import com.example.kmmtest.android.x_collage.XCollageView
import kotlinx.coroutines.launch
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val collageView: XCollageView by lazy { findViewById(R.id.collageView) }
    private val button1: View by lazy { findViewById(R.id.button1) }
    private val button2: View by lazy { findViewById(R.id.button2) }
    private val button3: View by lazy { findViewById(R.id.button3) }
    private val loadingView: View by lazy { findViewById(R.id.progressBar) }

    private var ratio = ratioList[0]
    private var border = borderList[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyInsets(R.id.layout_root)

        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            val xBitmaps = imageResList.map {
                val bitmap = getBitmapWithRes(this@MainActivity, it)
                XBitmapSimple(null, bitmap)
            }
            collageView.setData(xBitmaps, border, border, ratio)

            button1.setOnClickListener {
                val nextIndex = (ratioList.indexOf(ratio) + 1) % ratioList.size
                val nextRatio = ratioList[nextIndex]
                if (collageView.setRatioAnimated(nextRatio)) {
                    ratio = nextRatio
                }
            }

            button2.setOnClickListener {
                collageView.collageAnimated()
            }

            button3.setOnClickListener {
                val nextIndex = (borderList.indexOf(border) + 1) % borderList.size
                val nextBorder = borderList[nextIndex]
                if (collageView.setCollagePaddingAnimated(nextBorder, nextBorder)) {
                    border = nextBorder
                }
            }

            loadingView.visibility = View.GONE

        }
    }


    private fun applyInsets(viewId: Int) {
        val layout = findViewById<View>(viewId)
        if (layout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(layout) { v: View, windowInsets: WindowInsetsCompat ->
                val insets =
                    windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars())
                val mlp = v.layoutParams as MarginLayoutParams
                mlp.leftMargin = insets.left
                mlp.topMargin = insets.top
                mlp.rightMargin = insets.right
                mlp.bottomMargin = insets.bottom
                v.layoutParams = mlp
                WindowInsetsCompat.CONSUMED
            }
        }
    }


}
