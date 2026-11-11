# LangChain Chunking Strategies

Chunking is the process of splitting a large document into smaller pieces before embedding, and sending it to a Vector DB. In a Retrieval-Augmented Generation (RAG) system, chunking is one of the most important design choices because it directly affects:

- retrieval quality
- embedding quality
- context window usage
- cost
- answer accuracy

A bad chunking strategy can make a great model look weak because the right facts are never retrieved or the context is too fragmented to be useful.

---

## 1. Why chunking matters

Large documents do not fit well into a single prompt or a single embedding vector. If you embed an entire 50-page PDF as one chunk:

- the semantic meaning can get diluted
- the embedding may focus on the wrong part of the doc
- retrieval may return the whole document instead of the relevant section
- the model may miss the precise fact you need

Good chunking tries to balance:

- chunk size
- chunk overlap
- semantic boundaries
- retrieval efficiency

A useful rule of thumb:

- small chunks help retrieval precision
- larger chunks help preserve context and relationships
- overlap helps avoid splitting important ideas between chunks

---

## 2. Core ideas behind chunking

### Chunk size
This is the number of characters, tokens, or words in each chunk.

- very small chunks: faster retrieval, better precision, but more fragmented context
- medium chunks: usually a good default for RAG
- very large chunks: better context preservation, but weaker retrieval precision and more noise

### Overlap
Overlap means a few words or sentences are repeated between neighboring chunks.

Example:

- chunk 1: sentences 1-3
- chunk 2: sentences 3-5

This helps prevent information loss when a key concept is split across boundaries.

### Semantic boundaries
A chunk should ideally end at natural breaks such as:

- paragraph boundaries
- section headings
- sentence boundaries
- code/function boundaries

This is often better than arbitrary character cuts.

---

## 3. Common chunking strategies

### A. Fixed-size chunking
This is the simplest strategy: split text into chunks of a fixed number of characters or tokens.

#### Example: character-based splitting

```python
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
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")
```

#### Strengths

- easy to implement
- fast
- good baseline

#### Weaknesses

- may cut mid-sentence or mid-paragraph
- poor for structured documents
- less aware of semantics

This is a good starting point, but not usually the best final strategy for production RAG systems.

---

### B. Recursive chunking
This is the most common LangChain default. It tries to split text using a list of separators in order until it reaches the desired chunk size.

Example separators:

- `"\n\n"` (paragraphs)
- `"\n"` (lines)
- `". "` (sentences)
- `" "` (words)

#### Example

```python
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
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")
```

#### Why it works well

It respects natural boundaries before falling back to smaller separators. In practice, this usually produces cleaner chunks than pure fixed-size splitting.

#### Best for

- general documents
- notes and articles
- mixed content
- most beginner and production RAG systems

---

### C. Token-based chunking
Some teams care more about token counts than character counts because embeddings and LLM context windows are token-based.

```python
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
for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")
```

#### Why use it

- more aligned with model token limits
- useful when size constraints are strict
- good for API and embedding costs

#### Tradeoff

Token-based splitting can be more complex and less readable than character-based chunking, especially when total number of tokens changes with model tokenization.

---

### D. Markdown-aware chunking
If your documents are Markdown, you can split based on headings and structure.

```python
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

for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")
```

#### Why it matters

Markdown documents are often structured by headings. Keeping each heading block together improves retrieval for documentation and knowledge bases.

#### Best for

- technical docs
- Wikis
- internal knowledge bases
- GitHub README and docs

---

### E. Semantic chunking
Semantic chunking attempts to group text based on meaning rather than fixed boundaries.

This is especially useful when documents contain many disconnected ideas or when the content is highly narrative or technical.

```python
from langchain_experimental.text_splitter import SemanticChunker
from langchain_openai import OpenAIEmbeddings

text = """
Language models can summarize text.
They can also answer questions.
However, chunking still matters in RAG systems because retrieval is only as good as your segment sizes.
When you split documents well, the retriever finds the right evidence.
"""

embeddings = OpenAIEmbeddings()
splitter = SemanticChunker(embeddings, breakpoint_threshold_type="percentile")
chunks = splitter.split_text(text)

for i, chunk in enumerate(chunks, 1):
    print(f"Chunk {i}: {chunk}\n---")
```

#### Strengths

- better at grouping related ideas
- often improves retrieval quality for complex documents
- useful for long-form text with many themes

#### Weaknesses

- slower and more expensive
- may need additional dependencies
- more complex to tune

This is often seen as a more advanced chunking strategy for serious production systems.

---

## 4. Strategy selection guide

Use the following as a practical decision guide:

### Use fixed-size or recursive chunking when:

- you are starting a project
- documents are mostly plain text
- you want a simple baseline
- you need quick implementation

