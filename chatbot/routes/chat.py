from fastapi import APIRouter
from models.chat_model import ChatRequest
from services.response_service import ResponseService

router = APIRouter()

@router.post("/chat")
def chat(request: ChatRequest):

    return ResponseService.get_response(
        request.message
    )