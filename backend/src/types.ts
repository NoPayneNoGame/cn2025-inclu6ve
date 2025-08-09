/**
 * Enums for allowed values for emergency level
 * Feel free to change this based on expected values
 **/
export enum EmergencyLevel {
  Low = "Low",
  Moderate = "Moderate",
  High = "High",
  Severe = "Severe",
  Extreme = "Extreme",
}

/**
 * Enums for allowed values for emergency type
 * mainly based on what's shown on AWS website
 * Feel free to change this based on expected values
 **/

export enum EmergencyType {
  NaturalHazard = "Natural Hazard",
  Bushfire = "Bushfire",
  Flood = "Flood",
  Storm = "Storm",
  Cyclone = "Cyclone",
  ExtremeHeat = "Extreme Heat",
  Earthquake = "Earthquake",
  Other = "Other",
}

/**
 * CouchDB as a noSQL db, let us store data as JSON object
 * meaning with key-value pairs. Below is the int for alert payload.
 * _rev is MVCC token, which is used for optimistic-concurrency detection.
 * both optional as couchDB can generate uuid and automatically assigns rev
 * Feel free to change more fields and set those as optional
 */
export interface AlertDoc {
  _id?: string;
  _rev?: string;

  is_official: boolean;
  issued_by: string;
  timestamp: string;
  emergency_level?: EmergencyLevel;
  emergency_type: EmergencyType;

  affected_area: string[];
  response_measures?: string[];
  keywords?: string[];
  note?: string;
}
