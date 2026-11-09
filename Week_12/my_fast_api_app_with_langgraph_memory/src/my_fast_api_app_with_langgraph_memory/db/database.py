from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session

from my_fast_api_app_with_langgraph_memory.settings import settings

engine = create_engine(
    settings.DATABASE_URL,
    echo=settings.DEBUG,
)

SessionLocal = sessionmaker(
    bind=engine,
    autoflush=False,
    autocommit=False
)

def get_db() -> Session:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()