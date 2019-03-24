package com.msewa.andrasdktest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.msewa.vpqlib.metadata.AppMetadata
import com.msewa.vpqlib.model.merchant.Merchant
import com.msewa.vpqlib.model.merchant.PayQwikInit
import com.msewa.vpqlib.util.MerchantGateway
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //User's mobile number
    val mobileNum = "9860409020"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Local broadcast used for checking balance
        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(initiateReceiver, IntentFilter(AppMetadata.A_INIT_PAYMENT))

        btnTest.setOnClickListener {
            val merchant = Merchant("AndraBank", mobileNum)
            val api = MerchantGateway(PayQwikInit(this@MainActivity))
            api.begin(merchant)
        }
    }


    private val initiateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Requested balance in int

            val balance = intent.getIntExtra(AppMetadata.A_BALANCE,0);


            // Now Call balance API Here in background thread and broadcast the message
            Log.i("ObtBalance",balance.toString())


            //IF BALANCE IS AVAILABLE SEND TRUE ELSE FALSE
            val intent = Intent(AppMetadata.A_PROCEED_PAYMENT)
            intent.putExtra(AppMetadata.A_PROCEED,true);
            LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(intent)

        }
    }
}
