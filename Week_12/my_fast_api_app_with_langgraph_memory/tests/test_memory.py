import unittest
from unittest.mock import Mock

from langchain_core.messages import AIMessage, HumanMessage
from langgraph.checkpoint.memory import InMemorySaver
from langgraph.store.memory import InMemoryStore

from my_fast_api_app_with_langgraph_memory.service.memory import build_memory_graph


class MemoryGraphTests(unittest.TestCase):
    def test_thread_history_and_user_profile_are_persisted_separately(self):
        llm = Mock()
        llm.invoke.side_effect = [
            AIMessage(content="First answer"),
            AIMessage(content="The user enjoys hiking."),
            AIMessage(content="Second-thread answer"),
            AIMessage(content="Follow-up answer"),
            AIMessage(content="Other-user answer"),
        ]
        store = InMemoryStore()
        graph = build_memory_graph(llm, InMemorySaver(), store)

        graph.invoke(
            {
                "messages": [HumanMessage(content="I enjoy hiking.")],
                "context": "Relevant document text",
                "long_term_memory": "",
            },
            {
                "configurable": {
                    "thread_id": "conversation-1",
                    "user_id": "user-1",
                    "remember": True,
                }
            },
        )

        profile = store.get(("users", "user-1"), "profile")
        self.assertEqual(profile.value["summary"], "The user enjoys hiking.")

        result = graph.invoke(
            {
                "messages": [HumanMessage(content="Can you expand on that?")],
                "context": "More relevant document text",
                "long_term_memory": "",
            },
            {
                "configurable": {
                    "thread_id": "conversation-2",
                    "user_id": "user-1",
                    "remember": False,
                }
            },
        )

        self.assertEqual(result["messages"][-1].content, "Second-thread answer")
        second_thread_messages = llm.invoke.call_args_list[-1].args[0]
        self.assertIn("The user enjoys hiking.", second_thread_messages[0].content)
        self.assertEqual(
            [message.content for message in second_thread_messages[1:]],
            ["Can you expand on that?"],
        )

        result = graph.invoke(
            {
                "messages": [HumanMessage(content="What did I ask just before?")],
                "context": "Another relevant document passage",
                "long_term_memory": "",
            },
            {
                "configurable": {
                    "thread_id": "conversation-2",
                    "user_id": "user-1",
                    "remember": False,
                }
            },
        )
        self.assertEqual(result["messages"][-1].content, "Follow-up answer")
        follow_up_messages = llm.invoke.call_args_list[-1].args[0]
        self.assertEqual(
            [message.content for message in follow_up_messages[1:]],
            [
                "Can you expand on that?",
                "Second-thread answer",
                "What did I ask just before?",
            ],
        )

        graph.invoke(
            {
                "messages": [HumanMessage(content="A different person's question")],
                "context": "Relevant document text",
                "long_term_memory": "",
            },
            {
                "configurable": {
                    "thread_id": "conversation-3",
                    "user_id": "user-2",
                    "remember": False,
                }
            },
        )
        other_user_messages = llm.invoke.call_args_list[-1].args[0]
        self.assertNotIn("The user enjoys hiking.", other_user_messages[0].content)
        self.assertEqual(llm.invoke.call_count, 5)


if __name__ == "__main__":
    unittest.main()
