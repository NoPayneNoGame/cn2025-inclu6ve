<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>Recent Alerts</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-refresher slot="fixed" @ionRefresh="doRefresh">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <div v-if="state.loading" class="loading-center">
        <ion-spinner color="primary"></ion-spinner>
      </div>

      <div v-else-if="state.alerts.length > 0" class="alert-list-container">
        <div v-for="alert in state.alerts" :key="alert._id" class="alert-card">
          <!-- Icons row (all keyword icons, same size, same row, centered) -->
          <div class="card-icons-row">
            <img
              v-for="(icon, idx) in getKeywordIcons(alert)"
              :key="idx"
              :src="icon"
              alt="Alert icon"
              class="icon-xl"
            />
          </div>

          <!-- Text block (centered under icons) -->
          <div class="card-meta">
            <div class="card-title">
              {{ compactTitle(alert) }}
            </div>
            <div class="card-time">
              {{ formatTime(alert.timestamp) }}
            </div>
          </div>
        </div>
      </div>

      <div v-else class="no-alerts">
        <p>No alerts in the last 24 hours.</p>
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
  IonSpinner,
  type RefresherCustomEvent,
} from "@ionic/vue";
import axios from "axios";
import { onMounted, reactive } from "vue";
import { getKeywordIcons } from "@/mappings/imageMaps";
import type { AlertDoc } from "@/types/alerts";

const state = reactive({
  alerts: [] as AlertDoc[],
  loading: false,
});

const fetchLast24HoursAlerts = async (showLoader = false) => {
  if (showLoader) state.loading = true;
  try {
    const result = await axios.get("http://localhost:3000/alerts/recent");
    state.alerts = Array.isArray(result.data) ? result.data : [];
  } catch (error) {
    console.error("Failed to fetch alerts:", error);
    state.alerts = [];
  } finally {
    if (showLoader) state.loading = false;
  }
};

const doRefresh = async (event: RefresherCustomEvent) => {
  await fetchLast24HoursAlerts();
  event.target.complete();
};

const compactTitle = (alert: AlertDoc) => {
  const level = alert.emergency_level ? capitalize(alert.emergency_level) : "";
  const type = capitalize(alert.emergency_type);
  return level ? `${level} - ${type}` : type;
};

const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
  });
};

const capitalize = (s: string) => {
  if (!s) return "";
  return s.replace(/\b\w/g, (char) => char.toUpperCase());
};

onMounted(() => {
  fetchLast24HoursAlerts(true);
});
</script>

<style scoped>
ion-content {
  --background: #f4f5f8;
}

.alert-list-container {
  padding: 16px;
  display: grid;
  gap: 12px;
}

/* White rectangle with subtle shadow */
.alert-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.card-icons-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  flex-wrap: nowrap;
  overflow-x: auto;
  padding-bottom: 8px;
  width: 100%;
}

.icon-xl {
  width: 120px;
  height: 120px;
  flex: 0 0 120px; 
  object-fit: contain;
  filter: none;
  border-radius: 8px;
}

.card-meta {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  width: 100%;
  margin-top: 4px;
}

.card-title {
  font-size: 1rem;
  font-weight: 700;
  color: #1d1d1f;
  text-align: center;
}

.card-time {
  font-size: 0.95rem;
  color: #1d1d1f;
  font-weight: 700;
  text-align: center;
}

.loading-center,
.no-alerts {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  color: #8a8a8e;
}
</style>
