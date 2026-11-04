from typing import Protocol
from python_fast_api_langchain_app.domain.student import Student

class StudentRepositoryProtocol(Protocol):
    def get_by_id(self, student_id: int) -> Student | None:
        ...

    def get_by_name(self, name: str) -> list[Student]:
        ...

    def get_all(self) -> list[Student]:
        ...

    def add_student(self, student: Student) -> Student:
        ...
