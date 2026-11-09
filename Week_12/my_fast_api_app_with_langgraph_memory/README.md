# LangGraph memory

Run the API from the project root with:

```powershell
uv run uvicorn my_fast_api_app_with_langgraph_memory.api.main:app --reload
```

The document chat uses two kinds of PostgreSQL-backed LangGraph persistence:

- **Short-term memory:** the checkpointer stores message history per `conversation_id`.
  The first `/documents/chat` response includes a generated ID; send it back on later
  requests to continue that conversation. Use a new ID to start a separate thread.
- **Long-term memory:** when a `user_id` is supplied, the app loads a user profile
  shared across that user's threads. The graph asks the chat model to update this
  profile from explicitly stated, stable facts and preferences. Set `remember` to
  `false` to skip profile updates for a request. Profiles can also be inspected,
  replaced, or deleted with the `/documents/memory/{user_id}` GET, PUT, and DELETE
  endpoints.

Both features use the existing `DATABASE_URL` PostgreSQL database. On startup,
LangGraph's `setup()` methods create and migrate its internal checkpoint and store
tables; no manual SQL schema or pgvector extension is needed for this key/value
memory implementation. The database role must be allowed to create tables and
indexes in the target schema. The app does not create its unrelated `students`
table through these LangGraph migrations.

For example, the first request can omit `conversation_id`; reuse the returned ID
to continue and provide a stable user ID to enable long-term memory:

```json
{
  "question": "What does this document say about the topic?",
  "user_id": "user-123"
}
```

Subsequent requests should send `"conversation_id": "<id-from-the-response>"`.
`user_id` is an application-supplied scope, not an authenticated identity. Before
exposing these endpoints to untrusted clients, derive it from your authentication
system rather than trusting a caller-provided ID. Chat messages and saved profiles
are persisted in PostgreSQL; choose retention and backup policies accordingly.