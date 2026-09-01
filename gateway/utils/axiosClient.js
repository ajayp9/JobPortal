const axios = require("axios");

const axiosClient = axios.create({

    timeout: 30000

});

module.exports = axiosClient;