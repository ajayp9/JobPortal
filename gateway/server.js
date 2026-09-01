require("dotenv").config();

const express = require("express");
const cors = require("cors");
const morgan = require("morgan");

const {
    PORT,
    FRONTEND_URL
} = require("./config/backend");

const chatbotRoute = require("./routes/chatbot");
const springJsonRoute = require("./routes/springJson");
const springUploadRoute = require("./routes/springUpload");

const app = express();

/* =======================================================
   Middlewares
======================================================= */

app.use(cors({
    origin: FRONTEND_URL,
    credentials: true
}));

app.use(express.json());

app.use(express.urlencoded({
    extended: true
}));

app.use(morgan("dev"));

/* =======================================================
   Health Check
======================================================= */

app.get("/", (req, res) => {

    res.status(200).json({

        success: true,

        message: "Node API Gateway Running"

    });

});

/* =======================================================
   FastAPI
======================================================= */

app.use("/chat", chatbotRoute);

/* =======================================================
   Spring Boot
======================================================= */

app.use("/api", (req, res, next) => {

    // Multipart Request
    if (req.is("multipart/form-data")) {

        return springUploadRoute(req, res, next);

    }

    // JSON Request
    return springJsonRoute(req, res, next);

});

/* =======================================================
   404
======================================================= */

app.use((req, res) => {

    res.status(404).json({

        success: false,

        message: "Route Not Found"

    });

});

/* =======================================================
   Error Handler
======================================================= */

app.use((err, req, res, next) => {

    console.error(err);

    res.status(500).json({

        success: false,

        message: "Internal Server Error"

    });

});

/* =======================================================
   Start Server
======================================================= */

app.listen(PORT, () => {

    console.log("");

    console.log("======================================");

    console.log("🚀 API Gateway Started");

    console.log("======================================");

    console.log(`Gateway : http://localhost:${PORT}`);

    console.log("======================================");

});