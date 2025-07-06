#!/bin/bash

echo "🔍 Verifying System Monitoring Service Compilation..."
echo "=================================================="

# Clean and compile
echo "📦 Cleaning previous builds..."
mvn clean

echo "🔨 Compiling project..."
mvn compile

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "✅ All Lombok annotations processed correctly"
    echo "✅ All getter/setter methods generated"
    
    echo ""
    echo "🧪 Running tests to verify functionality..."
    mvn test
    
    if [ $? -eq 0 ]; then
        echo "✅ All tests passed!"
        echo "✅ System Monitoring Service is ready to run"
    else
        echo "❌ Some tests failed. Check the output above."
        exit 1
    fi
else
    echo "❌ Compilation failed. Check the errors above."
    exit 1
fi 