import { useState } from "react";
import ChatHeader from "./ChatHeader";
import ChatBody from "./ChatBody";
import ChatInput from "./ChatInput";
import "./chatbot.css";

const ChatBot = () => {
  const [isOpen, setIsOpen] = useState(false);

  const [messages, setMessages] = useState([
    {
      sender: "bot",
      text: "👋 Welcome! I'm your AI Job Assistant.",
    },
    {
      sender: "bot",
      text: "Ask me anything about jobs, companies, salaries or applications.",
    },
  ]);

  const [typing, setTyping] = useState(false);

  const sendMessage = async (text) => {
    if (!text.trim()) return;

    // Add user message
    setMessages((prev) => [
      ...prev,
      {
        sender: "user",
        text,
      },
    ]);

    setTyping(true);

    // Dummy bot response
    try {
      const response = await fetch("http://localhost:5000/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          message: text,
        }),
      });

      const data = await response.json();

      setMessages((prev) => [
        ...prev,
        {
          sender: "bot",
          text: data.reply,
        },
      ]);
    } catch (err) {
      console.error(err);
    } finally {
      setTyping(false);
    }
  };

  return (
    <>
      <button className="chatbot-button" onClick={() => setIsOpen(!isOpen)}>
        🤖
      </button>

      {isOpen && (
        <div className="chatbot-window">
          <ChatHeader closeChat={() => setIsOpen(false)} />

          <ChatBody
            messages={messages}
            typing={typing}
            sendMessage={sendMessage}
          />

          <ChatInput sendMessage={sendMessage} />
        </div>
      )}
    </>
  );
};

export default ChatBot;
