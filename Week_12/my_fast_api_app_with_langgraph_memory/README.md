# LangGraph memory

Run the API from the project root with:

```powershell
uv sync # (if first time setup and you just pulled this from github)
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
exposing these endpoints to untrusted clients, we should derive it from an authentication
system rather than trusting a caller-provided ID. (But we will anyway since we haven't implemented auth/security topics in this project/curriculum) Chat messages and saved profiles
are persisted in PostgreSQL; We haven't implemented any retention and backup policies, so by default, all short/long term memory is saved indefinitely as it is. 

## Files to note:
1. main.py = Implements a graph from our terminal function inside of 'memory.py' 
  - Here we use PostgresSaver & PostgresStore to help initialize a checkpointer and store, respectively 
  - The 'setup()' methods push necessary table migrations for each of these to our DB
2. memory.py = contains our terminal function that builds our graph and sets up nodes + edges 
3. documents.py = Here we have '/memory' endpoints to manage long-term user context. We also have '/chat/ endpoint that passes an instance of our memory graph to our service layer 'retrieval.py' file 
4. retrieval.py = answers user questions; Keeps a thread's checkpoint history scoped to the supplied user when available.


## FOR MAC

Some reported issues with running ```uv sync``` on mac. (maybe to do with m1 chips, I'm not sure)

I ran the following to generate a requirements.txt file from the 'uv.lock' file.
```
uv export --format requirements-txt --output-file requirements.txt
```

This will allow people on mac to install dependencies like so:
```
uv pip install -r requirements.txt
```

OR (with venv):
```
pip install -r requirements.txt
```