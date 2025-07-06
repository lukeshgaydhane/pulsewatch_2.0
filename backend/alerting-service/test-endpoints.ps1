# Test script for Alerting Service (PowerShell)
# Make sure the service is running on port 8084

$BaseUrl = "http://localhost:8084"

Write-Host "=== Testing Alerting Service ===" -ForegroundColor Green
Write-Host "Base URL: $BaseUrl" -ForegroundColor Yellow
Write-Host ""

# Test 1: Send Email Alert
Write-Host "1. Sending Email Alert..." -ForegroundColor Cyan
$EmailAlertBody = @{
    type = "EMAIL"
    message = "Test email alert from PulseWatch Alerting Service"
    recipient = "test@example.com"
} | ConvertTo-Json

try {
    $EmailResponse = Invoke-RestMethod -Uri "$BaseUrl/alerts/send" -Method POST -Body $EmailAlertBody -ContentType "application/json"
    Write-Host "Email Alert Response: $($EmailResponse | ConvertTo-Json)" -ForegroundColor Green
    $EmailAlertId = $EmailResponse.id
    Write-Host "Email Alert ID: $EmailAlertId" -ForegroundColor Yellow
} catch {
    Write-Host "Error sending email alert: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Send WhatsApp Alert
Write-Host "2. Sending WhatsApp Alert..." -ForegroundColor Cyan
$WhatsAppAlertBody = @{
    type = "WHATSAPP"
    message = "Test WhatsApp alert from PulseWatch Alerting Service"
    recipient = "+1234567890"
} | ConvertTo-Json

try {
    $WhatsAppResponse = Invoke-RestMethod -Uri "$BaseUrl/alerts/send" -Method POST -Body $WhatsAppAlertBody -ContentType "application/json"
    Write-Host "WhatsApp Alert Response: $($WhatsAppResponse | ConvertTo-Json)" -ForegroundColor Green
    $WhatsAppAlertId = $WhatsAppResponse.id
    Write-Host "WhatsApp Alert ID: $WhatsAppAlertId" -ForegroundColor Yellow
} catch {
    Write-Host "Error sending WhatsApp alert: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 3: Send In-App Alert
Write-Host "3. Sending In-App Alert..." -ForegroundColor Cyan
$InAppAlertBody = @{
    type = "IN_APP"
    message = "Test in-app alert from PulseWatch Alerting Service"
    recipient = "admin-user"
} | ConvertTo-Json

try {
    $InAppResponse = Invoke-RestMethod -Uri "$BaseUrl/alerts/send" -Method POST -Body $InAppAlertBody -ContentType "application/json"
    Write-Host "In-App Alert Response: $($InAppResponse | ConvertTo-Json)" -ForegroundColor Green
    $InAppAlertId = $InAppResponse.id
    Write-Host "In-App Alert ID: $InAppAlertId" -ForegroundColor Yellow
} catch {
    Write-Host "Error sending in-app alert: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 4: Get Alert History
Write-Host "4. Getting Alert History..." -ForegroundColor Cyan
try {
    $HistoryResponse = Invoke-RestMethod -Uri "$BaseUrl/alerts/history" -Method GET
    Write-Host "Alert History: $($HistoryResponse | ConvertTo-Json)" -ForegroundColor Green
    Write-Host "Total Alerts: $($HistoryResponse.Count)" -ForegroundColor Yellow
} catch {
    Write-Host "Error getting alert history: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 5: Get Alert by ID (if we have an ID)
if ($EmailAlertId) {
    Write-Host "5. Getting Alert by ID..." -ForegroundColor Cyan
    try {
        $AlertByIdResponse = Invoke-RestMethod -Uri "$BaseUrl/alerts/history/$EmailAlertId" -Method GET
        Write-Host "Alert by ID Response: $($AlertByIdResponse | ConvertTo-Json)" -ForegroundColor Green
    } catch {
        Write-Host "Error getting alert by ID: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# Test 6: Get Alert Statistics
Write-Host "6. Getting Alert Statistics..." -ForegroundColor Cyan
try {
    $StatsResponse = Invoke-RestMethod -Uri "$BaseUrl/alerts/stats" -Method GET
    Write-Host "Alert Statistics: $($StatsResponse | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Error getting alert statistics: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 7: Get Alerts by Type
Write-Host "7. Getting Alerts by Type (EMAIL)..." -ForegroundColor Cyan
try {
    $EmailAlertsResponse = Invoke-RestMethod -Uri "$BaseUrl/alerts/history?type=EMAIL" -Method GET
    Write-Host "Email Alerts: $($EmailAlertsResponse | ConvertTo-Json)" -ForegroundColor Green
    Write-Host "Email Alerts Count: $($EmailAlertsResponse.Count)" -ForegroundColor Yellow
} catch {
    Write-Host "Error getting email alerts: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test completed ===" -ForegroundColor Green 