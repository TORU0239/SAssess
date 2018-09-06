package my.com.toru.sassess.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_booking_complete.*
import my.com.toru.sassess.R
import my.com.toru.sassess.util.Util

class BookingCompleteActivity : AppCompatActivity() {

    private val countdownHandler:CountdownHandler by lazy{
        CountdownHandler{
            runOnUiThread {
                if(it == 0){
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                txt_countdown.text = ("$it")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_complete)
        initUI()
        initCountdown()
    }

    private fun initCountdown(){
        val msg = Message()
        with(msg){
            arg1 = Util.COUNTDOWN_MAX
            what = Util.COUNTDOWN_MAX_ARG
        }
        countdownHandler.sendMessage(msg)
    }

    private fun initUI(){
        btn_to_main.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        countdownHandler.removeMessages(Util.COUNTDOWN_MAX_ARG)
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    class CountdownHandler(val callback:(Int)->Unit):Handler(){
        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                Util.COUNTDOWN_MAX_ARG -> {
                    callback(msg.arg1)
                    if(msg.arg1 == 0){
                        removeMessages(Util.COUNTDOWN_MAX_ARG)
                    }
                    else{
                        val newMsg = Message()
                        with(newMsg){
                            what = Util.COUNTDOWN_MAX_ARG
                            arg1 = --(msg.arg1)
                        }
                        sendMessageDelayed(newMsg, 1000)
                    }
                }
                else->{
                    removeMessages(0x10)
                }
            }
        }
    }
}