import type { AlertDoc } from "@/types/alerts";

import defaultIcon from "@/assets/alerts/default.png";
import adviceIcon from "@/assets/alerts/advice.png";
import emergencyIcon from "@/assets/alerts/emergency_warning.png";
import watchAndActIcon from "@/assets/alerts/watch_and_act.png";
import evacuateIcon from "@/assets/alerts/evacuation.jpg";
import fireIcon from "@/assets/alerts/fire.jpg";
import tsunamiIcon from "@/assets/alerts/tsunami.png";
import moveHigherGroundIcon from "@/assets/alerts/move_to_higher_ground.jpg";
import secureLooseItems from "@/assets/alerts/secure_loose_object.png";

// Single source of truth: keyword -> icon
// Keys are lowercase, normalized (spaces).
const KEYWORD_ICONS_MAP: Record<string, string> = {
  // levels
  emergency: emergencyIcon,
  "watch and act": watchAndActIcon,
  advice: adviceIcon,
  "secure loose items": secureLooseItems,

  // types
  fire: fireIcon,
  tsunami: tsunamiIcon,

  // measures
  evacuate: evacuateIcon,
  "move to higher ground": moveHigherGroundIcon,
};

// Normalize db keyword strings to our keys
function norm(s: string): string {
  return s.trim().toLowerCase().replace(/[_-]+/g, " ").replace(/\s+/g, " ");
}

// Return ALL matching icons (deduped) based only on keywords.
// Order follows the order in alert.keywords.
export function getKeywordIcons(alert: AlertDoc): string[] {
  const out: string[] = [];
  const seen = new Set<string>();
  const kws = alert.keywords ?? [];
  for (const kw of kws) {
    const key = norm(kw);
    const icon = KEYWORD_ICONS_MAP[key];
    if (icon && !seen.has(icon)) {
      out.push(icon);
      seen.add(icon);
    }
  }
  return out.length ? out : [defaultIcon];
}

// Back-compat: first icon only (used if a single image is needed)
export function getAlertIcon(alert: AlertDoc): string {
  return getKeywordIcons(alert)[0];
}
