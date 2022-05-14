package com.example.android_app

import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity


class MainActivity: FlutterActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val getIntent : Intent = getIntent();
  } 
}
