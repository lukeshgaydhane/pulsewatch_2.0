# API Gateway Service Test Script (PowerShell)
# Make sure the service is running on port 8080

Write-Host "🧪 Testing API Gateway Service..." -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green

$BASE_URL = "http://localhost:8080"

# Test health endpoint (no auth required)
Write-Host "1. Testing Health Endpoint (No Auth)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/health" -Method GET
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test actuator endpoints (no auth required)
Write-Host "2. Testing Actuator Endpoints (No Auth)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/actuator/health" -Method GET
    Write-Host "Health Status: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test authentication (unauthorized)
Write-Host "3. Testing Authentication (Unauthorized)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/auth/users" -Method GET
    Write-Host "Unexpected success: $response" -ForegroundColor Yellow
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "✅ Correctly returned 401 Unauthorized" -ForegroundColor Green
    } else {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}
Write-Host ""

# Test authentication (authorized with admin)
Write-Host "4. Testing Authentication (Authorized - Admin)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/auth/users" -Method GET -Credential (New-Object System.Management.Automation.PSCredential("admin", (ConvertTo-SecureString "admin123" -AsPlainText -Force)))
    Write-Host "✅ Successfully authenticated with admin user" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Note: This is expected if the target service (port 8081) is not running" -ForegroundColor Cyan
}
Write-Host ""

# Test authentication (authorized with api user)
Write-Host "5. Testing Authentication (Authorized - API User)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/auth/users" -Method GET -Credential (New-Object System.Management.Automation.PSCredential("api", (ConvertTo-SecureString "api123" -AsPlainText -Force)))
    Write-Host "✅ Successfully authenticated with api user" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Note: This is expected if the target service (port 8081) is not running" -ForegroundColor Cyan
}
Write-Host ""

# Test route forwarding to different services
Write-Host "6. Testing Route Forwarding..." -ForegroundColor Yellow

$routes = @(
    @{Path="/auth/health"; Service="User Service (8081)"},
    @{Path="/metrics/health"; Service="Monitoring Service (8082)"},
    @{Path="/uptime/health"; Service="Uptime Service (8083)"},
    @{Path="/alerts/health"; Service="Alerting Service (8084)"}
)

foreach ($route in $routes) {
    Write-Host "   Testing $($route.Service)..." -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BASE_URL$($route.Path)" -Method GET -Credential (New-Object System.Management.Automation.PSCredential("admin", (ConvertTo-SecureString "admin123" -AsPlainText -Force)))
        Write-Host "   ✅ Route working: $($route.Path)" -ForegroundColor Green
    } catch {
        Write-Host "   ⚠️  Route failed: $($route.Path) - $($_.Exception.Message)" -ForegroundColor Yellow
        Write-Host "   Note: This is expected if the target service is not running" -ForegroundColor Cyan
    }
}
Write-Host ""

# Test gateway routes endpoint
Write-Host "7. Testing Gateway Routes Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/actuator/gateway/routes" -Method GET
    Write-Host "✅ Gateway routes retrieved successfully" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "✅ API Gateway testing completed!" -ForegroundColor Green
Write-Host ""
Write-Host "Summary:" -ForegroundColor Cyan
Write-Host "  - Health endpoints: Should work without authentication" -ForegroundColor White
Write-Host "  - Protected endpoints: Should require authentication" -ForegroundColor White
Write-Host "  - Route forwarding: Depends on target services being running" -ForegroundColor White
Write-Host ""
Write-Host "Expected services for full functionality:" -ForegroundColor Cyan
Write-Host "  - User Service (port 8081)" -ForegroundColor White
Write-Host "  - Monitoring Service (port 8082)" -ForegroundColor White
Write-Host "  - Uptime Service (port 8083)" -ForegroundColor White
Write-Host "  - Alerting Service (port 8084)" -ForegroundColor White 