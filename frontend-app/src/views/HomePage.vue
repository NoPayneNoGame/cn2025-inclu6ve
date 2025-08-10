<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Alerts</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content v-if="state.loading">
      <div class="loading-center">
        <ion-spinner color="primary"></ion-spinner>
      </div>
    </ion-content>

    <ion-content :fullscreen="true" v-else>
      <ion-refresher slot="fixed" @ionRefresh="doRefresh">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <ion-list v-if="state.alerts.length > 0" class="alert-list">
        <ion-item
          v-for="alert in state.alerts"
          :key="alert._id || alert.timestamp"
          class="alert-item"
          lines="none"
        >
          <ion-thumbnail slot="start" class="alert-thumb">
            <img :src="getAlertIcon(alert)" alt="alert icon" />
          </ion-thumbnail>

          <ion-label class="alert-label">
            <h2 class="alert-title">
              {{ compactTitle(alert) }}
            </h2>
          </ion-label>

          <ion-note slot="end" class="alert-time">
            {{ formatTime(alert.timestamp) }}
          </ion-note>
        </ion-item>
      </ion-list>

      <div v-else class="no-alerts">
        No alerts in the last 24 hours.
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonRefresher,
  IonRefresherContent,
  IonList,
  IonItem,
  IonThumbnail,
  IonLabel,
  IonNote,
  IonSpinner,
} from "@ionic/vue";
import axios from "axios";
import { reactive } from "vue";
import { getAlertIcon } from "@/mappings/imageMaps";

// AlertDoc type aligned with backend/src/types.ts
interface AlertDoc {
  _id?: string;
  _rev?: string;
  is_official: boolean;
  issued_by: string;
  timestamp: string;
  emergency_level?: string;
  emergency_type: string;
  affected_area: string[];
  response_measures?: string[];
  keywords?: string[];
  note?: string;
}

const state = reactive({
  alerts: [] as AlertDoc[],
  loading: false,
});

const fetchLast24HoursAlerts = async (dispLoaderPage: boolean) => {
  if (dispLoaderPage) state.loading = true;

  try {
    const result = await axios.get("http://localhost:3000/alerts/recent");
    state.alerts = Array.isArray(result.data) ? result.data : [];
  } catch {
    state.alerts = [];
  }

  state.loading = false;
};

const doRefresh = (event: CustomEvent) => {
  fetchLast24HoursAlerts(false);
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  event.target?.complete();
};

function compactTitle(alert: AlertDoc) {
  // Minimal text: "<Level> - <Type>", capitalized
  const lvl = alert.emergency_level ? capitalize(alert.emergency_level) : "";
  const typ = capitalize(alert.emergency_type);
  return lvl ? `${lvl} - ${typ}` : typ;
}

function formatTime(ts: string) {
  const d = new Date(ts);
  return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

function capitalize(s?: string) {
  if (!s) return "";
  return s.charAt(0).toUpperCase() + s.slice(1);
}

fetchLast24HoursAlerts(true);
</script>

<style>
.loading-center {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 90vh;
}
ion-spinner {
  transform: scale(1.5);
}

/* Minimal, image-oriented list design */
.alert-list {
  padding: 8px 12px;
}

.alert-item {
  margin: 10px 0;
  border-radius: 12px;
  --background: var(--ion-card-background, #1f1f1f);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.25);
  padding: 6px 8px;
}

.alert-thumb {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.06);
}

.alert-thumb img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.alert-label {
  margin-left: 6px;
}

.alert-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}

.alert-time {
  font-size: 14px;
  opacity: 0.7;
}

.no-alerts {
  text-align: center;
  margin-top: 2rem;
  color: #888;
}
</style>