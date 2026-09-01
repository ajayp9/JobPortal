import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { toast } from "react-toastify";
import {
  clearAllJobErrors,
  postJob,
  resetJobSlice,
} from "../store/slices/jobSlice";
import { CiCircleInfo } from "react-icons/ci";

const JobPost = () => {
  const [title, setTitle] = useState("");
  const [jobType, setJobType] = useState("");
  const [location, setLocation] = useState("");
  const [companyName, setCompanyName] = useState("");
  const [introduction, setIntroduction] = useState("");
  const [responsibilities, setResponsibilities] = useState("");
  const [qualifications, setQualifications] = useState("");
  const [offers, setOffers] = useState("");
  const [jobNiche, setJobNiche] = useState("");
  const [salary, setSalary] = useState("");
  const [hiringMultipleCandidates, setHiringMultipleCandidates] =
    useState("");
  const [personalWebsiteTitle, setPersonalWebsiteTitle] =
    useState("");
  const [personalWebsiteUrl, setPersonalWebsiteUrl] =
    useState("");

  const nichesArray = [
    "Software Development",
    "Web Development",
    "Cybersecurity",
    "Data Science",
    "Artificial Intelligence",
    "Cloud Computing",
    "DevOps",
    "Mobile App Development",
    "Blockchain",
    "Database Administration",
    "Network Administration",
    "UI/UX Design",
    "Game Development",
    "IoT (Internet of Things)",
    "Big Data",
    "Machine Learning",
    "IT Project Management",
    "IT Support and Helpdesk",
    "Systems Administration",
    "IT Consulting",
  ];

  const cities = [
    "Mumbai",
    "Delhi",
    "Bangalore",
    "Hyderabad",
    "Ahmedabad",
    "Chennai",
    "Kolkata",
    "Karnal",
    "Pune",
    "Jaipur",
    "Lucknow",
    "Kanpur",
    "Nagpur",
    "Indore",
    "Thane",
    "Bhopal",
    "Visakhapatnam",
    "Patna",
    "Vadodara",
    "Ghaziabad",
  ];

  const { loading, error, message } = useSelector(
    (state) => state.jobs
  );

  const dispatch = useDispatch();

  const handlePostJob = (e) => {
    e.preventDefault();

    const jobData = {
      title: title.trim(),
      jobType: jobType,
      location: location,
      companyName: companyName.trim(),
      introduction: introduction.trim(),
      responsibilities: responsibilities.trim(),
      qualifications: qualifications.trim(),
      offers: offers.trim(),
      jobNiche: jobNiche,
      salary: Number(salary),
      hiringMultipleCandidates: hiringMultipleCandidates,
      personalWebsiteTitle: personalWebsiteTitle.trim(),
      personalWebsiteUrl: personalWebsiteUrl.trim(),
    };

    dispatch(postJob(jobData));
  };

  useEffect(() => {
    if (error) {
      toast.error(error);
      dispatch(clearAllJobErrors());
    }

    if (message) {
      toast.success(message);

      setTitle("");
      setJobType("");
      setLocation("");
      setCompanyName("");
      setIntroduction("");
      setResponsibilities("");
      setQualifications("");
      setOffers("");
      setJobNiche("");
      setSalary("");
      setHiringMultipleCandidates("");
      setPersonalWebsiteTitle("");
      setPersonalWebsiteUrl("");

      dispatch(resetJobSlice());
    }
  }, [dispatch, error, message]);

  return (
    <div className="account_components">
      <h3>Post A Job</h3>

      <form onSubmit={handlePostJob}>
        <div>
          <label>Title</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Job Title"
            required
          />
        </div>

        <div>
          <label>Job Type</label>
          <select
            value={jobType}
            onChange={(e) => setJobType(e.target.value)}
            required
          >
            <option value="">Select Job Type</option>
            <option value="Full-time">Full-time</option>
            <option value="Part-time">Part-time</option>
          </select>
        </div>

        <div>
          <label>Location (City)</label>
          <select
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            required
          >
            <option value="">Select Location</option>

            {cities.map((city) => (
              <option key={city} value={city}>
                {city}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label>Company Name</label>
          <input
            type="text"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
            placeholder="Company Name"
            required
          />
        </div>

        <div>
          <label>Company/Job Introduction</label>
          <textarea
            value={introduction}
            onChange={(e) => setIntroduction(e.target.value)}
            placeholder="Company / Job Introduction"
            rows={7}
            required
          />
        </div>

        <div>
          <label>Responsibilities</label>
          <textarea
            value={responsibilities}
            onChange={(e) => setResponsibilities(e.target.value)}
            placeholder="Job Responsibilities"
            rows={7}
            required
          />
        </div>

        <div>
          <label>Qualifications</label>
          <textarea
            value={qualifications}
            onChange={(e) => setQualifications(e.target.value)}
            placeholder="Required Qualifications For Job"
            rows={7}
            required
          />
        </div>

        <div>
          <div className="label-infoTag-wrapper">
            <label>What We Offer</label>

            <span>
              <CiCircleInfo /> Optional
            </span>
          </div>

          <textarea
            value={offers}
            onChange={(e) => setOffers(e.target.value)}
            placeholder="What are we offering in return!"
            rows={7}
          />
        </div>

        <div>
          <label>Job Niche</label>

          <select
            value={jobNiche}
            onChange={(e) => setJobNiche(e.target.value)}
            required
          >
            <option value="">Select Job Niche</option>

            {nichesArray.map((niche) => (
              <option key={niche} value={niche}>
                {niche}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label>Salary</label>

          <input
            type="number"
            value={salary}
            onChange={(e) => setSalary(e.target.value)}
            placeholder="50000"
            min="1"
            step="0.01"
            required
          />
        </div>

        <div>
          <div className="label-infoTag-wrapper">
            <label>Hiring Multiple Candidates?</label>

            <span>
              <CiCircleInfo /> Required
            </span>
          </div>

          <select
            value={hiringMultipleCandidates}
            onChange={(e) =>
              setHiringMultipleCandidates(e.target.value)
            }
            required
          >
            <option value="">
              Select Hiring Multiple Candidates
            </option>

            <option value="YES">Yes</option>
            <option value="NO">No</option>
          </select>
        </div>

        <div>
          <div className="label-infoTag-wrapper">
            <label>Personal Website Name</label>

            <span>
              <CiCircleInfo /> Optional
            </span>
          </div>

          <input
            type="text"
            value={personalWebsiteTitle}
            onChange={(e) =>
              setPersonalWebsiteTitle(e.target.value)
            }
            placeholder="Personal Website Name/Title"
          />
        </div>

        <div>
          <div className="label-infoTag-wrapper">
            <label>Personal Website Link (URL)</label>

            <span>
              <CiCircleInfo /> Optional
            </span>
          </div>

          <input
            type="url"
            value={personalWebsiteUrl}
            onChange={(e) =>
              setPersonalWebsiteUrl(e.target.value)
            }
            placeholder="https://example.com"
          />
        </div>

        <div>
          <button
            type="submit"
            style={{ margin: "0 auto" }}
            className="btn"
            disabled={loading}
          >
            {loading ? "Posting..." : "Post Job"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default JobPost;