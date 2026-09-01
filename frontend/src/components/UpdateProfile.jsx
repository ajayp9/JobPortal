import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import {
  clearAllUpdateProfileErrors,
  updateProfile,
} from "../store/slices/updateProfileSlice";
import { toast } from "react-toastify";
import { getUser } from "../store/slices/userSlice";

const UpdateProfile = () => {
  const { user } = useSelector((state) => state.user);

  const { loading, error, isUpdated, message } = useSelector(
    (state) => state.updateProfile
  );

  const dispatch = useDispatch();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");
  const [coverLetter, setCoverLetter] = useState("");
  const [firstNiche, setFirstNiche] = useState("");
  const [secondNiche, setSecondNiche] = useState("");
  const [thirdNiche, setThirdNiche] = useState("");
  const [resume, setResume] = useState(null);
  const [resumePreview, setResumePreview] = useState("");

  useEffect(() => {
    if (user) {
      setName(user.name || "");
      setEmail(user.email || "");
      setPhone(user.phone || "");
      setAddress(user.address || "");
      setCoverLetter(user.coverLetter || "");
      setFirstNiche(user.niches?.firstNiche || "");
      setSecondNiche(user.niches?.secondNiche || "");
      setThirdNiche(user.niches?.thirdNiche || "");
      setResumePreview(user.resume?.url || "");
    }
  }, [user]);

  const handleUpdateProfile = () => {
    if (!name.trim()) {
      toast.error("Name is required");
      return;
    }

    if (name.trim().length < 3) {
      toast.error("Name must be at least 3 characters");
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

    const noChanges =
      name.trim() === (user?.name || "") &&
      String(phone).trim() === String(user?.phone || "") &&
      address.trim() === (user?.address || "") &&
      coverLetter.trim() === (user?.coverLetter || "") &&
      firstNiche === (user?.niches?.firstNiche || "") &&
      secondNiche === (user?.niches?.secondNiche || "") &&
      thirdNiche === (user?.niches?.thirdNiche || "") &&
      !resume;

    if (noChanges) {
      toast.info("No changes detected");
      return;
    }

    const profileData = {
      name: name.trim(),
      phone: String(phone).trim(),
      address: address.trim(),
      coverLetter: coverLetter.trim(),
      firstNiche: firstNiche || "",
      secondNiche: secondNiche || "",
      thirdNiche: thirdNiche || "",
      resume,
    };

    dispatch(updateProfile(profileData));
  };

  useEffect(() => {
    if (error) {
      toast.error(error);
      dispatch(clearAllUpdateProfileErrors());
    }

    if (isUpdated) {
      toast.success(message || "Profile updated successfully");

      setResume(null);

      dispatch(getUser());
      dispatch(clearAllUpdateProfileErrors());
    }
  }, [dispatch, error, isUpdated, message]);

  const resumeHandler = (e) => {
    const file = e.target.files?.[0];

    if (!file) {
      return;
    }

    setResume(file);

    const reader = new FileReader();

    reader.onload = () => {
      setResumePreview(reader.result);
    };

    reader.readAsDataURL(file);
  };

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

  return (
    <div className="account_components">
      <h3>Update Profile</h3>

      <div>
        <label>Full Name</label>

        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </div>

      <div>
        <label>Email Address</label>

        <input
          type="email"
          value={email}
          disabled
        />
      </div>

      <div>
        <label>Phone Number</label>

        <input
          type="number"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
        />
      </div>

      <div>
        <label>Address</label>

        <input
          type="text"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
        />
      </div>

      {user && user.role === "JOB_SEEKER" && (
        <>
          <div>
            <label>My Preferred Job Niches</label>

            <div
              style={{
                display: "flex",
                flexDirection: "column",
                gap: "15px",
              }}
            >
              <select
                value={firstNiche}
                onChange={(e) => setFirstNiche(e.target.value)}
              >
                <option value="">Select first niche</option>

                {nichesArray.map((element, index) => (
                  <option value={element} key={index}>
                    {element}
                  </option>
                ))}
              </select>

              <select
                value={secondNiche}
                onChange={(e) => setSecondNiche(e.target.value)}
              >
                <option value="">Select second niche</option>

                {nichesArray.map((element, index) => (
                  <option value={element} key={index}>
                    {element}
                  </option>
                ))}
              </select>

              <select
                value={thirdNiche}
                onChange={(e) => setThirdNiche(e.target.value)}
              >
                <option value="">Select third niche</option>

                {nichesArray.map((element, index) => (
                  <option value={element} key={index}>
                    {element}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label>Coverletter</label>

            <textarea
              value={coverLetter}
              onChange={(e) => setCoverLetter(e.target.value)}
              rows={5}
            />
          </div>

          <div>
            <label>Upload Resume</label>

            <input
              type="file"
              accept=".pdf,.doc,.docx"
              onChange={resumeHandler}
            />

            {resumePreview && (
              <div>
                <p>Current Resume:</p>

                <Link
                  to={resumePreview}
                  target="_blank"
                  className="view-resume"
                >
                  View Resume
                </Link>
              </div>
            )}
          </div>
        </>
      )}

      <div className="save_change_btn_wrapper">
        <button
          className="btn"
          onClick={handleUpdateProfile}
          disabled={loading}
        >
          {loading ? "Saving..." : "Save Changes"}
        </button>
      </div>
    </div>
  );
};

export default UpdateProfile;