

from fastapi import APIRouter, Depends, HTTPException, Response, status, Request
from langchain.chat_models import BaseChatModel
from pydantic import BaseModel, ConfigDict, Field
from sqlalchemy.orm import Session

from my_fast_api_app_with_langgraph_memory.db.database import get_db
from my_fast_api_app_with_langgraph_memory.domain.students import Student
from my_fast_api_app_with_langgraph_memory.repository.student_repo import StudentRepo
from my_fast_api_app_with_langgraph_memory.repository.student_repo_protocol import StudentRepoProtocol
from my_fast_api_app_with_langgraph_memory.service.llm_service_v2 import LlmService
from my_fast_api_app_with_langgraph_memory.service.student_service import StudentService

router = APIRouter(prefix="/students", tags=["students"])


class StudentCreate(BaseModel):
    name: str = Field(min_length=1, max_length=100)


class StudentUpdate(BaseModel):
    name: str = Field(min_length=1, max_length=100)

class StudentRead(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    name: str

def get_student_repo(db: Session = Depends(get_db)) -> StudentRepoProtocol:
    return StudentRepo(db)

def get_student_service(repo: StudentRepoProtocol = Depends(get_student_repo)) -> StudentService:
    return StudentService(repo)

def get_llm_model(request: Request) -> BaseChatModel:
    return request.app.state.llm

def get_llm_service(
        student_repo: StudentRepoProtocol = Depends(get_student_repo),
        llm: BaseChatModel = Depends(get_llm_model),
) -> LlmService:
    return LlmService(student_repo, llm)

@router.post(
    "",
    response_model=StudentRead,
    status_code=status.HTTP_201_CREATED,
)
def create_student(
    student_data: StudentCreate,
    service: StudentService = Depends(get_student_service),
) -> Student:
    return service.create_student(student_data.name)


@router.get("", response_model=list[StudentRead])
def get_students(
    service: StudentService = Depends(get_student_service),
) -> list[Student]:
    return service.get_all_students()


@router.get("/{student_id}", response_model=StudentRead)
def get_student(
    student_id: int,
    service: StudentService = Depends(get_student_service),
) -> Student:
    student = service.get_student(student_id)
    if student is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Student not found",
        )
    return student


@router.put("/{student_id}", response_model=StudentRead)
def update_student(
    student_id: int,
    student_data: StudentUpdate,
    service: StudentService = Depends(get_student_service),
) -> Student:
    student = service.update_student(student_id, student_data.name)
    if student is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Student not found",
        )
    return student


@router.delete("/{student_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_student(
    student_id: int,
    service: StudentService = Depends(get_student_service),
) -> Response:
    if not service.delete_student(student_id):
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Student not found",
        )
    return Response(status_code=status.HTTP_204_NO_CONTENT)


class PromptRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    prompt: str = Field(min_length=1, max_length=4000)


class PromptResponse(BaseModel):
    response: str


@router.post("/ask", response_model=PromptResponse)
def answer_student_question(
    request: PromptRequest,
    llm_svc: LlmService = Depends(get_llm_service),
) -> PromptResponse:
    return PromptResponse(response=llm_svc.ask(request.prompt))