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
        <div
          v-for="alert in state.alerts"
          :key="alert._id"
          class="alert-card"
          @click="openAlertModal(alert)"

          >
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

      <ion-modal :is-open="showModal" @didDismiss="showModal = false">
        <ion-header>
          <ion-toolbar>
            <ion-title>
              {{
                selectedAlert?.emergency_level
                  ? capitalize(selectedAlert.emergency_level)
                  : "Alert"
              }}
            </ion-title>
            <ion-buttons slot="end">
              <ion-button @click="showModal = false">Close</ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>

        <ion-content class="ion-padding">
          <div v-if="selectedAlert" class="modal-body">
            <div class="modal-icons-row">
              <img
                v-for="(icon, i) in getKeywordIcons(selectedAlert)"
                :key="i"
                :src="icon"
                alt="Alert icon"
                class="modal-icon"
              />
            </div>

            <div class="modal-title">
              <strong>{{ compactTitle(selectedAlert) }}</strong>
            </div>
            <div class="modal-time">
              <strong>{{ formatTime(selectedAlert.timestamp) }}</strong>
            </div>

            <div class="modal-grid">
              <p><strong>Issued by:</strong> {{ selectedAlert.issued_by }}</p>
              <p v-if="selectedAlert.affected_area?.length">
                <strong>Affected area:</strong>
                {{ selectedAlert.affected_area.join(", ") }}
              </p>
              <div v-if="selectedAlert.response_measures?.length">
                <strong>Response measures:</strong>
                <ul>
                  <li
                    v-for="(m, idx) in selectedAlert.response_measures"
                    :key="idx"
                  >
                    {{ m }}
                  </li>
                </ul>
              </div>
              <p v-if="selectedAlert.note">
                <strong>Note:</strong> {{ selectedAlert.note }}
              </p>
              <p v-if="selectedAlert.keywords?.length">
                <strong>Keywords:</strong>
                {{ selectedAlert.keywords.join(", ") }}
              </p>
            </div>
          </div>
        </ion-content>
      </ion-modal>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import {
  IonPage,
  IonContent,
  IonSpinner,
  IonModal,
  IonButtons,
  IonButton,
  type RefresherCustomEvent,
} from "@ionic/vue";
import { onMounted, reactive, ref } from "vue";
import axios from "axios";
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

const showModal = ref(false);
const selectedAlert = ref<AlertDoc | null>(null);

function openAlertModal(alert: AlertDoc) {
  selectedAlert.value = alert;
  showModal.value = true;
}
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
  width: 100%;
  padding-bottom: 8px;
}
.icon-xl {
  width: 80px;
  height: 80px;
  flex: 0 0 80px;
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
.loading-center {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80vh;
}

/* Modal styles (center images/text; bigger icons; no shading) */
.modal-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #1d1d1f;
}
.modal-icons-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  width: 100%;
}
.modal-icon {
  width: 96px;
  height: 96px;
  object-fit: contain;
  filter: none;
  border-radius: 8px;
}
.modal-title {
  font-size: 1.1rem;
  text-align: center;
}
.modal-time {
  font-size: 1rem;
  text-align: center;
}
.modal-grid {
  display: grid;
  gap: 6px;
  width: 100%;
  color: #1d1d1f;
}
.no-alerts {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  color: #8a8a8e;
}
</style>
