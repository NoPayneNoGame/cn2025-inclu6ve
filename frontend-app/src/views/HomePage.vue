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

      <ion-list v-if="state.alerts.length > 0">
        <ion-card v-for="alert in state.alerts" :key="alert._id || alert.timestamp">
          <ion-card-header>
            <ion-card-title>
              {{ alert.emergency_type }} <span v-if="alert.emergency_level">({{ alert.emergency_level }})</span>
            </ion-card-title>
            <ion-card-subtitle>
              Issued by: {{ alert.issued_by }} | {{ formatDate(alert.timestamp) }}
            </ion-card-subtitle>
          </ion-card-header>
          <ion-card-content>
            <div v-if="alert.note">{{ alert.note }}</div>
            <div v-if="alert.affected_area && alert.affected_area.length">
              <strong>Affected Areas:</strong> {{ alert.affected_area.join(", ") }}
            </div>
            <div v-if="alert.response_measures && alert.response_measures.length">
              <strong>Response Measures:</strong> {{ alert.response_measures.join(", ") }}
            </div>
            <div v-if="alert.keywords && alert.keywords.length">
              <strong>Keywords:</strong> {{ alert.keywords.join(", ") }}
            </div>
          </ion-card-content>
        </ion-card>
      </ion-list>
      <div v-else class="no-alerts">
        No alerts in the last 24 hours.
      </div>
    </ion-content>
  </ion-page>
</template>


<script setup lang="ts">
  import { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent } from "@ionic/vue";
  import axios from "axios";
  import { reactive } from "vue";

  // AlertDoc type based on backend/src/types.ts
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
    if (dispLoaderPage) {
      state.loading = true;
    }

    try {
      const result = await axios.get("http://localhost:8080/alerts/recent");
      if (Array.isArray(result.data)) {
        state.alerts = result.data;
      } else {
        state.alerts = [];
      }
    } catch (e) {
      state.alerts = [];
    }

    state.loading = false;
  };

  const doRefresh = (event: CustomEvent) => {
    fetchLast24HoursAlerts(false);
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    event.target?.complete();
  };

  function formatDate(ts: string) {
    const d = new Date(ts);
    return d.toLocaleString();
  }

  fetchLast24HoursAlerts(true);
</script>

<style>
.loading-center{
  display: flex;
  align-items: center;
  justify-content: center;
  height: 90vh;
}
ion-spinner{
  transform: scale(1.5);
}
.no-alerts {
  text-align: center;
  margin-top: 2rem;
  color: #888;
}
</style>