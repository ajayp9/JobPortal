import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";

import {
  clearAllApplicationErrors,
  resetApplicationSlice,
  deleteApplication,
  fetchJobSeekerApplications,
} from "../store/slices/applicationSlice";

import Spinner from "../components/Spinner";

const MyApplications = () => {
  const { loading, error, applications, message } = useSelector(
    (state) => state.applications
  );

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchJobSeekerApplications());
  }, [dispatch]);

  useEffect(() => {
    if (error) {
      toast.error(error);
      dispatch(clearAllApplicationErrors());
    }

    if (message) {
      toast.success(message);
      dispatch(resetApplicationSlice());
      dispatch(fetchJobSeekerApplications());
    }
  }, [dispatch, error, message]);

  const handleDeleteApplication = (applicationId) => {
    if (!applicationId) {
      toast.error("Application ID is missing");
      return;
    }

    dispatch(deleteApplication(applicationId));
  };

  return (
    <>
      {loading ? (
        <Spinner />
      ) : !applications || applications.length === 0 ? (
        <h1
          style={{
            fontSize: "1.4rem",
            fontWeight: "600",
          }}
        >
          You have not applied for any job.
        </h1>
      ) : (
        <div className="account_components">
          <h3>My Applications For Jobs</h3>

          <div className="applications_container">
            {applications.map((element) => (
              <div
                className="card"
                key={element.applicationId}
              >
                <p className="sub-sec">
                  <span>Job Title: </span>
                  {element.jobTitle}
                </p>

                <p className="sub-sec">
                  <span>Name: </span>
                  {element.jobSeekerName}
                </p>

                <p className="sub-sec">
                  <span>Email: </span>
                  {element.jobSeekerEmail}
                </p>

                <p className="sub-sec">
                  <span>Phone: </span>
                  {element.jobSeekerPhone}
                </p>

                <p className="sub-sec">
                  <span>Address: </span>
                  {element.jobSeekerAddress}
                </p>

                <p className="sub-sec">
                  <span>Cover Letter: </span>

                  <textarea
                    value={element.coverLetter || ""}
                    rows={5}
                    disabled
                    readOnly
                  />
                </p>

                <div className="btn-wrapper">
                  <button
                    className="outline_btn"
                    onClick={() =>
                      handleDeleteApplication(
                        element.applicationId
                      )
                    }
                    disabled={loading}
                  >
                    Delete Application
                  </button>

                  {element.resumeUrl && (
                    <Link
                      to={element.resumeUrl}
                      className="btn"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      View Resume
                    </Link>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </>
  );
};

export default MyApplications;