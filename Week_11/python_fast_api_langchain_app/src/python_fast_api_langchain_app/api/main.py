from contextlib import asynccontextmanager

from fastapi import FastAPI
from langchain_ollama import ChatOllama

from python_fast_api_langchain_app.api.routers.students import router as students_router
from python_fast_api_langchain_app.settings import settings

# can also set keep_alive to '-1' to keep it alive indefinitely 
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Application startup
    app.state.llm = ChatOllama(
        model=settings.OLLAMA_MODEL,
        base_url=settings.OLLAMA_BASE_URL,
        temperature=0,
        keep_alive="30m",
    )

    yield

    # Application shutdown
    app.state.llm = None

app = FastAPI(lifespan=lifespan)
app.include_router(students_router)
