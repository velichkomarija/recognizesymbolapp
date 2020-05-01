package com.velichkomarija.recognizesymbolapp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.velichkomarija.recognizesymbolapp.R
import com.velichkomarija.recognizesymbolapp.utils.Classifier
import com.velichkomarija.recognizesymbolapp.utils.Result
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
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
        iv_icon.setImageDrawable(getDrawable(R.drawable.ic_image))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_image) {
            showDialog()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun renderResult(result: Result) {
        tv_value_predicate.text = result.numberOfClass.toString()
        tv_probability_value.text = result.probabilityValue.toString()
        iv_icon.setImageDrawable(getDrawable(getResult(result.numberOfClass)))
        tv_timecost_value.text = String.format(
            getString(R.string.timecost_value),
            result.timeCostValue
        )
    }

    fun getResult(classValue: Int): Int {
        when (classValue) {
            0 -> {
                return R.drawable.symbol1
            }
            1 -> {
                return R.drawable.symbol2
            }
            2 -> {
                return R.drawable.symbol3
            }
            3 -> {
                return R.drawable.symbol4
            }
            4 -> {
                return R.drawable.symbol5
            }
            5 -> {
                return R.drawable.symbol6
            }
            6 -> {
                return R.drawable.symbol7
            }
            7 -> {
                return R.drawable.symbol8
            }
            8 -> {
                return R.drawable.symbol9
            }
            9 -> {
                return R.drawable.symbol10
            }
            10 -> {
                return R.drawable.symbol11
            }
            11 -> {
                return R.drawable.symbol12
            }
            else -> {
                return R.drawable.ic_image
            }
        }
    }

    @SuppressLint("InflateParams")
    fun showDialog() {
        val view: View = layoutInflater.inflate(R.layout.custom_dialog, null)

        val alertDialog: AlertDialog = AlertDialog.Builder(this)
            .create()
        alertDialog.setView(view)
        alertDialog.setTitle(R.string.imageTitle)
        alertDialog.setCancelable(true)

        view.btn_cancel.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog.show()
    }
}
