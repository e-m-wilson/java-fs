from pydantic import BaseModel
from dotenv import load_dotenv
import os

load_dotenv()


class Settings(BaseModel):
    DATABASE_URL: str
    ENV: str = "development"
    DEBUG: bool = False


settings = Settings(
    DATABASE_URL=os.getenv("DATABASE_URL")
)
