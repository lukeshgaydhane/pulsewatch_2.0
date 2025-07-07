# Dashboard Service Test Script (PowerShell)
# Make sure the service is running on port 8086

Write-Host "🧪 Testing Dashboard Service..." -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green

$BASE_URL = "http://localhost:8086"

# Test health endpoint
Write-Host "1. Testing Health Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/health" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test dashboard summary (will fail if other services are not running)
Write-Host "2. Testing Dashboard Summary..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/summary?userId=1" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test alerts endpoint
Write-Host "3. Testing Alerts Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/alerts?limit=5" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test predictions endpoint
Write-Host "4. Testing Predictions Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/predictions" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test anomalies endpoint
Write-Host "5. Testing Anomalies Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/anomalies" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test user config endpoint
Write-Host "6. Testing User Config Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/config/1" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test database endpoints
Write-Host "7. Testing Database Endpoints..." -ForegroundColor Yellow

# Test save metric
Write-Host "  7.1. Testing Save Metric..." -ForegroundColor Cyan
try {
    $metricData = @{
        userId = 1
        metricType = "TEST_METRIC"
        metricValue = "test_value_123"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/metrics" -Method POST -Body $metricData -ContentType "application/json"
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test get user metrics
Write-Host "  7.2. Testing Get User Metrics..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/metrics/1" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test get metrics by type
Write-Host "  7.3. Testing Get Metrics by Type..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/metrics/1/TEST_METRIC" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test metric count
Write-Host "  7.4. Testing Metric Count..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/dashboard/metrics/count/1/TEST_METRIC" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "✅ Dashboard Service testing completed!" -ForegroundColor Green
Write-Host ""
Write-Host "Note: Some endpoints may return errors if dependent services are not running." -ForegroundColor Cyan
Write-Host "Expected services:" -ForegroundColor Cyan
Write-Host "  - alerting-service (port 8081)" -ForegroundColor White
Write-Host "  - ai-smart-alerting (port 8082)" -ForegroundColor White
Write-Host "  - threshold-service (port 8085)" -ForegroundColor White
Write-Host "  - PostgreSQL database (port 5432)" -ForegroundColor White 