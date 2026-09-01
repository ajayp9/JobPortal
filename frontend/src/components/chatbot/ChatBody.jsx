import { useEffect, useRef } from "react";
import Message from "./Message";
import SuggestionChips from "./SuggestionChips";
import TypingIndicator from "./TypingIndicator";

const ChatBody = ({ messages, typing, sendMessage }) => {

  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages, typing]);

  return (
    <div className="chat-body">

      {messages.map((msg, index) => (
        <Message
          key={index}
          sender={msg.sender}
          text={msg.text}
        />
      ))}

      {typing && <TypingIndicator />}

      <SuggestionChips sendMessage={sendMessage} />

      <div ref={bottomRef}></div>

    </div>
  );
};

export default ChatBody;