package com.inclu6ve.alerto;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    registerPlugin(BluetoothMeshPlugin.class);
    super.onCreate(savedInstanceState);
  }
}
