from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column

from my_fast_api_app_with_langgraph_memory.domain.base import Base 

class Student(Base):
    __tablename__ = "students"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(100), nullable=False)