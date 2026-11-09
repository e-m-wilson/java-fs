import logging
import os 
import shutil
import tempfile
from uuid import UUID, uuid4

from langchain_chroma import Chroma
from fastapi import APIRouter, Depends, File, HTTPException, Path, Request, UploadFile
from langgraph.graph.state import CompiledStateGraph
from pydantic import BaseModel, ConfigDict, Field
from langgraph.store.base import BaseStore

from my_fast_api_app_with_langgraph_memory.service.ingestion import (
    DocumentValidationError,
    ingest_document
)

from my_fast_api_app_with_langgraph_memory.service.retrieval import answer_from_documents

router = APIRouter(prefix="/documents", tags=["Documents"])
logger = logging.getLogger(__name__)


def get_vector_store(request: Request) -> Chroma:
    return request.app.state.vector_store

def get_memory_graph(request: Request) -> CompiledStateGraph:
    return request.app.state.memory_graph


def get_memory_store(request: Request) -> BaseStore:
    return request.app.state.memory_store


class QuestionRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    question: str = Field(min_length=5, max_length=4000)
    conversation_id: UUID | None = None
    user_id: str | None = Field(default=None, min_length=1, max_length=128)
    remember: bool = True


class MemoryRequest(BaseModel):
    summary: str = Field(min_length=1, max_length=1000)


@router.post("/chat")
def ask_documents(
    request: QuestionRequest,
    vector_store: Chroma = Depends(get_vector_store),
    memory_graph=Depends(get_memory_graph),
):
    conversation_id = str(request.conversation_id or uuid4())
    return answer_from_documents(
        request.question,
        vector_store,
        memory_graph,
        conversation_id,
        request.user_id,
        request.remember,
    )


@router.get("/memory/{user_id}")
def get_user_memory(
    user_id: str = Path(min_length=1, max_length=128),
    memory_store: BaseStore = Depends(get_memory_store),
):
    item = memory_store.get(("users", user_id), "profile")
    return {"user_id": user_id, "summary": item.value.get("summary") if item else None}


@router.put("/memory/{user_id}")
def replace_user_memory(
    request: MemoryRequest,
    user_id: str = Path(min_length=1, max_length=128),
    memory_store: BaseStore = Depends(get_memory_store),
):
    memory_store.put(("users", user_id), "profile", {"summary": request.summary})
    return {"user_id": user_id, "summary": request.summary}


@router.delete("/memory/{user_id}")
def delete_user_memory(
    user_id: str = Path(min_length=1, max_length=128),
    memory_store: BaseStore = Depends(get_memory_store),
):
    memory_store.delete(("users", user_id), "profile")
    return {"user_id": user_id, "deleted": True}

@router.post("/upload")
def upload_document(
    file: UploadFile = File(...),
    vector_store: Chroma = Depends(get_vector_store)
):
    # step 1: create a temporary file in our root project
    filename = os.path.basename(file.filename or "")
    extension = os.path.splitext(filename)[1].lower()

    if not filename or extension not in {".pdf", ".txt", ".docx"}:
        raise HTTPException(
            status_code=400,
            detail="Upload a .txt, .pdf, or .docx file."
        )

    temp_path = None

    # NamedTemporaryFile actually creates a temp file and opens it
    # it may look like tmp8x92k22a.pdf
    try: 
        with tempfile.NamedTemporaryFile(
            # nromally the temp file would be deleted after this code block is done
            # we keep it for now because we need it in ingest_document()
            # we delete it later in our 'finally' code block 
            delete=False,
            suffix=extension
        ) as temp_file:
            temp_path = temp_file.name
            shutil.copyfileobj(file.file, temp_file)

    # step 2: vectorize and ingest that temporary vectorized representation of that file

        result = ingest_document(
            temp_path,
            filename,
            vector_store,
        )

        return {
            "status": "success",
            **result
        }

    except DocumentValidationError as error:
        raise HTTPException(status_code=400, detail=str(error)) from error
    except Exception as error:
        logger.exception("Failed to ingest uploaded document %s", filename)
        raise HTTPException(
            status_code=500,
            detail="Unable to process the uploaded document.",
        ) from error

    # step 3: delete the temporary file
    # The original file IS NOT SAVED ANYWHERE
    finally:
        if temp_path and os.path.exists(temp_path):
            try:
                os.remove(temp_path)
            except OSError:
                logger.exception("Failed to remove temporary upload %s", temp_path)

    
