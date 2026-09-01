const ChatHeader = ({ closeChat }) => {
  return (
    <div className="chat-header">

      <div>
        <h3>🤖 Job Assistant</h3>
        <span className="online-status">
          ● Online
        </span>
      </div>

      <button
        className="close-btn"
        onClick={closeChat}
      >
        ✖
      </button>

    </div>
  );
};

export default ChatHeader;