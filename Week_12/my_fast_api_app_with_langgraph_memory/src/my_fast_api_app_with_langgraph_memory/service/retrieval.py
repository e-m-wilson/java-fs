from langchain_chroma import Chroma
from langchain_core.messages import HumanMessage
from langgraph.graph.state import CompiledStateGraph

def answer_from_documents(
    question: str,
    vector_store: Chroma,
    memory_graph: CompiledStateGraph,
    conversation_id: str,
    user_id: str | None,
    remember: bool,
):
    # Chroma converts the question into an embedding
    # similarity_search compares the question's embedding against the stored document embeddings.
    # It calculates similarity or distance between the vectors.
    # It retrieves the four closest matching document chunks.
    # It returns those chunks as LangChain document objects.

    retrieved_chunks = vector_store.similarity_search(question, k=4)

    context = "\n\n".join(
        [
            (
                f"Source: {chunk.metadata.get('filename', 'Unknown')}\n"
                f"Page: {chunk.metadata.get('page', 'N/A')}\n\n"
                f"Content:\n{chunk.page_content}"
            )
            for chunk in retrieved_chunks
        ]
    )

    # Keep a thread's checkpoint history scoped to the supplied user when available.
    thread_id = f"{user_id}:{conversation_id}" if user_id else conversation_id
    result = memory_graph.invoke(
        {
            "messages": [HumanMessage(content=question)],
            "context": context,
            "long_term_memory": "",
        },
        {
            "configurable": {
                "thread_id": thread_id,
                "user_id": user_id,
                "remember": remember,
            }
        },
    )
    answer = result["messages"][-1].content
    if not isinstance(answer, str):
        raise TypeError("The chat model returned a non-text response.")

    return {
        "answer": answer,
        "conversation_id": conversation_id,
        "sources": [
            {
                "filename": chunk.metadata.get("filename"),
                "page": chunk.metadata.get("page", "N/A"),
                "chunk_index": chunk.metadata.get("chunk_index"),
                "content": chunk.page_content,
            }
            for chunk in retrieved_chunks
        ]
    }
