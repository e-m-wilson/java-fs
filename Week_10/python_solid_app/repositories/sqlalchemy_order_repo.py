from sqlalchemy.orm import Session
from domain.order import Order
from repositories.order_repo import OrderRepository

class SqlAlchemyOrderRepository(OrderRepository):
    def __init__(self, session: Session):
        self.session = session

    def get_by_id(self, order_id: int) -> Order:
        return self.session.get(Order, order_id)
