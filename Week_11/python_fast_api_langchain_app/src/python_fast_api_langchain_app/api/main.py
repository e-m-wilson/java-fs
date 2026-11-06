from contextlib import asynccontextmanager

from fastapi import FastAPI
from langchain_ollama import ChatOllama, OllamaEmbeddings

from python_fast_api_langchain_app.api.routers.students import router as students_router
from python_fast_api_langchain_app.api.routers.documents import router as documents_router
from langchain_chroma import Chroma
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
    # embeddings takes an integer in seconds, strings won't work 
    app.state.embeddings = OllamaEmbeddings(
        model=settings.OLLAMA_EMBEDDING_MODEL,
        base_url=settings.OLLAMA_BASE_URL,
        keep_alive=1800,
    )
    app.state.vector_store = Chroma(
        collection_name="documents",
        embedding_function=app.state.embeddings,
        persist_directory=settings.CHROMA_PERSIST_DIRECTORY,
    )

    yield

    # Application shutdown
    app.state.llm = None
    app.state.embeddings = None
    app.state.vector_store = None

app = FastAPI(lifespan=lifespan)
app.include_router(students_router)
app.include_router(documents_router)
