import json
from pathlib import Path
from domain.book import Book
from repository.book_repo_protocol import BookRepositoryProtocol


class Book_Repo(BookRepositoryProtocol):

    def get_all_books(self) -> list[Book]:
        file_path = Path("books.json")

        # x = exclusive create; throws FileExistsError and avoids race condition if 
        # another process tries to create the file as this one runs 
        # json modules lets us write python dictionaries to json and vice versa 
        if not file_path.exists():
            try:
                with open("books.json", "x") as file:
                    json.dump({}, file, indent=4)
            except FileExistsError:
                print("File exists.")

        with open("books.json", "r", encoding="utf-8") as f:
            data = json.load(f)
            return [Book.from_dict(item) for item in data]

    def add_book(self, book: Book) -> str:
        books = self.get_all_books()
        books.append(book)
        with open("books.json", "w", encoding="utf-8") as f:
            json.dump([b.to_dict() for b in books], f, indent=4)
        return book.book_id