import Nano from "nano";
import config from "./config";

const couch = Nano(config.dbUrl);
export const db = couch.use(config.dbName);

/**
 * Ensures a Mango index on the 'timestamp' field exists in the database.
 * This creates a design document (_design) with the index if it doesn't already exist,
 * allowing efficient queries that sort or filter by 'timestamp'.
 */
async function ensureTimestampIndex() {
  try {
    const response = await db.createIndex({
      index: {
        fields: ["timestamp"],
      },
      name: "timestamp-index",
      type: "json",
    });
    console.log("Timestamp index ensured:", response);
  } catch (err: any) {
    if (err.statusCode === 409) {
      console.log("Timestamp index already exists.");
    } else {
      console.error("Error creating timestamp index:", err);
    }
  }
}

// Run on start
ensureTimestampIndex();

export default db;
