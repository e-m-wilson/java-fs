# LangGraph 

### LangGraph is an open-source framework developed by LangChain for building, managing, and orchestrating stateful, multi-step AI agents and workflows using graph-based architecture. It allows developers to build AI applications where multiple LLM calls, tools, agents, and decision-making steps work together.

- Stateful? - LangGraph apps can store and carry data across multiple steps in logic. Basically it makes it easier to store globally accessible data instead of losing it after LLM prompt and response. 
- Multi-step? - LangGraph apps can execute multiple operations sequentially or conditionally. It’s way easier to conditionally control the flow of data.


## LangChain vs LangGraph


| LangChain | LangGraph |
|-|-|
| Framework for building LLM applications | Framework for orchestrating stateful workflows |
| Provides integrations and abstractions | Provides graph-based execution |
| Useful for chains and tool integrations | Useful for complex agents and workflows |
| Often linear workflows | Supports cycles and conditional paths |

## How LangGraph works

LangGraph represents an AI workflow as a graph consisting of three main components:

- Nodes: Individual steps or functions in the workflow. For example, calling an LLM, searching the web, or executing a tool.

- Edges: Connections that define how execution moves from one node to another.

- State: Shared information that gets updated and passed between nodes throughout the workflow.

See this snippet at the end of the provided 'memory.py' file, here we can see all three (more on checkpointers and stores later!):
```
    builder = StateGraph(ChatState)
    builder.add_node("load_long_term_memory", load_long_term_memory)
    builder.add_node("answer", answer)
    builder.add_node("update_long_term_memory", update_long_term_memory)
    builder.add_edge(START, "load_long_term_memory")
    builder.add_edge("load_long_term_memory", "answer")
    builder.add_edge("answer", "update_long_term_memory")
    builder.add_edge("update_long_term_memory", END)

    return builder.compile(checkpointer=checkpointer, store=store)
```

### Terminology Cheat Sheet

- Graphs = A Graph represents the overall flow of the application. It’s made up of nodes connected by edges. The Graph is defined and returned in a single terminal function, basically putting state, nodes, and edges together.
- Nodes = A single function or “step” in the graph. These steps can be anything from calling the LLM or grabbing context for a better response. Nodes can read and update app state. Individual nodes are defined in individual functions. They’re wired together via edges in the function that defines the Graph.
- Edges = Connections between nodes that can control execution order. These can be conditional or unconditional, meaning they may or may not branch based on app state. Edges appear in the function the defines the Graph, connecting nodes together to define the overall app flow.
- State = A shared data object that’s easily accessible to the overall graph’s components. State is good for storing any data that benefits from being globally accessible. Think baseline inputs, conversation history, or important retrieved docs. State is defined as a standalone Class that is referred to in the function that defines the Graph.

## LangGraph Memory

There are two major types of memory:

- Short-term memory: Remembers information within a particular conversation or thread.

- Long-term memory: Remembers information across multiple conversations and threads.

LangGraph manages these using two important mechanisms:

- Checkpointers: Used to save and restore conversation or workflow state.

- Stores: Used to save and retrieve long-term information across conversations.

BOTH typically end up saving into a DB for persistence in production applications. For example, individual short-term conversation threads could be saved and associated with a user account, and long-term memory can be used to store facts about a user to provide a personalized user experience.

## See the provided README in the project for further details on the implementation 