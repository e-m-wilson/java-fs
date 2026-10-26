from fastapi import FastAPI, Depends
from services.order_service import OrderService
from repositories.sqlalchemy_order_repository_ABC import SqlAlchemyOrderRepository

app = FastAPI()

def get_order_service():
    repo = SqlAlchemyOrderRepository(...)
    return OrderService(repo)

@app.post("/orders/{order_id}/discount")
def discount(order_id: int, percent: float, svc=Depends(get_order_service)):
    return svc.apply_discount(order_id, percent)
