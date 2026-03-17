import { ref } from "vue";

import type { Alert } from "@/plugins/BluetoothMesh";
import BluetoothMesh from "@/plugins/BluetoothMesh";

const bleInit = ref(false);
const bleStarted = ref(false);

const alertsIssued = ref(0);
const alerts = new Set<string>();

const hardCodedAlerts = [
  {
    issued_by: "Government",
    emergency_level: "Extreme" as const,
    emergency_type: "Bush Fire",
    affected_area: ["The Bush"],
    response_measures: ["Evacuate Immediately"],
    keywords: ["fire", "evacuation"],
    note: "Bush fire moving rapidly.",
    timestamp: new Date().toISOString(),
  },
  {
    issued_by: "Concerned",
    emergency_level: "Extreme" as const,
    emergency_type: "Natural Hazard",
    affected_area: ["Coastal Region"],
    response_measures: ["Evacuate to higher ground", "Secure loose objects"],
    keywords: ["tsunami", "evacuation"],
    note: "Tsunami warning issued following earthquake.",
    timestamp: new Date().toISOString(),
  },
];


export async function useBluetoothAlerts(startAutomatically = false) {
  if (!bleInit.value) {
    await BluetoothMesh.init();
    bleInit.value = true;
  }

  if (startAutomatically && !bleStarted.value) {
    await BluetoothMesh.start();
    bleStarted.value = true;
  }

  async function start() {
    if (!bleStarted.value) {
      await BluetoothMesh.start();
    }
  }

  async function onAlert(cb: (alert: Alert) => void) {
    if (!bleStarted.value) throw new Error("Bluetooth not started");
    await BluetoothMesh.addListener("alert", (alert) => {
      if (alerts.has(alert.id)) return;
      alerts.add(alert.id);

      if (alertsIssued.value > hardCodedAlerts.length) {
        return;
      }

      const data = hardCodedAlerts[alertsIssued.value];
      alertsIssued.value += 1;
      cb({ id: alert.id, data });
    });
  }

  return {
    started: bleStarted,
    start,
    onAlert,
  };
}