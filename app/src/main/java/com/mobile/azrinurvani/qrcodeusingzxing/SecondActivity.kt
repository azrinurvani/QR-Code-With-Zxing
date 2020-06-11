package com.mobile.azrinurvani.qrcodeusingzxing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_second.*

//TODO 8
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        imgScanIccid.setOnClickListener {
            startActivityForResult(Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("index",1)
            },1)
        }

        imgScanTcuIccid.setOnClickListener {
            startActivityForResult(Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("index",2)
            },2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.let{
            Log.d("ADX","Main Value : "+it.getStringExtra("value")+ " Index "+it.getIntExtra("index",0))

            when (requestCode){
                1 -> {
                    edtScanIccid.setText(it.getStringExtra("value").toString().trim())
                }
                2 -> {
                    edtScanTcuIccid.setText(it.getStringExtra("value").toString().trim())
                }
            }
        }


    }



}
