from sqlalchemy.orm import Session
from python_fast_api_app.domain.student import Student
from python_fast_api_app.repositories.student_repo import StudentRepositoryProtocol


class SqlAlchemyStudentRepository(StudentRepositoryProtocol):
    def __init__(self, session: Session):
        self.session = session

    def get_by_id(self, student_id: int) -> Student | None:
        return self.session.get(Student, student_id)

    def add_student(self, student: Student) -> Student:
        self.session.add(student)
        self.session.commit()
        self.session.refresh(student)
        return student
