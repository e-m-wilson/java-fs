from langchain_ollama import ChatOllama

# temperature controls the randomness of model generation
# 0 = more consistent 
# 0.3 = a bit more creative
# 1.0 = way more creative and variable 

llm = ChatOllama(
    model="lfm3",
    temperature=0
)

def ask_llm(prompt: str) -> str:
    response = llm.invoke(prompt)
    print(response.content)

ask_llm("why is the sky blue?")