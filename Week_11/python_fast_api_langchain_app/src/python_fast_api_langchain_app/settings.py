from pydantic import BaseModel
from dotenv import load_dotenv
import os

load_dotenv()


class Settings(BaseModel):
    DATABASE_URL: str
    ENV: str = "development"
    DEBUG: bool = False
    OLLAMA_MODEL: str = "lfm3"
    OLLAMA_BASE_URL: str = "http://localhost:11434"


settings = Settings(
    DATABASE_URL=os.getenv("DATABASE_URL"),
    OLLAMA_MODEL=os.getenv("OLLAMA_MODEL", "lfm3"),
    OLLAMA_BASE_URL=os.getenv("OLLAMA_BASE_URL", "http://localhost:11434"),
)
