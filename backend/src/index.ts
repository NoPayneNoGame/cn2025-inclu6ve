import express from "express";
import config from "./config";
import routes from "./routes";
import { errorHandler } from "./errorHandler";

const app = express();
app.use(express.json());

// Routes
app.use("/alerts", routes);

// Global error handler
app.use(errorHandler);

app.listen(config.port, () => {
  console.log(`Server running on http://localhost:${config.port}`);
});
