/// <reference types="vitest" />

import legacy from "@vitejs/plugin-legacy";
import vue from "@vitejs/plugin-vue";
import path from "path";
import { defineConfig } from "vite";

// eslint-disable-next-line no-restricted-imports
import pkg from "./package.json" with { type: "json" };

const PORT = process.env.PORT ? parseInt(process.env.PORT) : 8080;

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    legacy(),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    port: PORT,
    fs: { strict: true },
  },
  test: {
    globals: true,
    environment: "jsdom",
  },
  define: {
    __APP_VERSION__: JSON.stringify(pkg.version),
  },
});
