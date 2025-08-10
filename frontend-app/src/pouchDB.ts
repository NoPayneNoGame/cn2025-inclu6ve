import PouchDB from "pouchdb-browser";

/**
 * for pouchdb to work, add define: {global: "window"}
 * under defineConfig in vite.config.js
 * and install events
 **/

const localDB = new PouchDB(import.meta.env.VITE_LOCAL_DB);
const remoteDB = new PouchDB(import.meta.env.VITE_DB_URL);

localDB
  .sync(remoteDB, {
    live: true,
    retry: true,
  })
  .on("change", (info: any) => {
    console.log("Sync change:", info);
  })
  .on("paused", (err: any) => {
    console.log("Sync paused. Offline or up to date.", err);
  })
  .on("active", () => {
    console.log("Sync resumed.");
  })
  .on("denied", (err: any) => {
    console.error("Sync denied (e.g., permissions)", err);
  })
  .on("error", (err: any) => {
    console.error("Sync error:", err);
  });

export { localDB, remoteDB };
