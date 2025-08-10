import { registerPlugin } from "@capacitor/core";

export interface AlertPayload {
  issued_by: string;
  emergency_level: "Low" | "High" | "Extreme";
  emergency_type: string;
  affected_area: string[];
  response_measures: string[];
  keywords: string[];
  note: string;
}

export interface Alert {
  id: string;
  data: AlertPayload;
}

export interface BluetoothMeshPlugin {
  init(): Promise<void>;
  start(): Promise<void>;
  stop(): Promise<void>;

  addListener(event: "alert", callback: (alert: Alert) => void): Promise<void>;
}

const BluetoothMesh = registerPlugin<BluetoothMeshPlugin>("BluetoothMesh");

export default BluetoothMesh;
