from langchain_ollama import ChatOllama



llm = ChatOllama(
    model="lfm3",
    temperature=0,
    keep_alive='30m'
)

def ask_llm(prompt: str) -> str:
    response = llm.invoke(prompt)
    print(response.content)


ask_llm("What is the capital of Texas?")