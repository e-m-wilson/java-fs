from typing import Protocol
from domain.order import Order

class OrderRepository(Protocol):
    def get_by_id(self, order_id: int) -> Order:
        #...
        #pass
        raise NotImplementedError
