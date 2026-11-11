# You may need to install some packages to get this file to run
# Packages needed just for this file (on top of packages required for the base project): langchain-experimental tiktoken
# use 'uv sync' to get all packages from my pyproject.toml file 
# Then run with:
# uv run ./src/python_fast_api_langchain_app/chunking_demo.py

# fixed-size chunking:
from langchain_text_splitters import CharacterTextSplitter

text = """
LangChain is a framework for building LLM applications.
It helps connect prompts, models, memory, and retrieval.
Chunking is a crucial part of RAG pipelines.
Good chunking improves both quality and cost.
"""

splitter = CharacterTextSplitter(
    separator="\n\n",
    chunk_size=120,
    chunk_overlap=20,
    length_function=len,
)

chunks = splitter.split_text(text)
print('----Fixed-Size Chunking----')
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")


# Recursive chunking:
from langchain_text_splitters import RecursiveCharacterTextSplitter

text = """
# Project Overview
This project uses LangChain to build a RAG pipeline.

## Data Sources
The app reads from PDFs, docs, and web pages.
The retrieval component uses embeddings over chunks.

## Why chunking matters
Chunking affects retrieval quality, latency, and cost.
"""

splitter = RecursiveCharacterTextSplitter(
    chunk_size=200,
    chunk_overlap=30,
    separators=["\n\n", "\n", ". ", " ", ""],
)

chunks = splitter.split_text(text)
print('----Recursive Chunking----')
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")

# token-based chunking
from langchain_text_splitters import TokenTextSplitter

text = """Large language models are trained on massive amounts of text.
Chunking is essential for retrieval systems because retrieval is more precise when you work with smaller, related units.
This matters in RAG pipelines and agent workflows.
"""

splitter = TokenTextSplitter(
    chunk_size=40,
    chunk_overlap=10,
)

chunks = splitter.split_text(text)
print('----Token-Based Chunking----')
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")

# markdown-aware chunking:
from langchain_text_splitters import MarkdownHeaderTextSplitter

markdown_text = """
# Introduction
This section explains the project goals.

## Data Pipeline
The system loads files from storage and indexes them.

### Embeddings
Embeddings convert text into numerical vectors.

## Retrieval
RAG retrieves only the relevant passages.
"""

headers_to_split_on = [
    ("#", "Header 1"),
    ("##", "Header 2"),
    ("###", "Header 3"),
]

splitter = MarkdownHeaderTextSplitter(headers_to_split_on=headers_to_split_on)
chunks = splitter.split_text(markdown_text)
print('----Markdown-Aware Chunking----')
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")

# semantic chunking
from langchain_experimental.text_splitter import SemanticChunker
from langchain_ollama import OllamaEmbeddings

text = """
Language models can summarize text.
They can also answer questions.
However, chunking still matters in RAG systems because retrieval is only as good as your segment sizes.
When you split documents well, the retriever finds the right evidence.
"""

embeddings = OllamaEmbeddings(
    model='qwen3-embedding:0.6b',
    base_url='http://localhost:11434',
)
splitter = SemanticChunker(embeddings, breakpoint_threshold_type="percentile")
chunks = splitter.split_text(text)
print('----Semantic Chunking----')
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")
