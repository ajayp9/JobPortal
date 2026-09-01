const express = require("express");

const axiosClient = require("../utils/axiosClient");
const { FASTAPI_URL } = require("../config/backend");

const router = express.Router();

// FastAPI Chatbot

router.post("/", async (req, res) => {

    try {

        const response = await axiosClient({

            method: "POST",

            url: `${FASTAPI_URL}/chat`,

            headers: {
                "Content-Type": "application/json"
            },

            data: req.body

        });

        res.status(response.status).json(response.data);

    } catch (err) {

        console.error("FastAPI Error");

        console.error(err.response?.data || err.message);

        res.status(err.response?.status || 500).json({

            success: false,

            message: "FastAPI Server Error",

            error: err.response?.data || err.message

        });

    }

});

module.exports = router;