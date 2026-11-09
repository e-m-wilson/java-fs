from my_fast_api_app_with_langgraph_memory.domain.students import Student
from my_fast_api_app_with_langgraph_memory.repository.student_repo_protocol import StudentRepoProtocol


class StudentService:
    def __init__(self, repository: StudentRepoProtocol) -> None:
        self.repository = repository

    def get_all_students(self) -> list[Student]:
        return self.repository.get_all()

    def get_student(self, student_id: int) -> Student | None:
        return self.repository.get_by_id(student_id)

    def create_student(self, name: str) -> Student:
        return self.repository.create(name)

    def update_student(self, student_id: int, name: str) -> Student | None:
        return self.repository.update(student_id, name)

    def delete_student(self, student_id: int) -> bool:
        return self.repository.delete(student_id)