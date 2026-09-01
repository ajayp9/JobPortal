import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { clearAllUserErrors, login } from "../store/slices/userSlice";
import { toast } from "react-toastify";
import { MdOutlineMailOutline } from "react-icons/md";
import { RiLock2Fill } from "react-icons/ri";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const { loading, isAuthenticated, error, user } = useSelector(
    (state) => state.user
  );

  const dispatch = useDispatch();
  const navigateTo = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    const loginData = {
      email,
      password,
    };

    dispatch(login(loginData));
  };

  useEffect(() => {
    if (error) {
      toast.error(error);
      dispatch(clearAllUserErrors());
    }

    if (isAuthenticated && user) {
      if (user.role === "JOB_SEEKER") {
        navigateTo("/jobs", { replace: true });
      } else if (user.role === "EMPLOYER") {
        navigateTo("/dashboard", { replace: true });
      } else {
        navigateTo("/", { replace: true });
      }
    }
  }, [dispatch, error, isAuthenticated, user, navigateTo]);

  return (
    <section className="authPage">
      <div className="container login-container">
        <div className="header">
          <h3>Login to your account</h3>
        </div>

        <form onSubmit={handleLogin}>
          <div className="inputTag">
            <label>Email</label>

            <div>
              <input
                type="email"
                placeholder="youremail@gmail.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />

              <MdOutlineMailOutline />
            </div>
          </div>

          <div className="inputTag">
            <label>Password</label>

            <div>
              <input
                type="password"
                placeholder="Your Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />

              <RiLock2Fill />
            </div>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>

          <Link to="/register">Register Now</Link>
        </form>
      </div>
    </section>
  );
};

export default Login;