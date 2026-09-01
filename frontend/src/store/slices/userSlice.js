import { createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const userSlice = createSlice({
  name: "user",

  initialState: {
    loading: false,
    isAuthenticated: false,
    user: null,
    error: null,
    message: null,
  },

  reducers: {
    registerRequest(state) {
      state.loading = true;
      state.isAuthenticated = false;
      state.user = null;
      state.error = null;
      state.message = null;
    },

    registerSuccess(state, action) {
      state.loading = false;
      state.isAuthenticated = false;
      state.user = action.payload;
      state.error = null;
      state.message = "Registration successful";
    },

    registerFailed(state, action) {
      state.loading = false;
      state.isAuthenticated = false;
      state.user = null;
      state.error = action.payload;
      state.message = null;
    },

    loginRequest(state) {
      state.loading = true;
      state.isAuthenticated = false;
      state.user = null;
      state.error = null;
      state.message = null;
    },

    loginSuccess(state, action) {
      state.loading = false;
      state.isAuthenticated = true;
      state.user = action.payload.user;
      state.error = null;
      state.message = "Login successful";
    },

    loginFailed(state, action) {
      state.loading = false;
      state.isAuthenticated = false;
      state.user = null;
      state.error = action.payload;
      state.message = null;
    },

    fetchUserRequest(state) {
      state.loading = true;
      state.error = null;
    },

    fetchUserSuccess(state, action) {
      state.loading = false;
      state.isAuthenticated = true;
      state.user = action.payload;
      state.error = null;
    },

    fetchUserFailed(state, action) {
      state.loading = false;
      state.isAuthenticated = false;
      state.user = null;
      state.error = action.payload;
    },

    logoutSuccess(state) {
      state.loading = false;
      state.isAuthenticated = false;
      state.user = null;
      state.error = null;
      state.message = "Logout successful";
    },

    clearAllErrors(state) {
      state.error = null;
    },

    clearMessage(state) {
      state.message = null;
    },
  },
});

export const register = (data) => async (dispatch) => {
  dispatch(userSlice.actions.registerRequest());

  try {
    const response = await axios.post(
      "http://localhost:5000/api/auth/register",
      data,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );

    dispatch(userSlice.actions.registerSuccess(response.data));
    dispatch(userSlice.actions.clearAllErrors());
  } catch (error) {
    const response = error.response?.data;

    let errorMessage = "Registration failed";

    if (response?.fieldErrors) {
      errorMessage = Object.values(response.fieldErrors).join("\n");
    } else if (response?.message) {
      errorMessage = response.message;
    } else if (response?.error) {
      errorMessage = response.error;
    }

    dispatch(userSlice.actions.registerFailed(errorMessage));
  }
};

export const login = (data) => async (dispatch) => {
  dispatch(userSlice.actions.loginRequest());

  try {
    const response = await axios.post(
      "http://localhost:5000/api/auth/login",
      data,
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    const token = response.data.token;
    const user = response.data.user;

    if (!token) {
      throw new Error("Token not received from backend");
    }

    if (!user) {
      throw new Error("User details not received from backend");
    }

    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(user));

    dispatch(userSlice.actions.loginSuccess(response.data));
    dispatch(userSlice.actions.clearAllErrors());
  } catch (error) {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    dispatch(
      userSlice.actions.loginFailed(
        error.response?.data?.message ||
          error.response?.data?.error ||
          error.message ||
          "Login failed"
      )
    );
  }
};

export const getUser = () => async (dispatch) => {
  dispatch(userSlice.actions.fetchUserRequest());

  try {
    const token = localStorage.getItem("token");

    if (!token) {
      dispatch(userSlice.actions.logoutSuccess());
      return;
    }

    const response = await axios.get(
      "http://localhost:5000/api/users/profile",
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    localStorage.setItem("user", JSON.stringify(response.data));

    dispatch(userSlice.actions.fetchUserSuccess(response.data));
    dispatch(userSlice.actions.clearAllErrors());
  } catch (error) {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    dispatch(
      userSlice.actions.fetchUserFailed(
        error.response?.data?.message ||
          error.response?.data?.error ||
          "Failed to fetch user"
      )
    );
  }
};

export const logout = () => async (dispatch) => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");

  dispatch(userSlice.actions.logoutSuccess());
  dispatch(userSlice.actions.clearAllErrors());
};

export const clearAllUserErrors = () => (dispatch) => {
  dispatch(userSlice.actions.clearAllErrors());
};

export const clearUserMessage = () => (dispatch) => {
  dispatch(userSlice.actions.clearMessage());
};

export default userSlice.reducer;