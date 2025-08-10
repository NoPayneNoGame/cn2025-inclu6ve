import { createRouter, createWebHistory } from "@ionic/vue-router";
import type { RouteRecordRaw } from "vue-router";

import HomePage from "@/views/HomePage.vue";
import WalkthroughPage1 from "@/views/WalkthroughPage1.vue";
import WalkthroughPage2 from "@/views/WalkthroughPage2.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "Home",
    component: HomePage,
  },
  {
    path: "/walkthrough1",
    name: "Walkthrough1",
    component: WalkthroughPage1,
  },
  {
    path: "/walkthrough2",
    name: "Walkthrough2",
    component: WalkthroughPage2,
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
