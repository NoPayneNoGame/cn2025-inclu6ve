import express from "express";
import config from "./config";
import routes from "./routes";
import { errorHandler } from "./errorHandler";
import cors from "cors";

const app = express();
app.use(cors());
app.use(express.json());

// Routes
app.use("/alerts", routes);

// Global error handler
app.use(errorHandler);

app.listen(config.port, () => {
  console.log(`Server running on http://localhost:${config.port}`);
});
