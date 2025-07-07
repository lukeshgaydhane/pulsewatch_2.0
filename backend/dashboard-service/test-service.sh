#!/bin/bash

# Dashboard Service Test Script
# Make sure the service is running on port 8086

echo "🧪 Testing Dashboard Service..."
echo "=================================="

BASE_URL="http://localhost:8086"

# Test health endpoint
echo "1. Testing Health Endpoint..."
curl -s -X GET "$BASE_URL/dashboard/health" | jq .
echo ""

# Test dashboard summary (will fail if other services are not running)
echo "2. Testing Dashboard Summary..."
curl -s -X GET "$BASE_URL/dashboard/summary?userId=1" | jq .
echo ""

# Test alerts endpoint
echo "3. Testing Alerts Endpoint..."
curl -s -X GET "$BASE_URL/dashboard/alerts?limit=5" | jq .
echo ""

# Test predictions endpoint
echo "4. Testing Predictions Endpoint..."
curl -s -X GET "$BASE_URL/dashboard/predictions" | jq .
echo ""

# Test anomalies endpoint
echo "5. Testing Anomalies Endpoint..."
curl -s -X GET "$BASE_URL/dashboard/anomalies" | jq .
echo ""

# Test user config endpoint
echo "6. Testing User Config Endpoint..."
curl -s -X GET "$BASE_URL/dashboard/config/1" | jq .
echo ""

echo "✅ Dashboard Service testing completed!"
echo ""
echo "Note: Some endpoints may return errors if dependent services are not running."
echo "Expected services:"
echo "  - alerting-service (port 8081)"
echo "  - ai-smart-alerting (port 8082)"
echo "  - threshold-service (port 8085)" 