from typing import Protocol
from domain.book import Book

class BookRepositoryProtocol(Protocol):

    def add_book(self, book: Book) -> str:
        ...

    def get_all_books(self) -> list[Book]:
        ...