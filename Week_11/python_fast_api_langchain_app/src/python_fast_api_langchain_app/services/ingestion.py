
import os
import uuid

from langchain_community.document_loaders import (
    PyPDFLoader,
    TextLoader,
    Docx2txtLoader,
)
from langchain_chroma import Chroma
from langchain_text_splitters import RecursiveCharacterTextSplitter


class DocumentValidationError(ValueError):
    pass


def ingest_document(file_path: str, filename: str, vector_store: Chroma):
    extension = os.path.splitext(filename)[1].lower()

    if extension == ".pdf":
        loader = PyPDFLoader(file_path)
    elif extension == ".txt":
        loader = TextLoader(file_path, encoding="utf-8")
    elif extension == ".docx":
        loader = Docx2txtLoader(file_path)
    else:
        raise DocumentValidationError("Unsupported file type")

    documents = loader.load()

    # chunk_size == sets the approximate amount of characters in a chunk (not exact!)
    # chunk_overlap == some important information could be cut-off in between chunks, so setting overlap lets
    #       langchain repeat this amount of characters from the end of one chunk onto another
    # add_start_index=True == tells langchain to include the starting character position of each chunk in its metadata
    # it can help with: tracking the original location of retrieved information, debugging chunking behavior, and building 
    # document citation or source-reference functionality 
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=1000,
        chunk_overlap=200,
        add_start_index=True,
    )
    chunks = splitter.split_documents(documents)

    if not chunks:
        raise DocumentValidationError("The uploaded file contains no extractable text.")

    document_id = str(uuid.uuid4())
    for index, chunk in enumerate(chunks):
        chunk.metadata.update({
            "document_id": document_id,
            "filename": filename,
            "chunk_index": index,
        })

    vector_store.add_documents(chunks)

    return {
        "document_id": document_id,
        "filename": filename,
        "chunks_created": len(chunks)
    }