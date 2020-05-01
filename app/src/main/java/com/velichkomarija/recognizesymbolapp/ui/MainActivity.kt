package com.velichkomarija.recognizesymbolapp.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.velichkomarija.recognizesymbolapp.R
import com.velichkomarija.recognizesymbolapp.utils.Classifier
import com.velichkomarija.recognizesymbolapp.utils.Result
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val LOG_TAG: String = "MainActivity"

    private var classifier: Classifier? = null

    private val onDetectClick: View.OnClickListener = View.OnClickListener {
        when {
            classifier == null -> {
                Log.e(
                    LOG_TAG,
                    "onDetectClick(): Classifier is not initialized"
                )
            }
            finger_paint.isEmpty -> {
                Toast.makeText(
                    this,
                    R.string.please_write_a_digit,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            else -> {
                val image: Bitmap = finger_paint.exportToBitmap(
                    Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT
                )
                val result: Result = classifier!!.classify(image)
                renderResult(result)
            }
        }
    }

    private val onCancelClick: View.OnClickListener = View.OnClickListener {
        finger_paint.clear()
        tv_value_predicate.setText(R.string.emptyResult)
        tv_probability_value.setText(R.string.emptyResult)
        tv_timecost_value.setText(R.string.emptyResult)
    }

    override fun onStart() {
        super.onStart()
        try {
            this.classifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(
                this,
                R.string.failed_to_create_classifier,
                Toast.LENGTH_LONG
            )
                .show()

            Log.e(LOG_TAG, "init(): Failed to create Classifier", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_detect.setOnClickListener(onDetectClick)
        btn_clear.setOnClickListener(onCancelClick)
    }

    private fun renderResult(result: Result) {
        tv_value_predicate.text = result.numberOfClass.toString()
        tv_probability_value.text = result.probabilityValue.toString()
        tv_timecost_value.text = String.format(
            getString(R.string.timecost_value),
            result.timeCostValue
        )
    }
}
