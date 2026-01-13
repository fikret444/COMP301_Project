@echo off
echo ============================================
echo   Car Rental Platform - Test Runner
echo ============================================
echo.

set TOTAL_TESTS=0
set PASSED_TESTS=0
set FAILED_TESTS=0

echo [1/4] Testing User Service...
echo ----------------------------------------
cd user-service
call mvnw.cmd clean test
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] User Service tests passed!
    set /a PASSED_TESTS+=1
) else (
    echo [FAILED] User Service tests failed!
    set /a FAILED_TESTS+=1
)
cd ..
echo.

echo [2/4] Testing Car Service...
echo ----------------------------------------
cd car-service
call mvnw.cmd clean test
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Car Service tests passed!
    set /a PASSED_TESTS+=1
) else (
    echo [FAILED] Car Service tests failed!
    set /a FAILED_TESTS+=1
)
cd ..
echo.

echo [3/4] Testing Rental Service...
echo ----------------------------------------
cd rental-service
call mvnw.cmd clean test
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Rental Service tests passed!
    set /a PASSED_TESTS+=1
) else (
    echo [FAILED] Rental Service tests failed!
    set /a FAILED_TESTS+=1
)
cd ..
echo.

echo [4/4] Testing Payment Service...
echo ----------------------------------------
cd payment-service
call mvnw.cmd clean test
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Payment Service tests passed!
    set /a PASSED_TESTS+=1
) else (
    echo [FAILED] Payment Service tests failed!
    set /a FAILED_TESTS+=1
)
cd ..
echo.

set /a TOTAL_TESTS=PASSED_TESTS+FAILED_TESTS

echo ============================================
echo   TEST SUMMARY
echo ============================================
echo Total Services: %TOTAL_TESTS%
echo Passed: %PASSED_TESTS%
echo Failed: %FAILED_TESTS%
echo.

if %FAILED_TESTS% EQU 0 (
    echo [ALL TESTS PASSED] ^_^
    echo.
    echo Test reports available at:
    echo - user-service/target/surefire-reports/
    echo - car-service/target/surefire-reports/
    echo - rental-service/target/surefire-reports/
    echo - payment-service/target/surefire-reports/
) else (
    echo [SOME TESTS FAILED] Check logs above
)
echo.

pause

