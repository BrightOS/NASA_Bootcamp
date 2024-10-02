//package ru.berserkers.deepspace.utils
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Context
//import android.content.DialogInterface
//import com.google.firebase.messaging.RemoteMessage
//import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener
//
//class AzureNotificationListener(val activity: Activity) : NotificationListener {
//    override fun onPushNotificationReceived(p0: Context?, p1: RemoteMessage?) {
//        val notification: RemoteMessage.Notification = p1?.notification!!
//        val title: String? = notification.title
//        val body: String? = notification.body
//        activity.runOnUiThread {
//            val builder: AlertDialog.Builder = activity.let {
//                AlertDialog.Builder(it)
//            }
//            builder.setMessage(body)!!.setTitle(title)
//                .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ ->
//                    dialog.dismiss()
//                })
//            val dialog: AlertDialog = builder.create()
//            dialog.show()
//        }
//    }
//}