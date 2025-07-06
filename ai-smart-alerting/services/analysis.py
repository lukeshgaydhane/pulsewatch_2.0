import numpy as np
import pandas as pd
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score
import uuid
from datetime import datetime, timedelta
from typing import List, Dict, Any, Optional
import logging

from ..models import (
    MetricData, AnomalyResult, AnalysisResponse, 
    FailureRisk, PredictionResponse, MetricType
)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class AISmartAlertingService:
    """
    AI-powered smart alerting service for anomaly detection and failure prediction.
    """
    
    def __init__(self):
        self.anomaly_detector = IsolationForest(
            contamination=0.1, 
            random_state=42,
            n_estimators=100
        )
        self.scaler = StandardScaler()
        self.failure_predictor = LogisticRegression(random_state=42)
        self.is_fitted = False
        self.start_time = datetime.now()
        
        # Thresholds for different severity levels
        self.severity_thresholds = {
            'LOW': 0.3,
            'MEDIUM': 0.5,
            'HIGH': 0.7,
            'CRITICAL': 0.9
        }
        
        # Component-specific failure thresholds
        self.failure_thresholds = {
            'CPU': {'high': 85, 'critical': 95},
            'MEMORY': {'high': 80, 'critical': 90},
            'DISK': {'high': 85, 'critical': 95},
            'NETWORK': {'high': 80, 'critical': 90}
        }
        
        logger.info("AI Smart Alerting Service initialized")
    
    def _get_severity_level(self, anomaly_score: float) -> str:
        """Determine severity level based on anomaly score."""
        if anomaly_score >= self.severity_thresholds['CRITICAL']:
            return 'CRITICAL'
        elif anomaly_score >= self.severity_thresholds['HIGH']:
            return 'HIGH'
        elif anomaly_score >= self.severity_thresholds['MEDIUM']:
            return 'MEDIUM'
        else:
            return 'LOW'
    
    def _generate_anomaly_description(self, metric: MetricData, anomaly_score: float) -> str:
        """Generate human-readable description of the anomaly."""
        severity = self._get_severity_level(anomaly_score)
        
        descriptions = {
            'CPU': f"{severity} CPU usage anomaly detected: {metric.metric_value}{metric.unit}",
            'MEMORY': f"{severity} Memory usage anomaly detected: {metric.metric_value}{metric.unit}",
            'DISK': f"{severity} Disk usage anomaly detected: {metric.metric_value}{metric.unit}",
            'NETWORK': f"{severity} Network activity anomaly detected: {metric.metric_value}{metric.unit}",
            'CUSTOM': f"{severity} Custom metric anomaly detected: {metric.metric_name} = {metric.metric_value}{metric.unit}"
        }
        
        return descriptions.get(metric.metric_type.value, descriptions['CUSTOM'])
    
    def _prepare_metrics_data(self, metrics: List[MetricData]) -> np.ndarray:
        """Prepare metrics data for ML analysis."""
        # Extract numerical features
        features = []
        for metric in metrics:
            feature_vector = [
                metric.metric_value,
                metric.timestamp.hour,  # Time-based feature
                metric.timestamp.minute,
                hash(metric.metric_type.value) % 1000,  # Categorical encoding
                hash(metric.host) % 1000
            ]
            features.append(feature_vector)
        
        return np.array(features)
    
    def analyze_anomalies(self, metrics: List[MetricData], threshold: float = 2.0) -> AnalysisResponse:
        """
        Analyze metrics for anomalies using Isolation Forest algorithm.
        """
        if not metrics:
            return AnalysisResponse(
                request_id=str(uuid.uuid4()),
                anomalies_detected=0,
                anomalies=[],
                overall_risk_score=0.0,
                recommendations=["No metrics provided for analysis"]
            )
        
        try:
            # Prepare data for analysis
            features = self._prepare_metrics_data(metrics)
            
            # Fit the anomaly detector if not already fitted
            if not self.is_fitted:
                self.anomaly_detector.fit(features)
                self.is_fitted = True
                logger.info("Anomaly detector fitted with training data")
            
            # Detect anomalies
            anomaly_scores = self.anomaly_detector.decision_function(features)
            anomaly_predictions = self.anomaly_detector.predict(features)
            
            # Convert scores to 0-1 range (higher = more anomalous)
            normalized_scores = 1 - (anomaly_scores - anomaly_scores.min()) / (anomaly_scores.max() - anomaly_scores.min() + 1e-8)
            
            # Find anomalies (scores above threshold)
            anomalies = []
            for i, (metric, score, is_anomaly) in enumerate(zip(metrics, normalized_scores, anomaly_predictions)):
                if is_anomaly == -1 or score > threshold:  # Isolation Forest returns -1 for anomalies
                    anomaly_result = AnomalyResult(
                        metric_name=metric.metric_name,
                        metric_value=metric.metric_value,
                        anomaly_score=float(score),
                        severity=self._get_severity_level(score),
                        confidence=min(score * 1.2, 1.0),  # Boost confidence slightly
                        description=self._generate_anomaly_description(metric, score),
                        timestamp=datetime.now()
                    )
                    anomalies.append(anomaly_result)
            
            # Calculate overall risk score
            overall_risk_score = np.mean(normalized_scores) if len(normalized_scores) > 0 else 0.0
            
            # Generate recommendations
            recommendations = self._generate_recommendations(anomalies, overall_risk_score)
            
            return AnalysisResponse(
                request_id=str(uuid.uuid4()),
                anomalies_detected=len(anomalies),
                anomalies=anomalies,
                overall_risk_score=float(overall_risk_score),
                recommendations=recommendations
            )
            
        except Exception as e:
            logger.error(f"Error in anomaly analysis: {str(e)}")
            return AnalysisResponse(
                request_id=str(uuid.uuid4()),
                anomalies_detected=0,
                anomalies=[],
                overall_risk_score=0.0,
                recommendations=[f"Analysis failed: {str(e)}"]
            )
    
    def predict_failures(self, metrics: List[MetricData], 
                        historical_data: Optional[List[MetricData]] = None,
                        prediction_horizon: int = 24) -> PredictionResponse:
        """
        Predict potential system failures based on current and historical metrics.
        """
        if not metrics:
            return PredictionResponse(
                request_id=str(uuid.uuid4()),
                prediction_horizon=prediction_horizon,
                overall_risk_score=0.0,
                high_risk_components=0,
                failure_risks=[],
                system_health_score=1.0,
                recommendations=["No metrics provided for prediction"]
            )
        
        try:
            failure_risks = []
            component_risks = {}
            
            # Analyze each metric type for failure risk
            for metric in metrics:
                metric_type = metric.metric_type.value
                thresholds = self.failure_thresholds.get(metric_type, {'high': 80, 'critical': 90})
                
                # Calculate risk based on thresholds and trends
                risk_score = self._calculate_component_risk(metric, thresholds)
                confidence = self._calculate_prediction_confidence(metric, historical_data)
                
                # Estimate time to failure based on current trends
                time_to_failure = self._estimate_time_to_failure(metric, historical_data)
                
                # Generate contributing factors
                contributing_factors = self._identify_contributing_factors(metric, risk_score)
                
                # Generate mitigation suggestions
                mitigation_suggestions = self._generate_mitigation_suggestions(metric, risk_score)
                
                failure_risk = FailureRisk(
                    component=f"{metric_type}_{metric.metric_name}",
                    risk_score=risk_score,
                    confidence=confidence,
                    time_to_failure=time_to_failure,
                    contributing_factors=contributing_factors,
                    mitigation_suggestions=mitigation_suggestions
                )
                
                failure_risks.append(failure_risk)
                component_risks[metric_type] = max(component_risks.get(metric_type, 0), risk_score)
            
            # Calculate overall system risk
            overall_risk_score = np.mean(list(component_risks.values())) if component_risks else 0.0
            
            # Count high-risk components
            high_risk_components = sum(1 for risk in failure_risks if risk.risk_score > 0.7)
            
            # Calculate system health score (inverse of risk)
            system_health_score = max(0.0, 1.0 - overall_risk_score)
            
            # Generate system-wide recommendations
            recommendations = self._generate_system_recommendations(failure_risks, overall_risk_score)
            
            return PredictionResponse(
                request_id=str(uuid.uuid4()),
                prediction_horizon=prediction_horizon,
                overall_risk_score=float(overall_risk_score),
                high_risk_components=high_risk_components,
                failure_risks=failure_risks,
                system_health_score=float(system_health_score),
                recommendations=recommendations
            )
            
        except Exception as e:
            logger.error(f"Error in failure prediction: {str(e)}")
            return PredictionResponse(
                request_id=str(uuid.uuid4()),
                prediction_horizon=prediction_horizon,
                overall_risk_score=0.0,
                high_risk_components=0,
                failure_risks=[],
                system_health_score=1.0,
                recommendations=[f"Prediction failed: {str(e)}"]
            )
    
    def _calculate_component_risk(self, metric: MetricData, thresholds: Dict[str, float]) -> float:
        """Calculate failure risk for a specific component."""
        value = metric.metric_value
        
        if value >= thresholds['critical']:
            return 0.9 + (value - thresholds['critical']) / 100  # Very high risk
        elif value >= thresholds['high']:
            return 0.6 + (value - thresholds['high']) / (thresholds['critical'] - thresholds['high']) * 0.3
        elif value >= thresholds['high'] * 0.8:
            return 0.3 + (value - thresholds['high'] * 0.8) / (thresholds['high'] - thresholds['high'] * 0.8) * 0.3
        else:
            return max(0.1, value / thresholds['high'] * 0.3)  # Low risk but not zero
    
    def _calculate_prediction_confidence(self, metric: MetricData, 
                                       historical_data: Optional[List[MetricData]]) -> float:
        """Calculate confidence in the prediction based on data quality."""
        if not historical_data:
            return 0.5  # Medium confidence without historical data
        
        # Count historical data points for this metric
        historical_count = sum(1 for h in historical_data 
                             if h.metric_name == metric.metric_name and h.host == metric.host)
        
        # Higher confidence with more historical data
        return min(0.9, 0.5 + historical_count * 0.02)
    
    def _estimate_time_to_failure(self, metric: MetricData, 
                                 historical_data: Optional[List[MetricData]]) -> Optional[int]:
        """Estimate time to failure based on current trends."""
        if not historical_data:
            return None
        
        # Simple linear trend analysis
        relevant_history = [h for h in historical_data 
                          if h.metric_name == metric.metric_name and h.host == metric.host]
        
        if len(relevant_history) < 3:
            return None
        
        # Sort by timestamp
        relevant_history.sort(key=lambda x: x.timestamp)
        
        # Calculate trend
        values = [h.metric_value for h in relevant_history]
        time_diffs = [(h.timestamp - relevant_history[0].timestamp).total_seconds() / 3600 
                     for h in relevant_history]
        
        if len(set(time_diffs)) < 2:
            return None
        
        # Simple linear regression
        slope = np.polyfit(time_diffs, values, 1)[0]
        
        if slope <= 0:
            return None  # No increasing trend
        
        # Estimate time to reach critical threshold
        current_value = metric.metric_value
        critical_threshold = self.failure_thresholds.get(metric.metric_type.value, {}).get('critical', 90)
        
        if current_value >= critical_threshold:
            return 0  # Already at critical level
        
        time_to_critical = (critical_threshold - current_value) / slope
        return max(0, int(time_to_critical))
    
    def _identify_contributing_factors(self, metric: MetricData, risk_score: float) -> List[str]:
        """Identify factors contributing to the failure risk."""
        factors = []
        
        if risk_score > 0.8:
            factors.append("Critical threshold exceeded")
        elif risk_score > 0.6:
            factors.append("High resource utilization")
        
        if metric.metric_type == MetricType.CPU:
            factors.append("High CPU load detected")
        elif metric.metric_type == MetricType.MEMORY:
            factors.append("Memory pressure detected")
        elif metric.metric_type == MetricType.DISK:
            factors.append("Disk space pressure detected")
        elif metric.metric_type == MetricType.NETWORK:
            factors.append("Network congestion detected")
        
        return factors
    
    def _generate_mitigation_suggestions(self, metric: MetricData, risk_score: float) -> List[str]:
        """Generate mitigation suggestions based on the metric and risk level."""
        suggestions = []
        
        if metric.metric_type == MetricType.CPU:
            if risk_score > 0.8:
                suggestions.extend([
                    "Immediately scale up CPU resources",
                    "Check for runaway processes",
                    "Consider load balancing"
                ])
            elif risk_score > 0.6:
                suggestions.extend([
                    "Monitor CPU usage trends",
                    "Optimize application performance",
                    "Consider resource scaling"
                ])
        
        elif metric.metric_type == MetricType.MEMORY:
            if risk_score > 0.8:
                suggestions.extend([
                    "Immediately increase memory allocation",
                    "Check for memory leaks",
                    "Restart memory-intensive services"
                ])
            elif risk_score > 0.6:
                suggestions.extend([
                    "Monitor memory usage patterns",
                    "Optimize memory-intensive applications",
                    "Consider memory upgrade"
                ])
        
        elif metric.metric_type == MetricType.DISK:
            if risk_score > 0.8:
                suggestions.extend([
                    "Immediately free up disk space",
                    "Remove unnecessary files",
                    "Consider disk expansion"
                ])
            elif risk_score > 0.6:
                suggestions.extend([
                    "Monitor disk usage trends",
                    "Implement log rotation",
                    "Consider storage optimization"
                ])
        
        return suggestions
    
    def _generate_recommendations(self, anomalies: List[AnomalyResult], 
                                overall_risk_score: float) -> List[str]:
        """Generate system-wide recommendations based on analysis results."""
        recommendations = []
        
        if overall_risk_score > 0.8:
            recommendations.append("CRITICAL: Immediate attention required - system at high risk")
        elif overall_risk_score > 0.6:
            recommendations.append("HIGH: System showing concerning patterns - monitor closely")
        elif overall_risk_score > 0.4:
            recommendations.append("MEDIUM: Some anomalies detected - review system health")
        
        if anomalies:
            recommendations.append(f"Investigate {len(anomalies)} detected anomalies")
            
            # Group by severity
            critical_count = sum(1 for a in anomalies if a.severity == 'CRITICAL')
            high_count = sum(1 for a in anomalies if a.severity == 'HIGH')
            
            if critical_count > 0:
                recommendations.append(f"Address {critical_count} critical anomalies immediately")
            if high_count > 0:
                recommendations.append(f"Review {high_count} high-severity anomalies")
        
        if not recommendations:
            recommendations.append("System appears healthy - continue monitoring")
        
        return recommendations
    
    def _generate_system_recommendations(self, failure_risks: List[FailureRisk], 
                                       overall_risk_score: float) -> List[str]:
        """Generate system-wide recommendations for failure prevention."""
        recommendations = []
        
        if overall_risk_score > 0.8:
            recommendations.append("CRITICAL: System at high risk of failure - immediate action required")
        elif overall_risk_score > 0.6:
            recommendations.append("HIGH: System showing failure risk patterns - proactive measures needed")
        elif overall_risk_score > 0.4:
            recommendations.append("MEDIUM: Some components at risk - monitor and maintain")
        
        # Count components by risk level
        critical_components = [r for r in failure_risks if r.risk_score > 0.8]
        high_risk_components = [r for r in failure_risks if 0.6 < r.risk_score <= 0.8]
        
        if critical_components:
            recommendations.append(f"Address {len(critical_components)} critical components immediately")
        if high_risk_components:
            recommendations.append(f"Review {len(high_risk_components)} high-risk components")
        
        # Specific recommendations based on component types
        cpu_risks = [r for r in failure_risks if 'CPU' in r.component]
        memory_risks = [r for r in failure_risks if 'MEMORY' in r.component]
        disk_risks = [r for r in failure_risks if 'DISK' in r.component]
        
        if cpu_risks:
            recommendations.append("Consider CPU scaling or load balancing")
        if memory_risks:
            recommendations.append("Review memory allocation and optimization")
        if disk_risks:
            recommendations.append("Implement storage monitoring and cleanup")
        
        if not recommendations:
            recommendations.append("System appears healthy - continue regular monitoring")
        
        return recommendations
    
    def get_health_status(self) -> Dict[str, Any]:
        """Get service health status."""
        uptime = (datetime.now() - self.start_time).total_seconds()
        
        return {
            "status": "healthy",
            "timestamp": datetime.now(),
            "version": "1.0.0",
            "ml_models_loaded": self.is_fitted,
            "uptime_seconds": uptime
        } 