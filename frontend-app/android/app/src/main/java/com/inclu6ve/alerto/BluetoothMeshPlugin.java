package com.inclu6ve.alerto;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;
import java.util.List;

@CapacitorPlugin(name = "BluetoothMesh")
public class BluetoothMeshPlugin extends Plugin {

  BluetoothMesh bleMesh;
  Context context;

  @PluginMethod()
  public void init(PluginCall call) {
    context = this.getContext();
    bleMesh.init(context, (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE));
    call.resolve();
  }

  @PluginMethod()
  public void start(PluginCall call) {
    this.checkPermissionsAndEnable();
    bleMesh.start();
  }

  private void checkPermissionsAndEnable() {
    List<String> req = new ArrayList<>();
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      req.add(Manifest.permission.ACCESS_FINE_LOCATION);
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
      req.add(Manifest.permission.BLUETOOTH_SCAN);
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
      req.add(Manifest.permission.BLUETOOTH_ADVERTISE);
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
      req.add(Manifest.permission.BLUETOOTH_CONNECT);
    }

    if (!req.isEmpty()) {
      ActivityCompat.requestPermissions(this.getActivity(), req.toArray(new String[0]), 9);
      return;
    }
  }
}
