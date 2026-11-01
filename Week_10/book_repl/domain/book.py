from dataclasses import dataclass, field
from typing import Optional
import uuid 

@dataclass
class Book:
    title: str 
    author: Optional[str] = None
    book_id: str = field(default_factory=lambda: str(uuid.uuid4()))

    def to_dict(self) -> dict:
        return {
            "book_id":self.book_id,
            "title":self.title,
            "author":self.author
        }

    @classmethod
    def from_dict(cls, data: dict) -> "Book":
        return cls(**data)