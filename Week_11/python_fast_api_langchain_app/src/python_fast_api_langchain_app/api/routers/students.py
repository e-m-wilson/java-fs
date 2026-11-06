from fastapi import APIRouter, Depends, HTTPException, Query, status, Request
from sqlalchemy.orm import Session

from python_fast_api_langchain_app.api.schemas import StudentCreate, StudentRead, PromptRequest, PromptResponse
from python_fast_api_langchain_app.db.deps import get_db
from python_fast_api_langchain_app.domain.student import Student
from python_fast_api_langchain_app.repositories.sqlalchemy_student_repo import SqlAlchemyStudentRepository
from python_fast_api_langchain_app.repositories.student_repo import StudentRepositoryProtocol
from python_fast_api_langchain_app.services.llm_service_v2 import LlmService
from python_fast_api_langchain_app.services.student_service import StudentService

from langchain_core.language_models import BaseChatModel


router = APIRouter(prefix="/students", tags=["students"])

def get_student_repository(
    session: Session = Depends(get_db),
) -> StudentRepositoryProtocol:
    return SqlAlchemyStudentRepository(session)

def get_student_service(
    repo: StudentRepositoryProtocol = Depends(get_student_repository),
) -> StudentService:
    return StudentService(repo)

def get_llm_model(request: Request) -> BaseChatModel:
    return request.app.state.llm

def get_llm_service(
        student_repo: StudentRepositoryProtocol = Depends(get_student_repository),
        llm: BaseChatModel = Depends(get_llm_model),
) -> LlmService:
    return LlmService(student_repo, llm)

@router.post(
    "",
    response_model=StudentRead,
    status_code=status.HTTP_201_CREATED,
)
def add_student(
    student: StudentCreate,
    student_svc: StudentService = Depends(get_student_service),
) -> Student:
    return student_svc.add_student(student.name)


@router.get("/{student_id}", response_model=StudentRead)
def get_student(
    student_id: int,
    student_svc: StudentService = Depends(get_student_service),
) -> Student:
    student = student_svc.get_student(student_id)
    if student is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Student not found",
        )
    return student


@router.get("", response_model=list[StudentRead])
def get_students_by_name(
    name: str = Query(min_length=1, max_length=100),
    student_svc: StudentService = Depends(get_student_service),
) -> list[Student]:
    return student_svc.get_students_by_name(name)

@router.post("/ask", response_model=PromptResponse)
def answer_student_question(
    request: PromptRequest,
    llm_svc: LlmService = Depends(get_llm_service),
) -> PromptResponse:
    return PromptResponse(response=llm_svc.ask(request.prompt))
