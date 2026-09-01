import { createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const updateProfileSlice = createSlice({
  name: "updateProfile",

  initialState: {
    loading: false,
    error: null,
    isUpdated: false,
    message: null,
  },

  reducers: {
    updateProfileRequest(state) {
      state.loading = true;
      state.error = null;
      state.isUpdated = false;
      state.message = null;
    },

    updateProfileSuccess(state, action) {
      state.loading = false;
      state.error = null;
      state.isUpdated = true;
      state.message =
        action.payload?.message || "Profile updated successfully";
    },

    updateProfileFailed(state, action) {
      state.loading = false;
      state.error = action.payload;
      state.isUpdated = false;
      state.message = null;
    },

    updatePasswordRequest(state) {
      state.loading = true;
      state.error = null;
      state.isUpdated = false;
      state.message = null;
    },

    updatePasswordSuccess(state, action) {
      state.loading = false;
      state.error = null;
      state.isUpdated = true;
      state.message =
        action.payload?.message || "Password updated successfully";
    },

    updatePasswordFailed(state, action) {
      state.loading = false;
      state.error = action.payload;
      state.isUpdated = false;
      state.message = null;
    },

    profileResetAfterUpdate(state) {
      state.loading = false;
      state.error = null;
      state.isUpdated = false;
      state.message = null;
    },
  },
});

export const updateProfile = (data) => async (dispatch) => {
  dispatch(updateProfileSlice.actions.updateProfileRequest());

  try {
    const token = localStorage.getItem("token");

    const formData = new FormData();

    formData.append("name", data.name || "");
    formData.append("phone", data.phone || "");
    formData.append("address", data.address || "");
    formData.append("coverLetter", data.coverLetter || "");
    formData.append("firstNiche", data.firstNiche || "");
    formData.append("secondNiche", data.secondNiche || "");
    formData.append("thirdNiche", data.thirdNiche || "");

    if (data.resume instanceof File) {
      formData.append("resume", data.resume);
    }

    const response = await axios.put(
      "http://localhost:5000/api/users/profile",
      formData,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    dispatch(
      updateProfileSlice.actions.updateProfileSuccess(response.data)
    );
  } catch (error) {
    const response = error.response?.data;

    let errorMessage = "Failed to update profile";

    if (response?.fieldErrors) {
      errorMessage = Object.values(response.fieldErrors).join(", ");
    } else if (response?.message) {
      errorMessage = response.message;
    } else if (response?.error) {
      errorMessage = response.error;
    }

    dispatch(
      updateProfileSlice.actions.updateProfileFailed(errorMessage)
    );
  }
};
export const updatePassword = (data) => async (dispatch) => {
  dispatch(updateProfileSlice.actions.updatePasswordRequest());

  try {
    const token = localStorage.getItem("token");

    const response = await axios.put(
      "http://localhost:5000/api/users/change-password",
      data,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    dispatch(
      updateProfileSlice.actions.updatePasswordSuccess(response.data)
    );
  } catch (error) {
    const response = error.response?.data;

    let errorMessage = "Failed to update password";

    if (response?.fieldErrors) {
      errorMessage = Object.values(response.fieldErrors).join(", ");
    } else if (response?.message) {
      errorMessage = response.message;
    } else if (response?.error) {
      errorMessage = response.error;
    }

    dispatch(
      updateProfileSlice.actions.updatePasswordFailed(errorMessage)
    );
  }
};

export const clearAllUpdateProfileErrors = () => (dispatch) => {
  dispatch(updateProfileSlice.actions.profileResetAfterUpdate());
};

export default updateProfileSlice.reducer;