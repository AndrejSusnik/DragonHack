package com.example.android_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {

//   var sharedText: String = ""

//   override fun onCreate(savedInstanceState: Bundle?) {
//     super.onCreate(savedInstanceState)
//   }


//   override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
//     super.configureFlutterEngine(flutterEngine)

//     MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "files.dragondrop.io/files").setMethodCallHandler {
//         call, result ->
//         result.success("LMAO")
// //      Log.i("INTENT TYPE: ", intent?.type!!)
// //        if (call.method == "getSharedText") {
// //          if (Intent.ACTION_SEND == intent?.action!!) {
// //            handleSendTextIntent()
// //          }
// //          result.success(sharedText)
// //        } else {
// //          result.notImplemented()
// //        }
// //      // Note: this method is invoked on the main thread.
// //      // TODO
//     }
//   }

//   private fun handleSendTextIntent() {
//     sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)!!
//   }
}
