from pydantic import BaseModel, Field
from typing import List, Dict, Optional, Any
from datetime import datetime
from enum import Enum


class MetricType(str, Enum):
    CPU = "CPU"
    MEMORY = "MEMORY"
    DISK = "DISK"
    NETWORK = "NETWORK"
    CUSTOM = "CUSTOM"


class MetricData(BaseModel):
    metric_type: MetricType = Field(..., description="Type of metric being analyzed")
    metric_name: str = Field(..., description="Name of the specific metric")
    metric_value: float = Field(..., description="Current value of the metric")
    unit: str = Field(..., description="Unit of measurement (%, GB, MB/s, etc.)")
    timestamp: datetime = Field(default_factory=datetime.now, description="Timestamp of the metric")
    host: str = Field(..., description="Host/server identifier")
    additional_data: Optional[Dict[str, Any]] = Field(default=None, description="Additional context data")


class AnalysisRequest(BaseModel):
    metrics: List[MetricData] = Field(..., description="List of metrics to analyze for anomalies")
    threshold: Optional[float] = Field(default=2.0, description="Z-score threshold for anomaly detection")
    window_size: Optional[int] = Field(default=10, description="Number of historical data points to consider")


class AnomalyResult(BaseModel):
    metric_name: str = Field(..., description="Name of the metric with anomaly")
    metric_value: float = Field(..., description="Anomalous value detected")
    anomaly_score: float = Field(..., description="Anomaly detection score (0-1)")
    severity: str = Field(..., description="Severity level: LOW, MEDIUM, HIGH, CRITICAL")
    confidence: float = Field(..., description="Confidence level of the detection (0-1)")
    description: str = Field(..., description="Description of the anomaly")
    timestamp: datetime = Field(..., description="When the anomaly was detected")


class AnalysisResponse(BaseModel):
    request_id: str = Field(..., description="Unique identifier for this analysis request")
    timestamp: datetime = Field(default_factory=datetime.now, description="Analysis timestamp")
    anomalies_detected: int = Field(..., description="Number of anomalies found")
    anomalies: List[AnomalyResult] = Field(..., description="List of detected anomalies")
    overall_risk_score: float = Field(..., description="Overall system risk score (0-1)")
    recommendations: List[str] = Field(..., description="Recommended actions based on analysis")


class PredictionRequest(BaseModel):
    metrics: List[MetricData] = Field(..., description="Current metrics for failure prediction")
    historical_data: Optional[List[MetricData]] = Field(default=None, description="Historical metrics for context")
    prediction_horizon: int = Field(default=24, description="Hours ahead to predict failure risk")
    system_type: str = Field(default="general", description="Type of system being monitored")


class FailureRisk(BaseModel):
    component: str = Field(..., description="Component at risk of failure")
    risk_score: float = Field(..., description="Failure risk probability (0-1)")
    confidence: float = Field(..., description="Confidence in the prediction (0-1)")
    time_to_failure: Optional[int] = Field(default=None, description="Estimated hours until failure")
    contributing_factors: List[str] = Field(..., description="Factors contributing to the risk")
    mitigation_suggestions: List[str] = Field(..., description="Suggested mitigation actions")


class PredictionResponse(BaseModel):
    request_id: str = Field(..., description="Unique identifier for this prediction request")
    timestamp: datetime = Field(default_factory=datetime.now, description="Prediction timestamp")
    prediction_horizon: int = Field(..., description="Hours ahead the prediction covers")
    overall_risk_score: float = Field(..., description="Overall system failure risk (0-1)")
    high_risk_components: int = Field(..., description="Number of components with high failure risk")
    failure_risks: List[FailureRisk] = Field(..., description="Detailed failure risk analysis")
    system_health_score: float = Field(..., description="Overall system health score (0-1)")
    recommendations: List[str] = Field(..., description="System-wide recommendations")


class HealthResponse(BaseModel):
    status: str = Field(..., description="Service health status")
    timestamp: datetime = Field(default_factory=datetime.now, description="Health check timestamp")
    version: str = Field(..., description="Service version")
    ml_models_loaded: bool = Field(..., description="Whether ML models are loaded and ready")
    uptime_seconds: float = Field(..., description="Service uptime in seconds") 