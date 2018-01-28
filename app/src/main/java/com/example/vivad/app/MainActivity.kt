package com.example.vivad.app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.example.vivad.app.barcode.BarcodeCaptureActivity
import com.example.vivad.app.business.UserBusiness
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //private lateinit var result_textview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonScanShape = GradientDrawable()
        buttonScanShape.shape = GradientDrawable.RECTANGLE
                buttonScanShape.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccessBlue,null))
        buttonScanShape.cornerRadius = 20F

        scan_barcode_button.setBackground(buttonScanShape)

        //result_textview = findViewById(R.id.result_textview)

        scan_barcode_button.setOnClickListener {
            val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE)
        }
         button_logout.setOnClickListener {

             val userBusiness = UserBusiness(this)
             userBusiness.removeUser()

             val intent = Intent(applicationContext, LoginActivity::class.java)
             startActivity(intent)
             finish()


         }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    //val p = barcode.cornerPoints
                    result_textview.text = barcode.displayValue
                } else
                    result_textview.setText(R.string.no_barcode_captured)
            } else
                Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)))
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private val BARCODE_READER_REQUEST_CODE = 1
    }
}
