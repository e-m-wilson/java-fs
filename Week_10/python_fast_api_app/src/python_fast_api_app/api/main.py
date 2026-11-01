from fastapi import Depends, FastAPI, HTTPException, status
from sqlalchemy.orm import Session

from python_fast_api_app.api.schemas import StudentCreate, StudentRead
from python_fast_api_app.db.deps import get_db
from python_fast_api_app.domain.student import Student

from python_fast_api_app.repositories.sqlalchemy_student_repo import SqlAlchemyStudentRepository
from python_fast_api_app.repositories.student_repo import StudentRepositoryProtocol

from python_fast_api_app.services.student_service import StudentService

app = FastAPI()

def get_student_repository(
    session: Session = Depends(get_db),
) -> StudentRepositoryProtocol:
    return SqlAlchemyStudentRepository(session)


def get_student_service(
    repo: StudentRepositoryProtocol = Depends(get_student_repository),
) -> StudentService:
    return StudentService(repo)

@app.post(
    "/students",
    response_model=StudentRead,
    status_code=status.HTTP_201_CREATED,
)
def add_student(
    student: StudentCreate,
    service: StudentService = Depends(get_student_service),
) -> Student:
    return service.add_student(student.name)


@app.get("/students/{student_id}", response_model=StudentRead)
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
