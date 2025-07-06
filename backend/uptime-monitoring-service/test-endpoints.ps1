# Test script for Uptime Monitoring Service (PowerShell)
# Make sure the service is running on port 8083

$BaseUrl = "http://localhost:8083"

Write-Host "=== Testing Uptime Monitoring Service ===" -ForegroundColor Green
Write-Host "Base URL: $BaseUrl" -ForegroundColor Yellow
Write-Host ""

# Test 1: Register a service
Write-Host "1. Registering a service..." -ForegroundColor Cyan
$RegisterBody = @{
    name = "Test Service"
    url = "http://localhost:8081/actuator/health"
} | ConvertTo-Json

try {
    $RegisterResponse = Invoke-RestMethod -Uri "$BaseUrl/uptime/register" -Method POST -Body $RegisterBody -ContentType "application/json"
    Write-Host "Response: $($RegisterResponse | ConvertTo-Json)" -ForegroundColor Green
    $ServiceId = $RegisterResponse.id
    Write-Host "Service ID: $ServiceId" -ForegroundColor Yellow
} catch {
    Write-Host "Error registering service: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Get service status
if ($ServiceId) {
    Write-Host "2. Getting service status..." -ForegroundColor Cyan
    try {
        $StatusResponse = Invoke-RestMethod -Uri "$BaseUrl/uptime/status/$ServiceId" -Method GET
        Write-Host "Status Response: $($StatusResponse | ConvertTo-Json)" -ForegroundColor Green
    } catch {
        Write-Host "Error getting status: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# Test 3: Get all services
Write-Host "3. Getting all services..." -ForegroundColor Cyan
try {
    $AllServices = Invoke-RestMethod -Uri "$BaseUrl/uptime/services" -Method GET
    Write-Host "All Services: $($AllServices | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Error getting all services: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 4: Manual health check
if ($ServiceId) {
    Write-Host "4. Performing manual health check..." -ForegroundColor Cyan
    try {
        $CheckResponse = Invoke-RestMethod -Uri "$BaseUrl/uptime/check/$ServiceId" -Method POST
        Write-Host "Check Response: $($CheckResponse | ConvertTo-Json)" -ForegroundColor Green
    } catch {
        Write-Host "Error performing health check: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host "=== Test completed ===" -ForegroundColor Green 