#!/bin/bash

# Test script for Uptime Monitoring Service
# Make sure the service is running on port 8083

BASE_URL="http://localhost:8083"

echo "=== Testing Uptime Monitoring Service ==="
echo "Base URL: $BASE_URL"
echo

# Test 1: Register a service
echo "1. Registering a service..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/uptime/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Service",
    "url": "http://localhost:8081/actuator/health"
  }')

echo "Response: $REGISTER_RESPONSE"
SERVICE_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Service ID: $SERVICE_ID"
echo

# Test 2: Get service status
if [ ! -z "$SERVICE_ID" ]; then
    echo "2. Getting service status..."
    STATUS_RESPONSE=$(curl -s -X GET "$BASE_URL/uptime/status/$SERVICE_ID")
    echo "Status Response: $STATUS_RESPONSE"
    echo
fi

# Test 3: Get all services
echo "3. Getting all services..."
ALL_SERVICES=$(curl -s -X GET "$BASE_URL/uptime/services")
echo "All Services: $ALL_SERVICES"
echo

# Test 4: Manual health check
if [ ! -z "$SERVICE_ID" ]; then
    echo "4. Performing manual health check..."
    CHECK_RESPONSE=$(curl -s -X POST "$BASE_URL/uptime/check/$SERVICE_ID")
    echo "Check Response: $CHECK_RESPONSE"
    echo
fi

echo "=== Test completed ===" 