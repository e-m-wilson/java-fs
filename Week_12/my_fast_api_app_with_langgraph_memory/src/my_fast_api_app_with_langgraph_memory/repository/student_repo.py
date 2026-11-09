from sqlalchemy import select
from sqlalchemy.orm import Session

from my_fast_api_app_with_langgraph_memory.domain.students import Student


class StudentRepo:
    def __init__(self, db: Session) -> None:
        self.db = db

    def get_all(self) -> list[Student]:
        return list(self.db.scalars(select(Student)).all())

    def get_by_id(self, student_id: int) -> Student | None:
        return self.db.get(Student, student_id)

    def create(self, name: str) -> Student:
        student = Student(name=name)
        self.db.add(student)
        self.db.commit()
        self.db.refresh(student)
        return student

    def update(self, student_id: int, name: str) -> Student | None:
        student = self.get_by_id(student_id)
        if student is None:
            return None

        student.name = name
        self.db.commit()
        self.db.refresh(student)
        return student

    def delete(self, student_id: int) -> bool:
        student = self.get_by_id(student_id)
        if student is None:
            return False

        self.db.delete(student)
        self.db.commit()
        return True