import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Link, useParams } from "react-router-dom";
import { toast } from "react-toastify";

import {
  clearAllApplicationErrors,
  postApplication,
  resetApplicationSlice,
} from "../store/slices/applicationSlice";

import { fetchSingleJob } from "../store/slices/jobSlice";

import { IoMdCash } from "react-icons/io";
import { FaToolbox } from "react-icons/fa";
import { FaLocationDot } from "react-icons/fa6";

const PostApplication = () => {
  const { singleJob } = useSelector((state) => state.jobs);

  const { isAuthenticated, user } = useSelector(
    (state) => state.user
  );

  const { loading, error, message } = useSelector(
    (state) => state.applications
  );

  const { jobId } = useParams();
  const dispatch = useDispatch();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");
  const [coverLetter, setCoverLetter] = useState("");
  const [resume, setResume] = useState(null);

  useEffect(() => {
    if (jobId) {
      dispatch(fetchSingleJob(jobId));
    }
  }, [dispatch, jobId]);

  useEffect(() => {
    if (user) {
      setName(user.name || "");
      setEmail(user.email || "");
      setPhone(user.phone || "");
      setAddress(user.address || "");
      setCoverLetter(user.coverLetter || "");
    }
  }, [user]);

  useEffect(() => {
    if (error) {
      toast.error(error);
      dispatch(clearAllApplicationErrors());
    }

    if (message) {
      toast.success(message);
      dispatch(resetApplicationSlice());
    }
  }, [dispatch, error, message]);

  const handlePostApplication = (event) => {
    event.preventDefault();

    if (!jobId) {
      toast.error("Job ID is missing");
      return;
    }

    if (!name.trim()) {
      toast.error("Name is required");
      return;
    }

    if (!email.trim()) {
      toast.error("Email is required");
      return;
    }

    if (!phone.trim()) {
      toast.error("Phone number is required");
      return;
    }

    if (!address.trim()) {
      toast.error("Address is required");
      return;
    }

    if (!coverLetter.trim()) {
  toast.error("Cover letter is required");
  return;
}

if (coverLetter.trim().length < 20) {
  toast.error("Cover letter must contain at least 20 characters");
  return;
}

    const formData = new FormData();

    formData.append("name", name);
    formData.append("email", email);
    formData.append("phone", phone);
    formData.append("address", address);
    formData.append("coverLetter", coverLetter);

    if (resume) {
      formData.append("resume", resume);
    }

    dispatch(postApplication(formData, jobId));
  };

  const resumeHandler = (event) => {
    const selectedFile = event.target.files?.[0];

    if (!selectedFile) {
      setResume(null);
      return;
    }

    const allowedTypes = [
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];

    if (!allowedTypes.includes(selectedFile.type)) {
      toast.error("Please select a PDF, DOC or DOCX file");
      event.target.value = "";
      setResume(null);
      return;
    }

    if (selectedFile.size > 5 * 1024 * 1024) {
      toast.error("Resume size must be less than 5 MB");
      event.target.value = "";
      setResume(null);
      return;
    }

    setResume(selectedFile);
  };

  const qualifications = singleJob?.qualifications
    ? singleJob.qualifications
        .split(". ")
        .filter((item) => item.trim() !== "")
    : [];

  const responsibilities = singleJob?.responsibilities
    ? singleJob.responsibilities
        .split(". ")
        .filter((item) => item.trim() !== "")
    : [];

  const offering = singleJob?.offers
    ? singleJob.offers
        .split(". ")
        .filter((item) => item.trim() !== "")
    : [];

  return (
    <article className="application_page">
      <form onSubmit={handlePostApplication}>
        <h3>Application Form</h3>

        <div>
          <label>Job Title</label>

          <input
            type="text"
            value={singleJob?.title || ""}
            placeholder="Job title"
            disabled
          />
        </div>

        <div>
          <label>Your Name</label>

          <input
            type="text"
            value={name}
            placeholder="Enter your name"
            onChange={(event) => setName(event.target.value)}
            required
          />
        </div>

        <div>
          <label>Your Email</label>

          <input
            type="email"
            value={email}
            placeholder="Enter your email"
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </div>

        <div>
          <label>Phone Number</label>

          <input
            type="tel"
            value={phone}
            placeholder="Enter your phone number"
            maxLength={10}
            onChange={(event) => setPhone(event.target.value)}
            required
          />
        </div>

        <div>
          <label>Address</label>

          <input
            type="text"
            value={address}
            placeholder="Enter your address"
            onChange={(event) => setAddress(event.target.value)}
            required
          />
        </div>

        {user?.role === "JOB_SEEKER" && (
          <>
            <div>
              <label>Cover Letter</label>

              <textarea
                value={coverLetter}
                placeholder="Write your cover letter"
                onChange={(event) =>
                  setCoverLetter(event.target.value)
                }
                rows={10}
                required
              />
            </div>

            <div>
              <label>Resume</label>

              <input
                type="file"
                accept=".pdf,.doc,.docx"
                onChange={resumeHandler}
              />

              {resume && <p>Selected file: {resume.name}</p>}
            </div>
          </>
        )}

        {isAuthenticated && user?.role === "JOB_SEEKER" && (
          <div style={{ alignItems: "flex-end" }}>
            <button
              type="submit"
              className="btn"
              disabled={loading}
            >
              {loading ? "Applying..." : "Apply"}
            </button>
          </div>
        )}
      </form>

      <div className="job-details">
        <header>
          <h3>{singleJob?.title}</h3>

          {singleJob?.personalWebsiteUrl && (
            <Link
              target="_blank"
              rel="noopener noreferrer"
              to={singleJob.personalWebsiteUrl}
            >
              {singleJob.personalWebsiteTitle ||
                "Company Website"}
            </Link>
          )}

          <p>{singleJob?.location}</p>

          <p>
            Rs. {singleJob?.salary} a month
          </p>
        </header>

        <hr />

        <section>
          <div className="wrapper">
            <h3>Job details</h3>

            <div>
              <IoMdCash />

              <div>
                <span>Pay</span>
                <span>
                  Rs. {singleJob?.salary} a month
                </span>
              </div>
            </div>

            <div>
              <FaToolbox />

              <div>
                <span>Job type</span>
                <span>{singleJob?.jobType}</span>
              </div>
            </div>
          </div>

          <hr />

          <div className="wrapper">
            <h3>Location</h3>

            <div className="location-wrapper">
              <FaLocationDot />
              <span>{singleJob?.location}</span>
            </div>
          </div>

          <hr />

          <div className="wrapper">
            <h3>Full Job Description</h3>

            <p>{singleJob?.introduction}</p>

            {qualifications.length > 0 && (
              <div>
                <h4>Qualifications</h4>

                <ul>
                  {qualifications.map((element, index) => (
                    <li
                      key={`${element}-${index}`}
                      style={{ listStyle: "inside" }}
                    >
                      {element}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {responsibilities.length > 0 && (
              <div>
                <h4>Responsibilities</h4>

                <ul>
                  {responsibilities.map((element, index) => (
                    <li
                      key={`${element}-${index}`}
                      style={{ listStyle: "inside" }}
                    >
                      {element}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {offering.length > 0 && (
              <div>
                <h4>Offering</h4>

                <ul>
                  {offering.map((element, index) => (
                    <li
                      key={`${element}-${index}`}
                      style={{ listStyle: "inside" }}
                    >
                      {element}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        </section>

        <hr />

        <footer>
          <h3>Job Niche</h3>
          <p>{singleJob?.jobNiche}</p>
        </footer>
      </div>
    </article>
  );
};

export default PostApplication;