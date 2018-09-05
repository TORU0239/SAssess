package my.com.toru.sassess.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_booking_info.*
import my.com.toru.sassess.R
import java.text.SimpleDateFormat
import java.util.*

class BookingInfoDialogFragment :DialogFragment() {
    companion object {
        fun newInstance(argument: Bundle):BookingInfoDialogFragment{
            val dialog = BookingInfoDialogFragment()
            dialog.arguments = argument
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_booking_info, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val startTS = getLong("START_TS")
            val startDate = sdf.format(Date(startTS))

            val endTS = getLong("END_TS")
            val endDate = sdf.format(Date(endTS))

            Log.i("BookingInfoDialog", "date:: $startTS, $endTS")
            Log.i("BookingInfoDialog", "date:: $startDate, $endDate")

            booking_start_date.text = startDate
            booking_end_date.text = endDate

        }
    }
}