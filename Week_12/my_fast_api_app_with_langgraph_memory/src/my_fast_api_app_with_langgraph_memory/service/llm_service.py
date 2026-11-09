import json

from langchain_core.language_models import BaseChatModel
from langchain_core.prompts import (
    ChatPromptTemplate,
    HumanMessagePromptTemplate,
    SystemMessagePromptTemplate
)
from my_fast_api_app_with_langgraph_memory.repository.student_repo_protocol import StudentRepoProtocol


class LlmService:
    def __init__(
            self,
            student_repo: StudentRepoProtocol,
            llm: BaseChatModel
    ):
        self.student_repo = student_repo
        self.llm = llm
        # ChatPromptTemplate - more versatile 
        # PromptTemplate - less versatile 
        self.prompt_template = ChatPromptTemplate.from_messages(
            [
                SystemMessagePromptTemplate.from_template(
                        """Answer the user's question using only the supplied student 
                        records. The records contain only student IDs and names. 
                        If the records do not contain enough information, say so. 
                        Do not invent records or claim to have accessed anything 
                        outside this data."""
                ),
                HumanMessagePromptTemplate.from_template(
                    "Student records (JSON): {records}\n\nQuestion: {prompt}"
                )
            ]
        )

    def get_students_json(self) -> str:
        students = [
            {"id": student.id, "name":student.name}
            for student in self.student_repo.get_all()
        ]
        # you would get this: /u34343 NOT the kanji/chinese characters
        # if we have ensure_ascii=True
        records = json.dumps(students, ensure_ascii=False)
        return records

    def ask(self, question:str) -> str:
        prompt = self.prompt_template.invoke({
            "records": self.get_students_json(),
            "prompt": question,
        })
        response = self.llm.invoke(prompt)
        return response.text
        

    