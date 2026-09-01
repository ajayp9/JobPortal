import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";

import {
  clearAllApplicationErrors,
  deleteApplication,
  fetchEmployerApplications,
  resetApplicationSlice,
} from "../store/slices/applicationSlice";

import Spinner from "./Spinner";

const Applications = () => {
  const { applications, loading, error, message } = useSelector(
    (state) => state.applications
  );

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchEmployerApplications());
  }, [dispatch]);

  useEffect(() => {
    if (error) {
      toast.error(error);
      dispatch(clearAllApplicationErrors());
    }

    if (message) {
      toast.success(message);
      dispatch(resetApplicationSlice());
      dispatch(fetchEmployerApplications());
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
        <h1>You have no applications from job seekers.</h1>
      ) : (
        <div className="account_components">
          <h3>Applications For Your Posted Jobs</h3>

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
                  <span>Applicant&apos;s Name: </span>
                  {element.jobSeekerName}
                </p>

                <p className="sub-sec">
                  <span>Applicant&apos;s Email: </span>
                  {element.jobSeekerEmail}
                </p>

                <p className="sub-sec">
                  <span>Applicant&apos;s Phone: </span>
                  {element.jobSeekerPhone}
                </p>

                <p className="sub-sec">
                  <span>Applicant&apos;s Address: </span>
                  {element.jobSeekerAddress}
                </p>

                <p className="sub-sec">
                  <span>Applicant&apos;s Cover Letter: </span>

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

export default Applications;