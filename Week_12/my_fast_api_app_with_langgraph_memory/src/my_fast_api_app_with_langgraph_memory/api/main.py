from contextlib import ExitStack, asynccontextmanager

from fastapi import FastAPI
from langchain_ollama import ChatOllama, OllamaEmbeddings
from langchain_chroma import Chroma
from sqlalchemy.engine import make_url
from langgraph.checkpoint.postgres import PostgresSaver
from langgraph.checkpoint.serde.jsonplus import JsonPlusSerializer
from langgraph.store.postgres import PostgresStore

from my_fast_api_app_with_langgraph_memory.api.routers import student_router
from my_fast_api_app_with_langgraph_memory.api.routers import documents
from my_fast_api_app_with_langgraph_memory.service.memory import build_memory_graph
from my_fast_api_app_with_langgraph_memory.settings import settings


def _langgraph_database_url(database_url: str) -> str:
    # LangGraph's psycopg drivers need the native PostgreSQL URL, not SQLAlchemy's
    # postgresql+psycopg:// dialect form.
    url = make_url(database_url)
    if url.drivername.startswith("postgresql+"):
        url = url.set(drivername="postgresql")
    return url.render_as_string(hide_password=False)


@asynccontextmanager
async def lifespan(app: FastAPI):
    #application startup 
    app.state.llm = ChatOllama(
        model=settings.OLLAMA_MODEL,
        base_url=settings.OLLAMA_BASE_URL,
        temperature=0,
        keep_alive='30m'
    )

    # can't accept a string for keep_alive
    # it instead takes an integer value representing seconds
    app.state.embeddings = OllamaEmbeddings(
        model=settings.OLLAMA_EMBEDDING_MODEL,
        base_url=settings.OLLAMA_BASE_URL,
        keep_alive=1800
    )

    app.state.vector_store = Chroma(
        collection_name="documents",
        embedding_function=app.state.embeddings,
        persist_directory=settings.CHROMA_PERSIST_DIRECTORY
    )

    database_url = _langgraph_database_url(settings.DATABASE_URL)
    with ExitStack() as resources:
        # LangGraph creates and migrates its own checkpoint and key/value tables.
        checkpoint_connection = resources.enter_context(
            PostgresSaver.from_conn_string(database_url)
        )
        checkpoint_connection.serde = JsonPlusSerializer(
            allowed_msgpack_modules=[
                ("langchain_core.messages.human", "HumanMessage"),
                ("langchain_core.messages.ai", "AIMessage"),
            ]
        )
        checkpoint_connection.setup()

        memory_store = resources.enter_context(
            PostgresStore.from_conn_string(database_url)
        )
        memory_store.setup()

        app.state.memory_store = memory_store
        app.state.memory_graph = build_memory_graph(
            app.state.llm,
            checkpoint_connection,
            memory_store,
        )
        yield

    # application shuts down
    app.state.llm = None
    app.state.embeddings = None
    app.state.vector_store = None
    app.state.memory_graph = None
    app.state.memory_store = None

app = FastAPI(lifespan=lifespan)

app.include_router(student_router.router)
app.include_router(documents.router)