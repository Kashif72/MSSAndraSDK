package com.msewa.andrasdktest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.msewa.vpqlib.metadata.AppMetadata
import com.msewa.vpqlib.model.giftcard.GiftCardPaymentRequest
import com.msewa.vpqlib.model.giftcard.GiftCardPaymentResponse
import com.msewa.vpqlib.model.merchant.CheckOutInit
import com.msewa.vpqlib.model.merchant.GiftCheckOutInit
import com.msewa.vpqlib.model.merchant.Merchant
import com.msewa.vpqlib.model.merchant.PayQwikInit
import com.msewa.vpqlib.model.services.mobiletopup.mtpayment.MTPrePaymentRequest
import com.msewa.vpqlib.model.services.mobiletopup.mtpayment.MTPrePaymentResponse
import com.msewa.vpqlib.util.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), VPQPayGatewayApi, VPQStatusGatewayApi {

    //User's mobile number
    val mobileNum = "9860409020"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(initiateReceiver, IntentFilter(AppMetadata.A_INIT_PAYMENT))
        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(initiateGiftReceiver, IntentFilter(
            AppMetadata.A_INIT_PAYMENT_GIFT
        ))


        btnTest.setOnClickListener {
            val merchant = Merchant("AndraBank", mobileNum)
            val api = MerchantGateway(PayQwikInit(this@MainActivity))
            api.begin(merchant)
        }



        //*****UNCOMMENT AND USE THIS****//
//        TO CHECK THE STATUS//
        //Params : RefIdReceivedFromVPQSDK, RefIdOfAndraBank, Context
        val api = StatusGateway("REFID12345","12121212", this@MainActivity);
        api.begin();


    }


    private val initiateGiftReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val request = intent.getParcelableExtra<GiftCardPaymentRequest>(AppMetadata.CHECK_OUT_REQ)

            //Call balance API Here in background thread and broadcast the message
            Log.i("ObtBalance",request.products.get(0).price.toString())

            //ADD THIS FUNCTION AFTER YOU CHECK THE MPIN AND DEDCUT THE BALANCE AND HAVE REFERENCE NUMBER
            //Show loading dialog while calling this and listen to the callback

            //** ADD ANDRA REF HERE**//
            request.setAndhraBankRefNo("12134124214124")
            val api = CheckOutGateway(GiftCheckOutInit(request, this@MainActivity),this@MainActivity);
            api.payGift();



        }
    }


    private val initiateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val request = intent.getParcelableExtra<MTPrePaymentRequest>(AppMetadata.CHECK_OUT_REQ)

            //Call balance API Here in background thread and broadcast the message
            Log.i("ObtBalance",request.amount.toString())

            //ADD THIS FUNCTION AFTER YOU CHECK THE MPIN AND DEDCUT THE BALANCE AND HAVE REFERENCE NUMBER
            //Show loading dialog while calling this and listen to the callback


            //** ADD ANDRA REF HERE**//
            request.setAndhraBankRefNo("12134124214124")
            val api = CheckOutGateway(CheckOutInit(request, this@MainActivity),this@MainActivity);
            api.pay();



        }
    }




    //**************** STATUS CALLBACKS********************//

    override fun onTransactionSuccess(message: String?) {
        Toast.makeText(this@MainActivity,"Trans Success!! "+ message, Toast.LENGTH_SHORT).show()
    }

    override fun onTransactionError(message: String?) {
        Toast.makeText(this@MainActivity,"Trans Error!! "+ message, Toast.LENGTH_SHORT).show()
    }

    override fun onPTransactionPending(message: String?) {
        Toast.makeText(this@MainActivity,"Trans Pending!! "+ message, Toast.LENGTH_SHORT).show()
    }





    //**************** PAYMENT CALLBACKS********************//

    override fun onPaySuccess(mtPrePaymentResponse: MTPrePaymentResponse?) {
        Log.i("ReceivedAndraRef",mtPrePaymentResponse!!.andhraBankRefNo)
        Toast.makeText(this@MainActivity,"PaySuccess, RefID"+ mtPrePaymentResponse!!.referenceNo, Toast.LENGTH_SHORT).show()

    }

    override fun onPayPending(mtPrePaymentResponse: MTPrePaymentResponse?) {
        Toast.makeText(this@MainActivity,"PayPending", Toast.LENGTH_SHORT).show()

    }


    override fun onPayError() {
        Toast.makeText(this@MainActivity,"PayError", Toast.LENGTH_SHORT).show()
    }

    override fun onGiftCardPaySuccess(gPaymentResponse: GiftCardPaymentResponse?) {
        Toast.makeText(this@MainActivity,"GPaySuccess"+gPaymentResponse!!.giftCardResponse.transactionRefNo,Toast.LENGTH_SHORT).show()
    }
}
