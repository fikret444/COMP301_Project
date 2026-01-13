#!/bin/bash

echo "============================================"
echo "  Car Rental Platform - Test Runner"
echo "============================================"
echo ""

TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to run tests for a service
run_service_test() {
    SERVICE_NAME=$1
    echo "[$2/4] Testing $SERVICE_NAME..."
    echo "----------------------------------------"
    
    cd $SERVICE_NAME
    ./mvnw clean test
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}[SUCCESS]${NC} $SERVICE_NAME tests passed!"
        ((PASSED_TESTS++))
    else
        echo -e "${RED}[FAILED]${NC} $SERVICE_NAME tests failed!"
        ((FAILED_TESTS++))
    fi
    
    cd ..
    echo ""
}

# Run tests for all services
run_service_test "user-service" 1
run_service_test "car-service" 2
run_service_test "rental-service" 3
run_service_test "payment-service" 4

TOTAL_TESTS=$((PASSED_TESTS + FAILED_TESTS))

echo "============================================"
echo "  TEST SUMMARY"
echo "============================================"
echo "Total Services: $TOTAL_TESTS"
echo "Passed: $PASSED_TESTS"
echo "Failed: $FAILED_TESTS"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}[ALL TESTS PASSED]${NC} ✓"
    echo ""
    echo "Test reports available at:"
    echo "- user-service/target/surefire-reports/"
    echo "- car-service/target/surefire-reports/"
    echo "- rental-service/target/surefire-reports/"
    echo "- payment-service/target/surefire-reports/"
    exit 0
else
    echo -e "${RED}[SOME TESTS FAILED]${NC} Check logs above"
    exit 1
fi

