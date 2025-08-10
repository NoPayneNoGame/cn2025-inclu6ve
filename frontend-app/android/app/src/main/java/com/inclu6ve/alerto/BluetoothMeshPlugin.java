package com.inclu6ve.alerto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;
import java.util.List;

@CapacitorPlugin(name = "BluetoothMesh")
public class BluetoothMeshPlugin extends Plugin {

  BluetoothMesh bleMesh = new BluetoothMesh();
  Context context;
  private static final String TAG = "AlertoBLEPlugin";

  @SuppressLint("MissingPermission")
  @Override
  protected void handleOnDestroy() {
    bleMesh.stop();
    super.handleOnDestroy();
  }

  @PluginMethod()
  public void init(PluginCall call) {
    context = this.getContext();
    Log.i(TAG, "Plugin init");
    bleMesh.init(context, (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE));
    Log.i(TAG, "Plugin init complete");
    call.resolve();
  }


  private void onMessageAlertReceived(String id, String payload) {
    JSObject ret = new JSObject();
    ret.put("id", id);
    notifyListeners("alert", ret);
  }

  @PluginMethod()
  public void start(PluginCall call) {
    this.checkPermissionsAndEnable();

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
      call.reject("Missing permission somewhere");
      return;
    }
    bleMesh.start();
    bleMesh.addAlertListener(this::onMessageAlertReceived);
    call.resolve();
  }

  @PluginMethod()
  public void stop(PluginCall call) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
      call.reject("Missing permission somewhere");
      return;
    }
    bleMesh.stop();
    call.resolve();
  }


  private void checkPermissionsAndEnable() {
    Log.i(TAG, "Checking permissions");
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

    Log.i(TAG, "Perms checked: " + req);
    if (!req.isEmpty()) {
      ActivityCompat.requestPermissions(this.getActivity(), req.toArray(new String[0]), 9);
      return;
    }
  }
}
