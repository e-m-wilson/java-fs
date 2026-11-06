import json

from langchain_core.language_models import BaseChatModel
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import (
    ChatPromptTemplate,
    HumanMessagePromptTemplate,
    SystemMessagePromptTemplate,
)
from langchain_core.runnables import RunnablePassthrough

from python_fast_api_langchain_app.repositories.student_repo import StudentRepositoryProtocol


class LlmService:
    def __init__(
        self,
        student_repo: StudentRepositoryProtocol,
        llm: BaseChatModel,
    ):
        self.student_repo = student_repo
        self.llm = llm

        # A way to think about LCEL:
        # Each stage in the chain receives the previous stage's output.
        # The `|` operator means "send the result to the next step."
        #
        # In plain English, this chain does this:
        # 1. Take the incoming question
        # 2. Add the current student records as JSON
        # 3. Build the prompt using the template
        # 4. Send it to the LLM
        # 5. Convert the model output into a plain string
        self.prompt_template = ChatPromptTemplate.from_messages([
            SystemMessagePromptTemplate.from_template(
                "Answer the user's question using only the supplied student "
                "records. The records contain only student IDs and names. "
                "If the records do not contain enough information, say so. "
                "Do not invent records or claim to have accessed anything "
                "outside this data."
            ),
            HumanMessagePromptTemplate.from_template(
                "Student records (JSON): {students}\n\nQuestion: {question}"
            ),
        ])

        self.answer_chain = (
            {
                # `RunnablePassthrough` passes the original input question through
                # unchanged. We also inject the student JSON from our repository.

                # `_` indicates that the lambda accepts a single argument but
                # intentionally doesn't use it.
                # The lambda receives the original chain input, but we don't need
                # that input when retrieving our students in JSON format from our other function.
                # we also don't technically need the lambda, but using it ensures we don't 
                # retrieve the JSON until the chain runs. without the lambda, we would be 
                # calling get_students_json() whenever the service itself is created
                # this is a safety thing to make sure we are always getting the up-to-date students 
                "question": RunnablePassthrough(),
                "students": lambda _: self.get_students_json(),
            }
            | self.prompt_template  # Build a ChatPrompt from the dict above
            | self.llm              # Send the prompt to the model
            | StrOutputParser()    # Turn the model output into a simple string
        )

    def get_students_json(self) -> str:
        students = [
            {"id": student.id, "name": student.name}
            for student in self.student_repo.get_all()
        ]
        # `ensure_ascii=False` keeps names readable in the prompt.
        # Example: "李明" stays as "李明" instead of "\u674e\u660e".
        return json.dumps(students, ensure_ascii=False)

    def ask(self, question: str) -> str:
        # This is our LCEL demo:
        # we call the whole chain with a user question and get a final string back.
        return self.answer_chain.invoke(question)
