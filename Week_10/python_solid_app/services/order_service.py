from repositories.order_repository_ABC import OrderRepository

class OrderService:
    def __init__(self, repo: OrderRepository):
        self.repo = repo

    def apply_discount(self, order_id: int, percent: float):
        order = self.repo.get_by_id(order_id)
        order.apply_discount(percent)
        return order
