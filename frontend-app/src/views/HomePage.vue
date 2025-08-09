<template>
  <ion-page>
    <ion-content>
      <ion-grid>
        <ion-row class="full-height ion-align-items-center">
          <hello-world />
        </ion-row>
      </ion-grid>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
  import { IonContent, IonGrid, IonRow, IonPage } from "@ionic/vue";

  import HelloWorld from "@/components/HelloWorld.vue";
</script>

<style scoped>
ion-grid,
ion-row.full-height {
  height: 100%;
}

ion-content {
  --overflow: hidden;
}
</style>
