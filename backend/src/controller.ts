import { Request, Response, NextFunction } from "express";
import { type AlertDoc, EmergencyLevel, EmergencyType } from "./types";
import { db } from "./couchDb";
import dayjs from "dayjs";

/**
 * Checks if a value is a valid Emergency level enum
 * @param value the value to check
 * @returns boolean
 */

function isEmergencyLevelValid(value: any): value is EmergencyLevel {
  return Object.values(EmergencyLevel).includes(value);
}

/**
 * Checks if a value is a valid Emergency type enum
 * @param value the value to check
 * @returns boolean
 */
function isEmergencyTypeValid(value: any): value is EmergencyType {
  return Object.values(EmergencyType).includes(value);
}

/**
 * Create a new alert document omitting timestamp as we want to generate that
 * @param data - form data submitted by user (excluding timestamp)
 * @returns AlertDoc with with required fields, timestamp, and is_official set to false
 */
function transformAlertData(data: Omit<AlertDoc, "timestamp">): AlertDoc {
  return {
    ...data,
    is_official: false,
    timestamp: new Date().toISOString()
  };
}

/**
 * Saves submitted alert form data to the db as a new alert doc
 * @param req request object containing alert form data in body
 * @param res returns JSON with inserted doc ID on success
 *
 * It's safe to return the ID, as it does not contain any
 * sensitive or personal info.
 */

export async function saveAlert(
  req: Request,
  res: Response,
  next: NextFunction
) {
  try {
    const alert = transformAlertData(req.body);

    if (
      alert.emergency_level !== undefined &&
      !isEmergencyLevelValid(alert.emergency_level)
    ) {
      throw new Error(`Invalid emergency_level: ${alert.emergency_level}`);
    }

    if (!isEmergencyTypeValid(alert.emergency_type)) {
      throw new Error(
        `Invalid or missing emergency_type: ${alert.emergency_type}`
      );
    }

    const dbResponse = await db.insert(alert);
    res.status(201).json({ message: "Alert saved", id: dbResponse.id });
  } catch (error) {
    next(error);
  }
}

/**
 * Fetches the latest alert document based on the timestamp
 * and sends it as JSON response.
 * @param res returns the latest document or 404 if none found.
 *
 */
export async function fetchLatestAlert(
  _req: Request,
  res: Response,
  next: NextFunction
) {
  try {
    const response = await db.find({
      selector: {},
      sort: [{ timestamp: "desc" }],
      limit: 1
    });
    if (!response.docs.length) {
      return res.status(404).json({ error: "No documents found" });
    }
    res.json(response.docs[0]);
  } catch (error) {
    next(error);
  }
}

/**
 * Fetches alerts docs from the last 24 hrs, limited to 5 results
 * Can be adjusted later to allow more alerts to show
 * @param res returns JSON array of recent alert documents.
 */
export async function fetchLast24HoursAlerts(
  _req: Request,
  res: Response,
  next: NextFunction
) {
  try {
    const twentyFourHoursAgo = dayjs().subtract(24, "hour").toISOString();

    const response = await db.find({
      selector: {
        timestamp: { $gte: twentyFourHoursAgo }
      },
      sort: [{ timestamp: "desc" }],
      limit: 5
    });

    res.json(response.docs);
  } catch (error) {
    next(error);
  }
}
