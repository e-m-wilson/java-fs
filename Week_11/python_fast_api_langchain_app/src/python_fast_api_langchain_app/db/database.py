from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from python_fast_api_langchain_app.settings import settings

engine = create_engine(
    settings.DATABASE_URL,
    echo=settings.DEBUG,
)

SessionLocal = sessionmaker(
    bind=engine,
    autoflush=False,
    autocommit=False,
)
