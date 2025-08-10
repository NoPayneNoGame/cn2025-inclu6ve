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

// --- Image Asset Imports ---
// Ensure these files exist in `frontend-app/src/assets/alerts/`
import defaultIcon from "@/assets/alerts/default.png";
import floodIcon from "@/assets/alerts/flood.png";
import stormIcon from "@/assets/alerts/storm.png";
import emergencyIcon from "@/assets/alerts/level-emergency.png"; // Red icon for "Emergency"
import watchActIcon from "@/assets/alerts/level-watch-act.png"; // Orange icon for "Watch and Act"
import adviceIcon from "@/assets/alerts/level-advice.png"; // Yellow icon for "Advice"
import evacuateIcon from "@/assets/alerts/measure-evacuate.png";

// --- Mappings ---

// 1. Map Emergency Level to its specific icon (highest priority)
const ALERT_LEVEL_ICONS: Record<string, string> = {
  emergency: emergencyIcon,
  "watch and act": watchActIcon,
  advice: adviceIcon,
};

// 2. Map Emergency Type to a general icon if level is not specified
const EMERGENCY_TYPE_ICONS: Record<string, string> = {
  flood: floodIcon,
  storm: stormIcon,
};

// 3. Map specific keywords to icons (as a fallback)
const KEYWORD_ICONS: Record<string, string> = {
  flood: floodIcon,
  storm: stormIcon,
};

// 4. Map response measures to icons (for potential future use in a detail view)
const MEASURE_ICONS: Record<string, string> = {
  evacuate: evacuateIcon,
};

// --- Exported Functions ---

/**
 * Gets the primary icon for an alert based on a clear priority,
 * matching the visual design goal.
 * Priority: Emergency Level > Emergency Type > First Keyword > Default
 *
 * @param alert The alert document object.
 * @returns The path to the corresponding image asset.
 */
export function getAlertIcon(alert: AlertDoc): string {
  // 1. Check for a matching emergency level first.
  if (alert.emergency_level) {
    const levelKey = alert.emergency_level.trim().toLowerCase();
    if (ALERT_LEVEL_ICONS[levelKey]) {
      return ALERT_LEVEL_ICONS[levelKey];
    }
  }

  // 2. If no level icon, check for a matching emergency type.
  const typeKey = alert.emergency_type.trim().toLowerCase();
  if (EMERGENCY_TYPE_ICONS[typeKey]) {
    return EMERGENCY_TYPE_ICONS[typeKey];
  }

  // 3. As a fallback, check the first keyword.
  if (alert.keywords && alert.keywords.length > 0) {
    const keywordKey = alert.keywords[0].trim().toLowerCase();
    if (KEYWORD_ICONS[keywordKey]) {
      return KEYWORD_ICONS[keywordKey];
    }
  }

  // 4. Return the default icon if no other match is found.
  return defaultIcon;
}

/**
 * Retrieves a list of icons corresponding to the response measures.
 * (Useful for a detailed alert view).
 *
 * @param measures An array of response measure strings.
 * @returns An array of paths to image assets.
 */
export function getMeasureIcons(measures?: string[]): string[] {
  if (!measures?.length) return [];
  return measures
    .map((measure) => {
      const measureKey = measure.trim().toLowerCase();
      return MEASURE_ICONS[measureKey];
    })
    .filter(Boolean); // Remove any undefined entries for measures without icons
}
