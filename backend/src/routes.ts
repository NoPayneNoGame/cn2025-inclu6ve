import { Router } from "express";
import {
  saveAlert,
  fetchLatestAlert,
  fetchLast24HoursAlerts
} from "./controller";

const router = Router();

router.post("/submit", saveAlert);
router.get("/latest", fetchLatestAlert);
router.get("/recent", fetchLast24HoursAlerts);

export default router;
