
const express = require("express");

const axiosClient = require("../utils/axiosClient");
const { SPRING_BOOT_URL } = require("../config/backend");

const router = express.Router();

router.use(async (req, res) => {
  // Skip multipart requests
  if (req.is("multipart/form-data")) {
    return res.status(500).json({
      message: "Multipart request should be handled by springUpload.js",
    });
  }

  try {
    const response = await axiosClient({
      method: req.method,

      url: `${SPRING_BOOT_URL}${req.originalUrl}`,

      headers: {
        Authorization: req.headers.authorization || "",
        "Content-Type": "application/json",
      },

      data: req.body,
    });

    return res.status(response.status).json(response.data);
  } catch (err) {
    console.error("========== SPRING JSON ERROR ==========");
    console.error(err.response?.status);
    console.error(err.response?.data || err.message);

    // Forward Spring Boot response exactly
    if (err.response) {
      return res.status(err.response.status).json(err.response.data);
    }

    // Spring Boot server unavailable
    return res.status(500).json({
      success: false,
      message: "Unable to connect to Spring Boot.",
    });
  }
});

module.exports = router;
