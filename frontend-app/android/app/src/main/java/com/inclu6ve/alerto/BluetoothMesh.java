package com.inclu6ve.alerto;

import java.nio.charset.StandardCharsets;
import java.util.*;

import android.Manifest;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresPermission;

public class BluetoothMesh {


  private static class InputBuffer {
    final BluetoothDevice device;
    final UUID charUUID;
    public final List<byte[]> bytes;

    InputBuffer(BluetoothDevice device, UUID charUUID, byte[] value) {
      this.device = device;
      this.charUUID = charUUID;
      this.bytes = new ArrayList<>();
      this.bytes.add(value);
    }
  }

  private static final String TAG = "AlertoBLE";
  private static final UUID SERVICE_UUID = UUID.fromString("84d3472a-2098-47e3-8475-699fdde69070");
  private static final UUID CHAR_INDEX_UUID = UUID.fromString("84d3472a-2098-47e3-8475-699fdde69070"); // read
  private static final UUID CHAR_ALERT_UUID = UUID.fromString("84d3472a-2098-47e3-8475-699fdde69070"); // write

  private static final ParcelUuid ADVERTISE_PARCEL_UUID = new ParcelUuid(SERVICE_UUID);

  private BluetoothManager bluetoothManager;
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothLeAdvertiser advertiser;
  private BluetoothGattServer gattServer;
  private BluetoothLeScanner scanner;

  public interface OnAlertReceivedListener {
    void onAlertReceived(String id, String payload);
  }

  private OnAlertReceivedListener alertListener;

  private List<InputBuffer> inputBuffers = new LinkedList<>();

  private  void handleInputFragment(BluetoothDevice device, UUID charUuid, byte[] value) {
    Log.d(TAG, "handling input fragment");
    for (InputBuffer buff : inputBuffers) {
      if (buff.device.equals(device)) {
        buff.bytes.add(value);
        return;
      }
    }
    inputBuffers.add(new InputBuffer(device, charUuid, value));
  }

  // Mock db, TODO: update
  private final Map<String, String> localAlerts = new HashMap<>();

  // Active GATT connections
  private final Map<String, BluetoothGatt> activeGatts = new HashMap<>();

  private Context context;

  public void init(Context ctx, BluetoothManager bleManager) {
    context = ctx;
    bluetoothManager = bleManager;
    bluetoothAdapter = bluetoothManager.getAdapter();
    if (bluetoothAdapter == null) {
      // BLE not supported
      return;
    }

    // dummy data :)
    localAlerts.put("alert1", "Danger");
//    localAlerts.put("alert3", "BAD Danger!!!!");
  }

  @RequiresPermission(allOf = { Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE,
      Manifest.permission.BLUETOOTH_SCAN })
  public void start() {
    startGattServer();
    startAdvertising();
    startScanning();
  }

