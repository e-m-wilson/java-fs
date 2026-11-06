import json

from langchain_core.language_models import BaseChatModel
from langchain_core.prompts import (
    ChatPromptTemplate,
    HumanMessagePromptTemplate,
    SystemMessagePromptTemplate,
)

from python_fast_api_langchain_app.repositories.student_repo import StudentRepositoryProtocol


class LlmService:
    def __init__(
        self,
        student_repo: StudentRepositoryProtocol,
        llm: BaseChatModel
    ):
        self.student_repo = student_repo
        self.llm = llm

    prompt_template = ChatPromptTemplate.from_messages([
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

    def get_students_json(self) -> str:
        students = [
                    {"id": student.id, "name": student.name}
                    for student in self.student_repo.get_all()
                ]
                # setting ensure_ascii to false preserves non-ascii characters in the prompt instead
                # of converting them to unicode escape sequences
                # eg. {"id": 3, "name": "\u674e\u660e"} is preserved as: {"id": 3, "name": "李明"}
        records = json.dumps(students, ensure_ascii=False)
        return records

    def ask(self, question: str) -> str:
        prompt = self.prompt_template.invoke({
            "students": self.get_students_json(),
            "question": question,
        })
        response = self.llm.invoke(prompt)
        return response.text
