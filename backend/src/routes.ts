import { Router } from 'express';
import {
  saveAlert,
  fetchLatestUpdate,
  fetchLast24HoursUpdates,
} from './controller';

const router = Router();

router.post('/submit', saveAlert);
router.get('/latest', fetchLatestUpdate);
router.get('/recent', fetchLast24HoursUpdates);

export default router;
