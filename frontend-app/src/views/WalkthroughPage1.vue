<style scoped>
  /* This style is for demonstration purposes only. */
  /* It's not required for the tabs to function. */
  ion-content::part(scroll) {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .example-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 100%;
  }
  .ion-toolbar {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
</style>
<template>
  <ion-page>
      <div id="home-page">
        <ion-header>
        </ion-header>
        <ion-content :fullscreen="true" class="ion-padding">
          <div class="example-content">
          <img src ="/bluetooth-icon.svg"/>
          <ion-toggle :checked="false"></ion-toggle><br /><br />
          <h3>Turn on Bluetooth and location to receive the alert.</h3>
          </div>
        </ion-content>
      </div>

    <ion-item class="ion-toolbar">
    <ion-button :disabled = "true" slot="start" fill="clear">
          <ion-label>Prev</ion-label>
      </ion-button>
      <ion-button slot="end" fill="clear" href="/walkthrough2">
          <ion-label>Next</ion-label>
      </ion-button>
    </ion-item>
  </ion-page>
</template>

<script setup lang="ts">
  import {
    IonTabs,
    IonPage,
    IonItem,
    IonTab,
    IonRouterOutlet,
    IonToolbar,
    IonButton,
    IonContent,
    IonHeader,
    IonTitle,
    IonIcon,
    IonFooter,
  } from '@ionic/vue';

  import { playCircle, radio, library, search } from 'ionicons/icons';

</script>