  @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT})
  public void stop() {
    if (advertiser != null) advertiser.stopAdvertising(advertiseCallback);
    if (scanner != null) scanner.stopScan(scanCallback);
    if (gattServer != null) gattServer.close();

    for (BluetoothGatt g : activeGatts.values()) {
      if (g != null) g.close();
    }
  }

  public void addAlertListener(OnAlertReceivedListener listener) {
    this.alertListener = listener;
  }

  @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
  private void startGattServer() {
    gattServer = bluetoothManager.openGattServer(context, gattServerCallback);
    if (gattServer == null) {
      // failed
      return;
    }

    BluetoothGattService service = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
    BluetoothGattCharacteristic indexChar = new BluetoothGattCharacteristic(
        CHAR_INDEX_UUID,
        // readable
        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
        BluetoothGattCharacteristic.PERMISSION_READ);

    BluetoothGattCharacteristic alertChar = new BluetoothGattCharacteristic(
        CHAR_ALERT_UUID,
        // writable
        BluetoothGattCharacteristic.PROPERTY_WRITE,
        BluetoothGattCharacteristic.PERMISSION_WRITE);

    service.addCharacteristic(indexChar);
    service.addCharacteristic(alertChar);
    gattServer.addService(service);
    Log.i(TAG, "GATT server service added.");
  }

  private final BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
      Log.i(TAG, "GATT Server connection change. Device=" + device.getAddress() + " state=" + newState);
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
      super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
      Log.i(TAG, "oncharwreq: " + value.length + " bytes");
      if (preparedWrite) {
        handleInputFragment(device, characteristic.getUuid(), value);
      } else {
        handleMessageInput(device, characteristic.getUuid(), value);
      }

      if (responseNeeded) {
        Log.i(TAG, "sending response");
        if (!gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, new byte[0])) {
          Log.e(TAG, "sending resopnse failed");
        }
      }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
        BluetoothGattCharacteristic characteristic) {
      if (CHAR_INDEX_UUID.equals(characteristic.getUuid())) {
        String index = String.join(",", localAlerts.keySet());
        byte[] payload = index.getBytes(StandardCharsets.UTF_8);
        gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, payload);
        Log.i(TAG, "Served index to " + device.getAddress() + ": " + index);
      } else {
        gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
      }
    }

    void handleMessageInput(BluetoothDevice device, UUID uuid, byte[] value) {
      String s = new String(value, StandardCharsets.UTF_8);
      String[] parts = s.split("\\|", 2);
      if (parts.length == 2) {
        String id = parts[0];
        String payload = parts[1];
        localAlerts.put(id, payload);

        Log.i(TAG, "ALERT RECEIVED sending to listener:" + (alertListener != null ? "true" : "false"));
        if (alertListener != null) {
          alertListener.onAlertReceived(id, payload);
        }
      }
    }

