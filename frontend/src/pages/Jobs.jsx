import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { toast } from "react-toastify";
import { clearAllJobErrors, fetchJobs } from "../store/slices/jobSlice";
import Spinner from "../components/Spinner";
import { FaSearch } from "react-icons/fa";
import { Link } from "react-router-dom";

const Jobs = () => {
  const [city, setCity] = useState("");
  const [selectedCity, setSelectedCity] = useState("");
  const [niche, setNiche] = useState("");
  const [selectedNiche, setSelectedNiche] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");

  const { jobs, loading, error } = useSelector((state) => state.jobs);

  const dispatch = useDispatch();

  const handleCityChange = (selectedValue) => {
    setCity(selectedValue);
    setSelectedCity(selectedValue);
  };

  const handleNicheChange = (selectedValue) => {
    setNiche(selectedValue);
    setSelectedNiche(selectedValue);
  };

  useEffect(() => {
    dispatch(fetchJobs(city, niche, searchKeyword));
  }, [dispatch, city, niche]);

  useEffect(() => {
    if (error) {
      toast.error(error);
      dispatch(clearAllJobErrors());
    }
  }, [dispatch, error]);

  const handleSearch = () => {
    dispatch(fetchJobs(city, niche, searchKeyword));
  };

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
    <>
      {loading ? (
        <Spinner />
      ) : (
        <section className="jobs">
          <div className="search-tab-wrapper">
            <input
              type="text"
              value={searchKeyword}
              placeholder="Search jobs"
              onChange={(e) => setSearchKeyword(e.target.value)}
            />

            <button type="button" onClick={handleSearch}>
              Find Job
            </button>

            <FaSearch />
          </div>

          <div className="wrapper">
            <div className="filter-bar">
              <div className="cities">
                <h2>Filter Job By City</h2>

                {cities.map((cityName) => (
                  <div key={cityName}>
                    <input
                      type="radio"
                      id={cityName}
                      name="city"
                      value={cityName}
                      checked={selectedCity === cityName}
                      onChange={() => handleCityChange(cityName)}
                    />

                    <label htmlFor={cityName}>{cityName}</label>
                  </div>
                ))}
              </div>

              <div className="cities">
                <h2>Filter Job By Niche</h2>

                {nichesArray.map((nicheName) => (
                  <div key={nicheName}>
                    <input
                      type="radio"
                      id={nicheName}
                      name="niche"
                      value={nicheName}
                      checked={selectedNiche === nicheName}
                      onChange={() => handleNicheChange(nicheName)}
                    />

                    <label htmlFor={nicheName}>{nicheName}</label>
                  </div>
                ))}
              </div>
            </div>

            <div className="container">
              <div className="mobile-filter">
                <select
                  value={city}
                  onChange={(e) => {
                    setCity(e.target.value);
                    setSelectedCity(e.target.value);
                  }}
                >
                  <option value="">Filter By City</option>

                  {cities.map((cityName) => (
                    <option value={cityName} key={cityName}>
                      {cityName}
                    </option>
                  ))}
                </select>

                <select
                  value={niche}
                  onChange={(e) => {
                    setNiche(e.target.value);
                    setSelectedNiche(e.target.value);
                  }}
                >
                  <option value="">Filter By Niche</option>

                  {nichesArray.map((nicheName) => (
                    <option value={nicheName} key={nicheName}>
                      {nicheName}
                    </option>
                  ))}
                </select>
              </div>

              <div className="jobs_container">
                {jobs &&
                  jobs.map((element) => (
                    <div className="card" key={element.id}>
                      {element.hiringMultipleCandidates === "YES" ? (
                        <p className="hiring-multiple">
                          Hiring Multiple Candidates
                        </p>
                      ) : (
                        <p className="hiring">Hiring</p>
                      )}

                      <p className="title">{element.title}</p>

                      <p className="company">{element.companyName}</p>

                      <p className="location">{element.location}</p>

                      <p className="salary">
                        <span>Salary:</span> Rs. {element.salary}
                      </p>

                      {element.jobPostedOn && (
                        <p className="posted">
                          <span>Posted On:</span>{" "}
                          {element.jobPostedOn.substring(0, 10)}
                        </p>
                      )}

                      <div className="btn-wrapper">
                        <Link
                          className="btn"
                          to={`/post/application/${element.id}`}
                        >
                          Apply Now
                        </Link>
                      </div>
                    </div>
                  ))}
              </div>
            </div>
          </div>
        </section>
      )}
    </>
  );
};

export default Jobs;