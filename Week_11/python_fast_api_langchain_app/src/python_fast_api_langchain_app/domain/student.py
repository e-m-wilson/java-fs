from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column

from python_fast_api_langchain_app.base import Base

class Student(Base):
    __tablename__ = "students"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(100), nullable=False)
