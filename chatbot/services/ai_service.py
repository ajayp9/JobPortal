import os

from groq import Groq
from dotenv import load_dotenv

load_dotenv()

client = Groq(
    api_key=os.getenv("GROQ_API_KEY")
)


def ask_ai(question, jobs):

    prompt = f"""
You are an AI assistant for a Job Portal.

You have access to the following job data.

{jobs}

Rules:

1. If the question is about jobs, answer ONLY using the job data.

2. If the question is about resume, interview, career guidance or placement,
answer using your own knowledge.

3. If no matching job exists, reply politely that no matching jobs were found.

User Question:

{question}
"""

    completion = client.chat.completions.create(

        model="llama-3.3-70b-versatile",

        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ]

    )

    return completion.choices[0].message.content