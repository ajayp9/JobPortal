import { useState } from "react";

const ChatInput = ({ sendMessage }) => {

  const [text, setText] = useState("");

  const handleSend = () => {

    if (!text.trim()) return;

    sendMessage(text);

    setText("");

  };

  return (

    <div className="chat-input">

      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Type your message..."
        onKeyDown={(e) => {

          if (e.key === "Enter") {

            handleSend();

          }

        }}
      />

      <button onClick={handleSend}>
        ➤
      </button>

    </div>

  );

};

export default ChatInput;