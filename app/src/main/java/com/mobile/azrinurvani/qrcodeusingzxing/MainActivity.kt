package com.mobile.azrinurvani.qrcodeusingzxing

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mobile.azrinurvani.qrcodeusingzxing.model.QRGeoModel
import com.mobile.azrinurvani.qrcodeusingzxing.model.QRUrlModel
import com.mobile.azrinurvani.qrcodeusingzxing.model.QRVCardModel
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    zxscan.setResultHandler(this@MainActivity)
                    zxscan.startCamera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(applicationContext,"You should enable this permission ",Toast.LENGTH_LONG).show()
                }

            }).check()
    }

    override fun handleResult(rawResult: Result?) {
//        txt_result.text = rawResult?.text -->use this if just handle text value from QR Code
        processRawResult(rawResult?.text)
    }

    private fun processRawResult(text: String?) {
        if (text?.startsWith("BEGIN:")!!){
            val tokens = text?.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val qrvCardModel = QRVCardModel()
            for (i in tokens.indices){
                if (tokens[i].startsWith("BEGIN:"))
                    qrvCardModel.type = tokens[i].substring("BEGIN:".length) //remove BEGIN: from String to get type
                else if (tokens[i].startsWith("N:"))
                    qrvCardModel.name = tokens[i].substring("N:".length) //remove N: from String to get name
                else if (tokens[i].startsWith("ORG:"))
                    qrvCardModel.org = tokens[i].substring("ORG:".length) //remove ORG: from String to get org
                else if (tokens[i].startsWith("TEL:"))
                    qrvCardModel.url = tokens[i].substring("TEL:".length) //remove TE:: from String to get url
                else if (tokens[i].startsWith("EMAIL:"))
                    qrvCardModel.email = tokens[i].substring("EMAIL:".length) //remove EMAIL: from String to get email
                else if (tokens[i].startsWith("ADR:"))
                    qrvCardModel.address = tokens[i].substring("ADR:".length) //remove ADR: from String to get address
                //VEVENT
                else if (tokens[i].startsWith("NOTE:"))
                    qrvCardModel.note = tokens[i].substring("NOTE:".length) //remove NOTE: from String to get note
                else if (tokens[i].startsWith("SUMMARY:"))
                    qrvCardModel.summary = tokens[i].substring("SUMMARY:".length) //remove SUMMARY: from String to get summary
                else if (tokens[i].startsWith("DTSTART:"))
                    qrvCardModel.dtstart = tokens[i].substring("DTSTART:".length) //remove DTSTART: from String to get dtstart
                else if (tokens[i].startsWith("DTEND:"))
                    qrvCardModel.dtend = tokens[i].substring("DTEND:".length) //remove DTEND: from String to get summary

                //show type if VCARD/VEVENT
                txt_result?.text = qrvCardModel.type
            }
        }else if (text?.startsWith("http://")||text?.startsWith("https://")||text?.startsWith("www.")){
            val qrUrlModel = QRUrlModel()
            qrUrlModel.url = text!!
            txt_result.text = qrUrlModel.url
        }else if(text?.startsWith("geo:")){
            val qrGeoModel = QRGeoModel()
            val delims = "[ , ?q= ]+"
            val tokens = text?.split(delims.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            for (i in tokens.indices){
                if (tokens[i].startsWith(" geo:"))
                    qrGeoModel.lat = tokens[i].substring("geo:".length)
            }

            qrGeoModel.lat = tokens[0].substring("geo: ".length)
            qrGeoModel.lng = tokens[1]
            qrGeoModel.get_place = tokens[2]

            txt_result.text = qrGeoModel.lat + " / "+qrGeoModel.lng
        }else{
            txt_result.text = text!!
        }
    }
}
