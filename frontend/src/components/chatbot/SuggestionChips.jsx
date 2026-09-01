const suggestions = [
  // "Java Jobs",
  "Full-time",
  "Part-time",
  "Highest Salary",
  "Resume Tips"
];

const SuggestionChips = ({ sendMessage }) => {

  return (

    <div className="suggestion-container">

      {suggestions.map((item) => (

        <button
          key={item}
          className="suggestion-chip"
          onClick={() => sendMessage(item)}
        >
          {item}
        </button>

      ))}

    </div>

  );

};

export default SuggestionChips;