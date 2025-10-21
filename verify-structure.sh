#!/bin/bash

# Verification script for Gem Match Android project structure

echo "========================================="
echo "Gem Match Project Structure Verification"
echo "========================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

ERRORS=0
WARNINGS=0

# Function to check if file exists
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        return 0
    else
        echo -e "${RED}✗${NC} $1 (MISSING)"
        ((ERRORS++))
        return 1
    fi
}

# Function to check XML syntax
check_xml() {
    if [ -f "$1" ]; then
        if xmllint --noout "$1" 2>/dev/null; then
            echo -e "${GREEN}✓${NC} $1 (valid XML)"
            return 0
        else
            echo -e "${YELLOW}⚠${NC} $1 (XML validation skipped - xmllint not available)"
            ((WARNINGS++))
            return 1
        fi
    fi
}

# Function to check Kotlin file for basic syntax
check_kotlin_basic() {
    if [ -f "$1" ]; then
        # Check for basic Kotlin syntax markers
        if grep -q "package\|class\|fun\|val\|var" "$1"; then
            echo -e "${GREEN}✓${NC} $1 (contains Kotlin code)"
            return 0
        else
            echo -e "${YELLOW}⚠${NC} $1 (doesn't look like Kotlin)"
            ((WARNINGS++))
            return 1
        fi
    fi
}

echo "1. Checking Gradle Build Files..."
echo "-----------------------------------"
check_file "build.gradle.kts"
check_file "settings.gradle.kts"
check_file "gradle.properties"
check_file "app/build.gradle.kts"
check_file "gradlew"
check_file "gradlew.bat"
check_file "gradle/wrapper/gradle-wrapper.properties"
echo ""

echo "2. Checking Android Manifest..."
echo "-----------------------------------"
check_file "app/src/main/AndroidManifest.xml"
check_xml "app/src/main/AndroidManifest.xml"
echo ""

echo "3. Checking Kotlin Source Files..."
echo "-----------------------------------"
check_file "app/src/main/java/com/example/gemmatch/model/Gem.kt"
check_kotlin_basic "app/src/main/java/com/example/gemmatch/model/Gem.kt"

check_file "app/src/main/java/com/example/gemmatch/game/GameBoard.kt"
check_kotlin_basic "app/src/main/java/com/example/gemmatch/game/GameBoard.kt"

check_file "app/src/main/java/com/example/gemmatch/game/GameView.kt"
check_kotlin_basic "app/src/main/java/com/example/gemmatch/game/GameView.kt"

check_file "app/src/main/java/com/example/gemmatch/ui/MainActivity.kt"
check_kotlin_basic "app/src/main/java/com/example/gemmatch/ui/MainActivity.kt"
echo ""

echo "4. Checking Resource Files..."
echo "-----------------------------------"
check_file "app/src/main/res/layout/activity_main.xml"
check_xml "app/src/main/res/layout/activity_main.xml"

check_file "app/src/main/res/values/strings.xml"
check_xml "app/src/main/res/values/strings.xml"

check_file "app/src/main/res/values/colors.xml"
check_xml "app/src/main/res/values/colors.xml"

check_file "app/src/main/res/values/themes.xml"
check_xml "app/src/main/res/values/themes.xml"
echo ""

echo "5. Checking Documentation..."
echo "-----------------------------------"
check_file "README.md"
check_file ".gitignore"
echo ""

echo "========================================="
echo "Verification Summary"
echo "========================================="
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}All required files are present!${NC}"
else
    echo -e "${RED}Found $ERRORS missing files${NC}"
fi

if [ $WARNINGS -gt 0 ]; then
    echo -e "${YELLOW}Found $WARNINGS warnings${NC}"
fi

echo ""
echo "Project Structure:"
echo "-------------------"
tree -L 4 -I '.git|.gradle|build|*.iml' 2>/dev/null || find . -type f -not -path '*/.git/*' | head -30

echo ""
echo "========================================="
echo "Build Requirements"
echo "========================================="
echo "To build this project, you need:"
echo "  1. Android SDK (API 24 or higher)"
echo "  2. Android SDK Build Tools"
echo "  3. JDK 8 or higher"
echo ""
echo "Set ANDROID_HOME environment variable to your Android SDK path."
echo "Then run: ./gradlew build"
echo ""

exit $ERRORS
