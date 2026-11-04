from python_fast_api_app.domain.student import Student
from python_fast_api_app.repositories.student_repo import StudentRepositoryProtocol

class StudentService:
    def __init__(self, repo: StudentRepositoryProtocol):
        self.repo = repo

    def add_student(self, name: str) -> Student:
        return self.repo.add_student(Student(name=name))

    def get_student(self, student_id: int) -> Student | None:
        return self.repo.get_by_id(student_id)

    def get_students_by_name(self, name: str) -> list[Student]:
        return self.repo.get_by_name(name)
