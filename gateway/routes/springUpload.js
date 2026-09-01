
const express = require("express");
const FormData = require("form-data");

const upload = require("../middleware/upload");
const axiosClient = require("../utils/axiosClient");
const { SPRING_BOOT_URL } = require("../config/backend");

const router = express.Router();

router.use(upload.any());

router.use(async (req, res, next) => {

    // Skip JSON requests
    if (!req.is("multipart/form-data")) {
        return next();
    }

    try {

        const form = new FormData();

        // Forward all text fields
        Object.keys(req.body).forEach((key) => {
            form.append(key, req.body[key]);
        });

        // Forward uploaded files
        if (req.files && req.files.length > 0) {

            req.files.forEach((file) => {

                form.append(
                    file.fieldname,
                    file.buffer,
                    {
                        filename: file.originalname,
                        contentType: file.mimetype
                    }
                );

            });

        }

        const response = await axiosClient({

            method: req.method,

            url: `${SPRING_BOOT_URL}${req.originalUrl}`,

            headers: {
                ...form.getHeaders(),
                Authorization: req.headers.authorization || ""
            },

            params: req.query,

            data: form,

            maxBodyLength: Infinity,
            maxContentLength: Infinity

        });

        return res.status(response.status).json(response.data);

    } catch (err) {

        console.error("========== SPRING UPLOAD ERROR ==========");
        console.error(err.response?.status);
        console.error(err.response?.data || err.message);

        // Forward Spring Boot error exactly
        if (err.response) {
            return res
                .status(err.response.status)
                .json(err.response.data);
        }

        // Spring Boot server unavailable
        return res.status(500).json({
            success: false,
            message: "Unable to connect to Spring Boot."
        });

    }

});

module.exports = router;