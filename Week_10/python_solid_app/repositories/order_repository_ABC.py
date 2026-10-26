'''
Docstring for repositories.order_repository_ABC

This is an example of the repo pattern using ABC
This is NOT recommended for most applications
Use Protocols instead!
'''

from abc import ABC, abstractmethod
from domain.order import Order

class OrderRepository(ABC):
    @abstractmethod
    def get_by_id(self, order_id: int) -> Order:
        #...
        #pass
        raise NotImplementedError
