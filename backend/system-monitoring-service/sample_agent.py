#!/usr/bin/env python3
"""
Sample Python agent for collecting system metrics and sending to the monitoring service.
This script demonstrates how external agents can send metrics to the system-monitoring-service.
"""

import requests
import json
import time
from datetime import datetime
import random  # For demo purposes, replace with actual system monitoring libraries

def collect_system_metrics():
    """Collect system metrics (simulated for demo purposes)"""
    metrics = []
    
    # Simulate CPU metrics
    cpu_usage = random.uniform(10, 90)
    metrics.append({
        "metricType": "CPU",
        "metricName": "cpu_usage",
        "metricValue": round(cpu_usage, 2),
        "unit": "%",
        "additionalData": json.dumps({"cores": 8, "load_average": random.uniform(0.5, 3.0)})
    })
    
    # Simulate Memory metrics
    memory_usage = random.uniform(20, 80)
    metrics.append({
        "metricType": "MEMORY",
        "metricName": "memory_usage",
        "metricValue": round(memory_usage, 2),
        "unit": "%",
        "additionalData": json.dumps({"total_gb": 16, "available_gb": round(16 * (1 - memory_usage/100), 2)})
    })
    
    # Simulate Disk metrics
    disk_usage = random.uniform(30, 85)
    metrics.append({
        "metricType": "DISK",
        "metricName": "disk_usage",
        "metricValue": round(disk_usage, 2),
        "unit": "%",
        "additionalData": json.dumps({"total_gb": 500, "free_gb": round(500 * (1 - disk_usage/100), 2)})
    })
    
    # Simulate Network metrics
    network_speed = random.uniform(10, 1000)
    metrics.append({
        "metricType": "NETWORK",
        "metricName": "network_speed",
        "metricValue": round(network_speed, 2),
        "unit": "Mbps",
        "additionalData": json.dumps({"interface": "eth0", "packets_sent": random.randint(1000, 10000)})
    })
    
    return {
        "host": "demo-server-01",
        "timestamp": datetime.now().isoformat(),
        "metrics": metrics
    }

def send_metrics_to_service(service_url="http://localhost:8082/metrics"):
    """Send metrics to the monitoring service"""
    try:
        data = collect_system_metrics()
        
        print(f"Sending metrics for host: {data['host']}")
        print(f"Timestamp: {data['timestamp']}")
        print(f"Number of metrics: {len(data['metrics'])}")
        
        response = requests.post(
            service_url,
            json=data,
            headers={'Content-Type': 'application/json'},
            timeout=10
        )
        
        if response.status_code == 200:
            print(f"✅ Successfully sent metrics. Response: {response.text}")
        else:
            print(f"❌ Failed to send metrics. Status: {response.status_code}, Response: {response.text}")
            
    except requests.exceptions.ConnectionError:
        print("❌ Connection error: Make sure the system-monitoring-service is running on port 8082")
    except requests.exceptions.Timeout:
        print("❌ Request timeout")
    except Exception as e:
        print(f"❌ Error sending metrics: {str(e)}")

def test_service_endpoints():
    """Test the service endpoints"""
    base_url = "http://localhost:8082"
    
    endpoints = [
        "/metrics/cpu",
        "/metrics/memory", 
        "/metrics/disk",
        "/metrics/network",
        "/actuator/health"
    ]
    
    print("\n🔍 Testing service endpoints:")
    for endpoint in endpoints:
        try:
            response = requests.get(f"{base_url}{endpoint}", timeout=5)
            print(f"  {endpoint}: {response.status_code}")
        except Exception as e:
            print(f"  {endpoint}: ❌ Error - {str(e)}")

if __name__ == "__main__":
    print("🚀 System Monitoring Agent Demo")
    print("=" * 40)
    
    # Test service endpoints first
    test_service_endpoints()
    
    print("\n📊 Sending sample metrics...")
    send_metrics_to_service()
    
    print("\n💡 To run continuously, use:")
    print("   while true; do python sample_agent.py; sleep 60; done") 