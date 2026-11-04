from sqlalchemy.orm import Session
from python_fast_api_langchain_app.db.database import SessionLocal

def get_db() -> Session:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
