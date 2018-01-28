package com.club.gm7.app

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.club.gm7.app.barcode.BarcodeCaptureActivity
import com.club.gm7.app.business.UserBusiness
import com.github.kittinunf.fuel.httpPost
import com.club.gm7.app.R
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*

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
             finish()
             startActivity(intent)


         }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    //val p = barcode.cornerPoints
                    validate(barcode.displayValue)
                } else
                    toast(R.string.no_barcode_captured)
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

    private fun validate(qr_code: String) {

        val reg = Regex("((?<=_\\\$Club\\\$_)|(?=_\\\$Club\\\$_))")

        var components = qr_code.split(reg)

        if (components.count() != 3 ) {
            toast("Código inválido")
            return
        }


        val list = listOf("email" to components[2], "code" to components[0])
        val loading = indeterminateProgressDialog("This a progress dialog")
        loading.show()
        "http://painelgm7club.com.br/place/validate".httpPost(list).responseObject(ResponseData.Deserializer()) { request, response, result ->
            val (data, err) = result
            loading.hide()
            if (data != null) {
                alert(data.message, "Validado") {
                    positiveButton("Ok") {}
                    customView {
                        verticalLayout {
                            gravity = Gravity.CENTER
                            imageView(R.drawable.done_tick).lparams(300)
                        }
                    }

                }.show()
            } else {
                if (response.data.isEmpty()) {
                    alert("Erro. Verifique sua conexão."){
                        positiveButton("Ok") {}
                    }.show()
                } else {
                    val json = JsonParser().parse(String(response.data)).asJsonObject
                    if (json.has("message")) {
                        alert(json["message"].asString){
                            positiveButton("Ok") {}
                        }.show()
                    } else {
                        alert("Erro."){
                            positiveButton("Ok") {}
                        }.show()
                    }

                }
            }
        }
    }
}
