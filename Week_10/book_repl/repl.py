import pretty_tables

from domain.book import Book
from repository.book_repo import Book_Repo


class BookRepl:
    def __init__(self, book_repo):
        self.running = True
        self.book_repo = book_repo

    def start(self):
        print("Welcome to my book manager REPL! Type 'help' for commands.")
        while self.running:
            cmd = input(">>> ").strip()
            self.handle_command(cmd)

    def handle_command(self, cmd):
        if cmd == "exit":
            self.running = False
            print("Goodbye")
        elif cmd == "addBook":
            self.add_book()
        elif cmd == "getAllBooks":
            self.get_all_books()
        elif cmd == "help":
            print("Available commands: exit, addBook, getAllBooks")
        else: 
            print(f"Unknown Command: {cmd}")

    
    def add_book(self):
        try:
            print("Enter book details.")
            title = input("Title: ")
            author = input("Author: ")
            book = Book(title=title, author=author)
            book_id = self.book_repo.add_book(book)
            print(book_id)
        except Exception as e:
            print(f"An error occurred:{e}")

    def get_all_books(self):
        try:
            books = self.book_repo.get_all_books()

            headers = ['ID', 'Author', 'Title']
            rows = []

            for b in books:
                rows.append([b.book_id, b.author, b.title])

            table = pretty_tables.create(
                headers=headers,
                rows=rows,
                empty_cell_placeholder='No data',  # Optional: override the default `None` with a custom string
            )

            print(table)
        except FileNotFoundError:
            print("Error: file not found.")
        except Exception as e:
            print(f"An error occurred: {e}")

if __name__ == "__main__":
    repo = Book_Repo()
    repl = BookRepl(repo)
    repl.start()