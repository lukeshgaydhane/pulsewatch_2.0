from fastapi import APIRouter, HTTPException
from typing import Any
from ..models import (
    AnalysisRequest, AnalysisResponse, 
    PredictionRequest, PredictionResponse, HealthResponse
)
from ..services.analysis import AISmartAlertingService

router = APIRouter(prefix="/ai", tags=["AI Smart Alerting"])

# Initialize the AI service (singleton for now)
ai_service = AISmartAlertingService()

@router.post("/analyze", response_model=AnalysisResponse, summary="Analyze metrics for anomalies", response_description="Anomaly detection results")
def analyze_metrics(request: AnalysisRequest) -> Any:
    """
    Analyze incoming metrics and detect anomalies using ML algorithms (Isolation Forest).
    """
    try:
        result = ai_service.analyze_anomalies(request.metrics, threshold=request.threshold or 2.0)
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")

@router.post("/predict", response_model=PredictionResponse, summary="Predict failure risk", response_description="Failure risk prediction results")
def predict_failure(request: PredictionRequest) -> Any:
    """
    Predict possible failures based on current and historical metrics (Logistic Regression or rules).
    """
    try:
        result = ai_service.predict_failures(
            request.metrics, 
            historical_data=request.historical_data, 
            prediction_horizon=request.prediction_horizon
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")

@router.get("/health", response_model=HealthResponse, summary="Service health check", response_description="Health status of the AI service")
def health_check() -> Any:
    """
    Get health status of the AI Smart Alerting service.
    """
    status = ai_service.get_health_status()
    return HealthResponse(**status) 