### Use markdown-aware splitting when:

- the content is primarily documentation or README files
- heading structure matters
- users search by topic/category

### Use token-based chunking when:

- embedding costs are important
- model context windows are tight
- you need strict token budgets

### Use semantic chunking when:

- your data is long and concept-heavy
- retrieval quality matters more than raw speed
- you are optimizing for best possible RAG results

---

## 5. Chunk overlap: how much is enough?

Overlap is often necessary, but too much overlap can create duplication and increase costs.

Common starting points:

- fixed-size chunking: 10%-20% overlap
- recursive splitting: 20-50 characters overlap
- token-based: 10-20 tokens overlap

Example:

```python
splitter = RecursiveCharacterTextSplitter(
    chunk_size=500,
    chunk_overlap=50,
)
```

A good default is often a moderate overlap, not excessive overlap.

---

## 6. Chunking for different document types

### PDFs and scanned docs

- first convert to text
- split into logical sections
- if OCR is noisy, clean text before chunking
- preserve page headers or section titles if useful

### Markdown docs

- chunk by headings and subheadings
- preserve metadata such as `#`, `##`, and `###`

### Source code

- split by function/class boundaries
- keep imports and docstrings with the related code block
- avoid splitting in the middle of a function body

### Legal or policy docs

- chunk by section and clause
- preserve context around definitions and exceptions
- use smaller chunks for precise retrieval

---

## 7. Good chunking patterns for RAG

A few strong patterns:

- keep each chunk around one idea or one topic
- preserve clear boundaries between chunks
- avoid mixing unrelated information
- keep metadata with each chunk
- record the source document, page, heading, and section

Example:

```python
from langchain_core.documents import Document

chunks = [
    Document(
        page_content="This section explains retrieval-augmented generation.",
        metadata={"source": "guide.pdf", "section": "RAG", "page": 12}
    ),
    Document(
        page_content="This section explains chunking and embeddings.",
        metadata={"source": "guide.pdf", "section": "Chunking", "page": 13}
    ),
]
```

This metadata makes it easier to debug and improve retrieval later.

---

## 8. Common mistakes

### Mistake 1: One giant chunk
This makes retrieval fuzzy and hurts precision.

### Mistake 2: Tiny chunks with no context
You may retrieve fragments without enough surrounding meaning.

### Mistake 3: No overlap
Key information can be lost at chunk boundaries.

### Mistake 4: Chunking without thinking about document structure
This is especially bad for markdown, legal text, and code.

### Mistake 5: Over-optimizing too early
You do not need semantic chunking on day one if recursive splitting already works well.

---

## 9. A practical workflow

Here is a solid learning-friendly workflow:

1. Start with `RecursiveCharacterTextSplitter`
2. Use medium chunk size (for example 300-800 characters or 100-250 tokens)
3. Add moderate overlap (for example 20-50 chars or 10-20 tokens)
4. Test retrieval quality on real questions
5. If results are weak, try a different boundary strategy or semantic chunker

```python
from langchain_text_splitters import RecursiveCharacterTextSplitter

text = """Place your document text here."""

splitter = RecursiveCharacterTextSplitter(
    chunk_size=500,
    chunk_overlap=50,
    separators=["\n\n", "\n", ". ", " ", ""],
)

chunks = splitter.split_text(text)
print(f"Created {len(chunks)} chunks")
```

This is a very practical baseline for many LangChain RAG projects.

---

## 10. Final takeaways

Chunking is not just a technical detail — it is a core part of building high-quality LLM applications.

The best chunking strategy depends on:

- type of document
- retrieval task
- quality vs. speed tradeoff
- cost constraints
- structure of the content

In general:

- start simple
- choose boundaries that match the document
- test with real retrieval questions
- iterate based on results

For most students and beginners, `RecursiveCharacterTextSplitter` is the best first tool to learn because it is flexible, effective, and easy to explain.

---

## 11. Quick recap

- Fixed-size chunking: simple but naive
- Recursive chunking: best everyday default
- Token chunking: best for token budget control
- Markdown-aware splitting: best for docs
- Semantic chunking: best advanced option

If you are building a RAG app, the question is not just "How do I split text?" but also "How do I split text so the right information is retrieved and preserved for the model?"

That is the real goal of chunking.

---

## 12. Study questions

1. Why is chunking important in a RAG pipeline?
2. What is the difference between fixed-size and recursive chunking?
3. When would you prefer semantic chunking over recursive chunking?
4. Why is overlap useful?
5. How does document structure affect chunking decisions?

---

## 13. Suggested next steps

- Check out and try running the 'chunking_demo.py' file in the provided 'python_fast_api_langchain_app' project
