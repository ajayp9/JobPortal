from services.job_service import JobService
from services.ai_service import ask_ai

class ResponseService:

    @staticmethod
    def get_response(message):

        jobs = JobService.get_all_jobs()

        answer = ask_ai(
            message,
            jobs
        )

        return {
            "reply": answer
        }