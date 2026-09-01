require("dotenv").config();

module.exports = {

    PORT: process.env.PORT,

    FRONTEND_URL: process.env.FRONTEND_URL,

    SPRING_BOOT_URL: process.env.SPRING_BOOT_URL,

    FASTAPI_URL: process.env.FASTAPI_URL,

    DOTNET_URL: process.env.DOTNET_URL

};