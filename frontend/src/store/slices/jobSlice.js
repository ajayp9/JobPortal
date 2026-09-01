import { createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const BASE_URL = "http://localhost:5000/api/jobs";

const getAuthConfig = () => {
  const token = localStorage.getItem("token");

  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

const getErrorMessage = (error) => {
  return (
    error.response?.data?.message ||
    error.response?.data ||
    error.message ||
    "Something went wrong"
  );
};

const jobSlice = createSlice({
  name: "jobs",

  initialState: {
    jobs: [],
    loading: false,
    error: null,
    message: null,
    singleJob: {},
    myJobs: [],
  },

  reducers: {
    requestForAllJobs(state) {
      state.loading = true;
      state.error = null;
    },

    successForAllJobs(state, action) {
      state.loading = false;
      state.jobs = action.payload;
      state.error = null;
    },

    failureForAllJobs(state, action) {
      state.loading = false;
      state.error = action.payload;
    },

    requestForSingleJob(state) {
      state.message = null;
      state.error = null;
      state.loading = true;
    },

    successForSingleJob(state, action) {
      state.loading = false;
      state.error = null;
      state.singleJob = action.payload;
    },

    failureForSingleJob(state, action) {
      state.error = action.payload;
      state.loading = false;
    },

    requestForPostJob(state) {
      state.message = null;
      state.error = null;
      state.loading = true;
    },

    successForPostJob(state, action) {
      state.message = action.payload;
      state.error = null;
      state.loading = false;
    },

    failureForPostJob(state, action) {
      state.message = null;
      state.error = action.payload;
      state.loading = false;
    },

    requestForMyJobs(state) {
      state.loading = true;
      state.myJobs = [];
      state.error = null;
    },

    successForMyJobs(state, action) {
      state.loading = false;
      state.myJobs = action.payload;
      state.error = null;
    },

    failureForMyJobs(state, action) {
      state.loading = false;
      state.error = action.payload;
    },

    requestForDeleteJob(state) {
      state.loading = true;
      state.error = null;
      state.message = null;
    },

    successForDeleteJob(state, action) {
      state.loading = false;
      state.error = null;
      state.message = action.payload;
    },

    failureForDeleteJob(state, action) {
      state.loading = false;
      state.error = action.payload;
      state.message = null;
    },

    clearAllErrors(state) {
      state.error = null;
    },

    resetJobSlice(state) {
      state.error = null;
      state.loading = false;
      state.message = null;
      state.singleJob = {};
    },
  },
});

// export const fetchJobs =
//   (city, niche, searchKeyword = "") =>
//   async (dispatch) => {
//     try {
//       dispatch(jobSlice.actions.requestForAllJobs());

//       const queryParams = new URLSearchParams();

//       if (searchKeyword?.trim()) {
//         queryParams.append("searchKeyword", searchKeyword.trim());
//       }

//       if (city?.trim()) {
//         queryParams.append("city", city.trim());
//       }

//       if (niche?.trim()) {
//         queryParams.append("niche", niche.trim());
//       }

//       let link = `${BASE_URL}/getall`;

//       const queryString = queryParams.toString();

//       if (queryString) {
//         link += `?${queryString}`;
//       }

//       const response = await axios.get(link);

//       dispatch(
//         jobSlice.actions.successForAllJobs(response.data)
//       );

//       dispatch(jobSlice.actions.clearAllErrors());
//     } catch (error) {
//       dispatch(
//         jobSlice.actions.failureForAllJobs(
//           getErrorMessage(error)
//         )
//       );
//     }
//   };

export const fetchJobs =
  (city, niche, searchKeyword = "") =>
  async (dispatch) => {
    try {
      dispatch(jobSlice.actions.requestForAllJobs());

      const queryParams = new URLSearchParams();

      if (searchKeyword?.trim()) {
        queryParams.append("searchKeyword", searchKeyword.trim());
      }

      if (city?.trim()) {
        queryParams.append("city", city.trim());
      }

      if (niche?.trim()) {
        queryParams.append("niche", niche.trim());
      }

      let link = `${BASE_URL}/getall`;

      const queryString = queryParams.toString();

      if (queryString) {
        link += `?${queryString}`;
      }

      // Debug logs
      console.log("City:", city);
      console.log("Niche:", niche);
      console.log("Keyword:", searchKeyword);
      console.log("Request URL:", link);

      const response = await axios.get(link);

      console.log("Response:", response.data);

      dispatch(jobSlice.actions.successForAllJobs(response.data));
      dispatch(jobSlice.actions.clearAllErrors());
    } catch (error) {
      console.error("Search Error:", error);

      dispatch(
        jobSlice.actions.failureForAllJobs(
          getErrorMessage(error)
        )
      );
    }
  };

export const fetchSingleJob =
  (jobId) =>
  async (dispatch) => {
    try {
      dispatch(jobSlice.actions.requestForSingleJob());

      const response = await axios.get(
        `${BASE_URL}/get/${jobId}`
      );

      dispatch(
        jobSlice.actions.successForSingleJob(
          response.data
        )
      );

      dispatch(jobSlice.actions.clearAllErrors());
    } catch (error) {
      dispatch(
        jobSlice.actions.failureForSingleJob(
          getErrorMessage(error)
        )
      );
    }
  };

export const postJob =
  (data) =>
  async (dispatch) => {
    try {
      dispatch(jobSlice.actions.requestForPostJob());

      const response = await axios.post(
        `${BASE_URL}/post`,
        data,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem(
              "token"
            )}`,
          },
        }
      );

      dispatch(
        jobSlice.actions.successForPostJob(
          "Job posted successfully"
        )
      );

      dispatch(jobSlice.actions.clearAllErrors());

      return response.data;
    } catch (error) {
      dispatch(
        jobSlice.actions.failureForPostJob(
          getErrorMessage(error)
        )
      );

      throw error;
    }
  };

export const getMyJobs =
  () =>
  async (dispatch) => {
    try {
      dispatch(jobSlice.actions.requestForMyJobs());

      const response = await axios.get(
        `${BASE_URL}/getmyjobs`,
        getAuthConfig()
      );

      dispatch(
        jobSlice.actions.successForMyJobs(
          response.data
        )
      );

      dispatch(jobSlice.actions.clearAllErrors());
    } catch (error) {
      dispatch(
        jobSlice.actions.failureForMyJobs(
          getErrorMessage(error)
        )
      );
    }
  };

export const deleteJob =
  (id) =>
  async (dispatch) => {
    try {
      dispatch(jobSlice.actions.requestForDeleteJob());

      const response = await axios.delete(
        `${BASE_URL}/delete/${id}`,
        getAuthConfig()
      );

      dispatch(
        jobSlice.actions.successForDeleteJob(
          response.data
        )
      );

      dispatch(jobSlice.actions.clearAllErrors());

      dispatch(getMyJobs());
    } catch (error) {
      dispatch(
        jobSlice.actions.failureForDeleteJob(
          getErrorMessage(error)
        )
      );
    }
  };

export const clearAllJobErrors = () => (dispatch) => {
  dispatch(jobSlice.actions.clearAllErrors());
};

export const resetJobSlice = () => (dispatch) => {
  dispatch(jobSlice.actions.resetJobSlice());
};

export default jobSlice.reducer;