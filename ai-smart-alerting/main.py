# Entry point for the AI Smart Alerting Service
# Requires FastAPI and uvicorn to be installed (see requirements.txt)
import uvicorn
from fastapi import FastAPI
from routers import ai_routes

app = FastAPI(
    title="AI Smart Alerting Service",
    description="AI-powered microservice for anomaly detection and failure prediction.\n\n"
                "**Endpoints:**\n"
                "- POST /ai/analyze: Analyze metrics for anomalies.\n"
                "- POST /ai/predict: Predict failure risk.\n\n"
                "**Docs:** Swagger UI available at /docs",
    version="1.0.0",
    contact={
        "name": "Pulsewatch AI Team",
        "email": "support@pulsewatch.ai"
    }
)

# Include routers
app.include_router(ai_routes.router)

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True) 