//    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
//    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
//        BluetoothGattCharacteristic characteristic, boolean preparedToWrite, boolean responseNeeded, int offset,
//        byte[] value) {
//
//      Log.i(TAG, "ON CHARAFTR WRITE REQUEST " + characteristic.getUuid());
//      if (CHAR_ALERT_UUID.equals(characteristic.getUuid())) {
//        String s = new String(value, StandardCharsets.UTF_8);
//        String[] parts = s.split("\\|", 2);
//        if (parts.length == 2) {
//          String id = parts[0];
//          String payload = parts[1];
//          localAlerts.put(id, payload);
//
//          Log.i(TAG, "ALERT RECEIVED sending to listener:" + (alertListener != null ? "true" : "false"));
//          if (alertListener != null) {
//            alertListener.onAlertReceived(id, payload);
//          }
//
//          if (responseNeeded) {
//            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
//            return;
//          }
//        }
//      }
//      if (responseNeeded) {
//        gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
//      }
//    }
  };

  @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
  private void startAdvertising() {
    if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
      Log.e(TAG, "Device does not support BLE advertising");
      return;
    }

    advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    if (advertiser == null) {
      Log.e(TAG, "Cannot get advertiser");
      return;
    }

    AdvertiseSettings settings = new AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setConnectable(true)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .build();

    AdvertiseData data = new AdvertiseData.Builder()
        .addServiceUuid(ADVERTISE_PARCEL_UUID)
        .build();
    advertiser.startAdvertising(settings, data, advertiseCallback);
    Log.i(TAG, "Started advertising");
  }

  private final AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
    @Override
    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
      Log.i(TAG, "Advertising onStartSuccess");
    }

    @Override
    public void onStartFailure(int errorCode) {
      Log.e(TAG, "Advertising failed:" + errorCode);
    }
  };

  @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
  private void startScanning() {
    scanner = bluetoothAdapter.getBluetoothLeScanner();
    if (scanner == null) {
      Log.e(TAG, "Scanner not available");
      return;
    }

    ScanFilter filter = new ScanFilter.Builder()
        .setServiceUuid(ADVERTISE_PARCEL_UUID)
        .build();

    ScanSettings settings = new ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build();
    scanner.startScan(Collections.singletonList(filter), settings, scanCallback);
    Log.i(TAG, "Started scanning.");
  }

  private final ScanCallback scanCallback = new ScanCallback() {
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
      BluetoothDevice device = result.getDevice();
      String key = device.getAddress();
      if (!activeGatts.containsKey(key)) {
        Log.i(TAG, "Found potential peer: " + key + " name=" + device.getName());
        connectToDevice(device);
      }
    }
  };

  @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
  private void connectToDevice(BluetoothDevice device) {
    BluetoothGatt gatt = device.connectGatt(context, false, clientGattCallback);
    activeGatts.put(device.getAddress(), gatt);
    Log.i(TAG, "Connected to: " + device.getAddress());

    if (alertListener != null) {
      alertListener.onAlertReceived("abc123", "Danger, danger! High voltage!");
    }
  }

  private final BluetoothGattCallback clientGattCallback = new BluetoothGattCallback() {
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      String remoteAddr = gatt.getDevice().getAddress();
      Log.i(TAG, "Client connection state changed: " + remoteAddr + " state: " + newState);
      if (newState == BluetoothProfile.STATE_CONNECTED) {
        Log.i(TAG, "Discovering services on " + remoteAddr);
        gatt.discoverServices();
      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        activeGatts.remove(remoteAddr);
        gatt.close();
      }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      Log.i(TAG, "Services discovered, reading index...");
      BluetoothGattService service = gatt.getService(SERVICE_UUID);
      if (service == null) {
        Log.e(TAG, "Remote service not found");
        return;
      }
      BluetoothGattCharacteristic indexChar = service.getCharacteristic(CHAR_INDEX_UUID);
      if (indexChar != null) {
        boolean ok = gatt.readCharacteristic(indexChar);
        Log.i(TAG, "Requested index read: " + ok);
      }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      if (CHAR_INDEX_UUID.equals(characteristic.getUuid())) {
        byte[] val = characteristic.getValue();
        String index = val == null ? "" : new String(val, StandardCharsets.UTF_8);
        Log.i(TAG, "Received remote index: " + index);
        handleRemoteIndex(gatt, index);
      }
    }

    // after computing what's missing, write alerts to remote server
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void handleRemoteIndex(BluetoothGatt gatt, String indexCsv) {
      Set<String> remoteIds = new HashSet<>();
      if (!indexCsv.trim().isEmpty()) {
        String[] parts = indexCsv.split(",");
        for (String p : parts)
          remoteIds.add(p.trim());
      }

      // compute missing alerts that we have and remote doesn't
      List<String> toSend = new ArrayList<>();
      for (Map.Entry<String, String> e : localAlerts.entrySet()) {
        if (!remoteIds.contains(e.getKey())) {
          toSend.add(e.getKey());
        }
      }
      if (toSend.isEmpty()) {
        Log.i(TAG, "Nothing to sync to remote.");
        gatt.disconnect();
        return;
      }

      BluetoothGattService service = gatt.getService(SERVICE_UUID);
      if (service == null) {
        Log.e(TAG, "Service disappeared.");
        return;
      }
      BluetoothGattCharacteristic alertChar = service.getCharacteristic(CHAR_ALERT_UUID);
      if (alertChar == null) {
        Log.e(TAG, "Alert characteristic missing.");
        return;
      }

      // send alerts one by one (simple). In production handle write queue and
      // responses.
      for (String id : toSend) {
        String payload = id + "|" + localAlerts.get(id);
        alertChar.setValue(payload.getBytes(StandardCharsets.UTF_8));
        boolean ok = gatt.writeCharacteristic(alertChar);
        Log.i(TAG, "Writing alert " + id + ", payload " + payload + " -> " + ok);
        // NOTE: synchronous waiting for onCharacteristicWrite is better; here we just
        // fire writes.
      }
      // after writes, disconnect
//      gatt.disconnect();
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      if (CHAR_ALERT_UUID.equals(characteristic.getUuid())) {
        Log.i(TAG, "Client got write response (alert) status=" + status);
      }
    }
  };
};
