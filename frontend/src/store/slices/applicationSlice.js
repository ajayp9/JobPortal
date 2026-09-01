import { createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const BASE_URL = "http://localhost:5000/api/applications";

const getAuthConfig = () => {
  const token = localStorage.getItem("token");

  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

const getErrorMessage = (error) => {
  const responseData = error.response?.data;

  if (responseData?.message) {
    return responseData.message;
  }

  if (responseData?.fieldErrors) {
    return Object.values(responseData.fieldErrors).join(", ");
  }

  if (typeof responseData === "string") {
    return responseData;
  }

  return error.message || "Something went wrong";
};

const applicationSlice = createSlice({
  name: "applications",

  initialState: {
    applications: [],
    loading: false,
    error: null,
    message: null,
  },

  reducers: {
    requestForAllApplications(state) {
      state.loading = true;
      state.error = null;
    },

    successForAllApplications(state, action) {
      state.loading = false;
      state.error = null;
      state.applications = action.payload;
    },

    failureForAllApplications(state, action) {
      state.loading = false;
      state.error = action.payload;
    },

    requestForMyApplications(state) {
      state.loading = true;
      state.error = null;
    },

    successForMyApplications(state, action) {
      state.loading = false;
      state.error = null;
      state.applications = action.payload;
    },

    failureForMyApplications(state, action) {
      state.loading = false;
      state.error = action.payload;
    },

    requestForPostApplication(state) {
      state.loading = true;
      state.error = null;
      state.message = null;
    },

    successForPostApplication(state, action) {
      state.loading = false;
      state.error = null;
      state.message = action.payload;
    },

    failureForPostApplication(state, action) {
      state.loading = false;
      state.error = action.payload;
      state.message = null;
    },

    requestForDeleteApplication(state) {
      state.loading = true;
      state.error = null;
      state.message = null;
    },

    successForDeleteApplication(state, action) {
      state.loading = false;
      state.error = null;
      state.message = action.payload;
    },

    failureForDeleteApplication(state, action) {
      state.loading = false;
      state.error = action.payload;
      state.message = null;
    },

    clearAllErrors(state) {
      state.error = null;
    },

    resetApplicationSlice(state) {
      state.error = null;
      state.message = null;
      state.loading = false;
    },
  },
});

export const fetchEmployerApplications =
  () => async (dispatch) => {
    dispatch(
      applicationSlice.actions.requestForAllApplications()
    );

    try {
      const response = await axios.get(
        `${BASE_URL}/employer/getall`,
        getAuthConfig()
      );

      dispatch(
        applicationSlice.actions.successForAllApplications(
          response.data
        )
      );

      dispatch(applicationSlice.actions.clearAllErrors());
    } catch (error) {
      dispatch(
        applicationSlice.actions.failureForAllApplications(
          getErrorMessage(error)
        )
      );
    }
  };

export const fetchJobSeekerApplications =
  () => async (dispatch) => {
    dispatch(
      applicationSlice.actions.requestForMyApplications()
    );

    try {
      const response = await axios.get(
        `${BASE_URL}/jobseeker/getall`,
        getAuthConfig()
      );

      dispatch(
        applicationSlice.actions.successForMyApplications(
          response.data
        )
      );

      dispatch(applicationSlice.actions.clearAllErrors());
    } catch (error) {
      dispatch(
        applicationSlice.actions.failureForMyApplications(
          getErrorMessage(error)
        )
      );
    }
  };

export const postApplication =
  (data, jobId) => async (dispatch) => {
    dispatch(
      applicationSlice.actions.requestForPostApplication()
    );

    try {
      const token = localStorage.getItem("token");

      await axios.post(
        `${BASE_URL}/post/${jobId}`,
        data,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      dispatch(
        applicationSlice.actions.successForPostApplication(
          "Application submitted successfully"
        )
      );

      dispatch(applicationSlice.actions.clearAllErrors());
    } catch (error) {
      dispatch(
        applicationSlice.actions.failureForPostApplication(
          getErrorMessage(error)
        )
      );
    }
  };

export const deleteApplication =
  (applicationId) => async (dispatch) => {
    dispatch(
      applicationSlice.actions.requestForDeleteApplication()
    );

    try {
      const response = await axios.delete(
        `${BASE_URL}/delete/${applicationId}`,
        getAuthConfig()
      );

      dispatch(
        applicationSlice.actions.successForDeleteApplication(
          response.data
        )
      );

      dispatch(applicationSlice.actions.clearAllErrors());
    } catch (error) {
      dispatch(
        applicationSlice.actions.failureForDeleteApplication(
          getErrorMessage(error)
        )
      );
    }
  };

export const clearAllApplicationErrors =
  () => (dispatch) => {
    dispatch(applicationSlice.actions.clearAllErrors());
  };

export const resetApplicationSlice =
  () => (dispatch) => {
    dispatch(
      applicationSlice.actions.resetApplicationSlice()
    );
  };

export default applicationSlice.reducer;