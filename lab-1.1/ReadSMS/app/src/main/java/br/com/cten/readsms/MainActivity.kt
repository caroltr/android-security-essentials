package br.com.cten.readsms

import android.Manifest
import android.app.ListActivity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView

class MainActivity : ListActivity() {

    private val SMS = Uri.parse("content://sms")
    private val PERMISSIONS_REQUEST_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            readSMS()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), PERMISSIONS_REQUEST_READ_SMS)
        }
    }

    // Singleton class
    object SmsColumns {
        val ID = "_id"
        val ADDRESS = "address"
        val DATE = "date"
        val BODY = "body"
    }

    private inner class SmsCursosAdapter(context: Context,
                                         c: Cursor, autoRequery: Boolean): CursorAdapter(context, c, autoRequery) {

        override fun newView(context: Context, cursor: Cursor, viewGroup: ViewGroup): View {
            return View.inflate(context, R.layout.activity_main, null)
        }

        override fun bindView(view: View, context: Context, cursor: Cursor) {

            val smsOrigin = view.findViewById<TextView>(R.id.sms_origin)
            val smsBody = view.findViewById<TextView>(R.id.sms_body)
            val smsDate = view.findViewById<TextView>(R.id.sms_date)

            smsOrigin.text = cursor.getString(cursor.getColumnIndexOrThrow(SmsColumns.ADDRESS))
            smsBody.text = cursor.getString(cursor.getColumnIndexOrThrow(SmsColumns.BODY))
            smsDate.text = cursor.getString(cursor.getColumnIndexOrThrow(SmsColumns.DATE))
        }
    }

    private fun readSMS() {
        val cursor = contentResolver.query(SMS, arrayOf(SmsColumns.ID,
                SmsColumns.ADDRESS, SmsColumns.DATE, SmsColumns.BODY), null, null,
                SmsColumns.DATE + " DESC")

        val adapter = SmsCursosAdapter(this, cursor, true)
        listAdapter = adapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_READ_SMS -> {
                // If the request is cancelled, the result arrays are empty
                if (grantResults?.isNotEmpty()!! && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    readSMS()
                } else {
                    // Permission denied
                }

                return
            }
        }
    }
}
