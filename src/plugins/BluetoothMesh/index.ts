import { registerPlugin } from "@capacitor/core";


type JsonSerialized<T> = string & {
  __json_serialized: T;
}

declare global {
  interface JSON {
    parse<T>(text: JsonSerialized<T>): T;
  }
}

export interface BluetoothMeshPlugin {
  init(): Promise<void>;
}

const BluetoothMesh = registerPlugin<BluetoothMeshPlugin>("BluetoothMesh");

export default BluetoothMesh;
