# AI Smart Alerting Service

AI-powered microservice for anomaly detection and failure prediction, built with Python FastAPI. This service is designed to be consumed by other backend services (such as alerting-service) via REST API. It uses machine learning algorithms to analyze incoming system metrics and predict potential failures.

---

## Features
- **REST API** with auto-generated Swagger docs
- **/ai/analyze**: Detect anomalies in system metrics (Isolation Forest, Z-score)
- **/ai/predict**: Predict failure risk using ML or rules
- **Pydantic** input/output schemas for validation
- **No direct DB access** (stateless, API-only)
- **Ready for integration** with other microservices

---

## Project Structure
```
ai-smart-alerting/
├── main.py                # FastAPI app entrypoint
├── models.py              # Pydantic schemas
├── routers/
│   └── ai_routes.py       # API endpoints
├── services/
│   └── analysis.py        # ML logic
├── requirements.txt       # Python dependencies
└── README.md              # This file
```

---

## Setup & Installation

1. **Clone the repo**
   ```sh
   git clone <repo-url>
   cd ai-smart-alerting
   ```

2. **Create a virtual environment**
   ```sh
   python -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\activate
   ```

3. **Install dependencies**
   ```sh
   pip install -r requirements.txt
   ```

4. **Run the service**
   ```sh
   uvicorn main:app --host 0.0.0.0 --port 8001 --reload
   ```

---

## API Documentation

Interactive Swagger UI available at: [http://localhost:8001/docs](http://localhost:8001/docs)

### 1. Analyze Metrics for Anomalies
- **POST** `/ai/analyze`
- **Description:** Analyze incoming metrics and detect anomalies using ML algorithms.
- **Request Body Example:**
```json
{
  "metrics": [
    {
      "metric_type": "CPU",
      "metric_name": "cpu_usage",
      "metric_value": 92.5,
      "unit": "%",
      "timestamp": "2024-07-06T18:30:00Z",
      "host": "server-1"
    }
  ],
  "threshold": 2.0,
  "window_size": 10
}
```
- **Response Example:**
```json
{
  "request_id": "b1c2...",
  "timestamp": "2024-07-06T18:30:01Z",
  "anomalies_detected": 1,
  "anomalies": [
    {
      "metric_name": "cpu_usage",
      "metric_value": 92.5,
      "anomaly_score": 0.95,
      "severity": "CRITICAL",
      "confidence": 0.98,
      "description": "CRITICAL CPU usage anomaly detected: 92.5%",
      "timestamp": "2024-07-06T18:30:01Z"
    }
  ],
  "overall_risk_score": 0.85,
  "recommendations": [
    "CRITICAL: Immediate attention required - system at high risk",
    "Investigate 1 detected anomalies",
    "Address 1 critical anomalies immediately"
  ]
}
```

### 2. Predict Failure Risk
- **POST** `/ai/predict`
- **Description:** Predict possible failures based on current and historical metrics.
- **Request Body Example:**
```json
{
  "metrics": [
    {
      "metric_type": "MEMORY",
      "metric_name": "memory_usage",
      "metric_value": 88.0,
      "unit": "%",
      "timestamp": "2024-07-06T18:30:00Z",
      "host": "server-1"
    }
  ],
  "prediction_horizon": 24,
  "system_type": "general"
}
```
- **Response Example:**
```json
{
  "request_id": "a2d3...",
  "timestamp": "2024-07-06T18:30:02Z",
  "prediction_horizon": 24,
  "overall_risk_score": 0.7,
  "high_risk_components": 1,
  "failure_risks": [
    {
      "component": "MEMORY_memory_usage",
      "risk_score": 0.8,
      "confidence": 0.7,
      "time_to_failure": 5,
      "contributing_factors": ["High resource utilization", "Memory pressure detected"],
      "mitigation_suggestions": ["Monitor memory usage patterns", "Optimize memory-intensive applications"]
    }
  ],
  "system_health_score": 0.3,
  "recommendations": [
    "HIGH: System showing failure risk patterns - proactive measures needed",
    "Review memory allocation and optimization"
  ]
}
```

### 3. Health Check
- **GET** `/ai/health`
- **Description:** Service health status and uptime.

---

## Contributing

1. Fork this repo
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Push and open a Pull Request

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Credits
- Built with [FastAPI](https://fastapi.tiangolo.com/), [scikit-learn](https://scikit-learn.org/), [Pydantic](https://docs.pydantic.dev/), and [Uvicorn](https://www.uvicorn.org/).
- Project structure inspired by [Real Python README best practices](https://realpython.com/readme-python-project/). 