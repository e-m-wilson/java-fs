from fastapi import FastAPI

from python_fast_api_app.api.routers.students import router as students_router

app = FastAPI()

app.include_router(students_router)
