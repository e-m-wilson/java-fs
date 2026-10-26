from dataclasses import dataclass

@dataclass
class Order:
    id: int
    total: float

    def apply_discount(self, percent: float) -> None:
        self.total *= (1 - percent)
