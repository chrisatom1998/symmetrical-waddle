#!/bin/bash

# Kotlin code validation script
# Checks for common syntax issues without compiling

echo "========================================"
echo "Kotlin Code Validation"
echo "========================================"
echo ""

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

ERRORS=0
WARNINGS=0

# Find all Kotlin files
KOTLIN_FILES=$(find app/src/main/java -name "*.kt")

for file in $KOTLIN_FILES; do
    echo "Checking: $file"

    # Check package declaration
    if ! grep -q "^package " "$file"; then
        echo -e "  ${RED}✗${NC} Missing package declaration"
        ((ERRORS++))
    else
        echo -e "  ${GREEN}✓${NC} Package declaration present"
    fi

    # Check for balanced braces
    OPEN_BRACES=$(grep -o "{" "$file" | wc -l)
    CLOSE_BRACES=$(grep -o "}" "$file" | wc -l)

    if [ "$OPEN_BRACES" -ne "$CLOSE_BRACES" ]; then
        echo -e "  ${RED}✗${NC} Unbalanced braces (open: $OPEN_BRACES, close: $CLOSE_BRACES)"
        ((ERRORS++))
    else
        echo -e "  ${GREEN}✓${NC} Braces balanced ($OPEN_BRACES pairs)"
    fi

    # Check for balanced parentheses
    OPEN_PARENS=$(grep -o "(" "$file" | wc -l)
    CLOSE_PARENS=$(grep -o ")" "$file" | wc -l)

    if [ "$OPEN_PARENS" -ne "$CLOSE_PARENS" ]; then
        echo -e "  ${YELLOW}⚠${NC} Unbalanced parentheses (open: $OPEN_PARENS, close: $CLOSE_PARENS)"
        ((WARNINGS++))
    else
        echo -e "  ${GREEN}✓${NC} Parentheses balanced ($OPEN_PARENS pairs)"
    fi

    # Check for common keywords
    if grep -q "class\|object\|fun\|val\|var" "$file"; then
        echo -e "  ${GREEN}✓${NC} Contains Kotlin keywords"
    else
        echo -e "  ${YELLOW}⚠${NC} No Kotlin keywords found"
        ((WARNINGS++))
    fi

    # Count lines of code (excluding empty lines and comments)
    LOC=$(grep -v "^\s*$" "$file" | grep -v "^\s*//" | wc -l)
    echo -e "  ${GREEN}ℹ${NC} Lines of code: $LOC"

    echo ""
done

echo "========================================"
echo "Validation Summary"
echo "========================================"

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✓ No syntax errors found!${NC}"
else
    echo -e "${RED}✗ Found $ERRORS potential errors${NC}"
fi

if [ $WARNINGS -gt 0 ]; then
    echo -e "${YELLOW}⚠ Found $WARNINGS warnings${NC}"
fi

echo ""
echo "Code Statistics:"
echo "----------------"
echo "Total Kotlin files: $(echo "$KOTLIN_FILES" | wc -l)"
echo "Total lines of code: $(cat $KOTLIN_FILES | grep -v "^\s*$" | grep -v "^\s*//" | wc -l)"
echo ""

# Display project structure
echo "Project Code Structure:"
echo "----------------------"
echo "Model Layer:"
ls -lh app/src/main/java/com/example/gemmatch/model/*.kt 2>/dev/null | awk '{print "  " $9, "(" $5 ")"}'

echo "Game Logic Layer:"
ls -lh app/src/main/java/com/example/gemmatch/game/*.kt 2>/dev/null | awk '{print "  " $9, "(" $5 ")"}'

echo "UI Layer:"
ls -lh app/src/main/java/com/example/gemmatch/ui/*.kt 2>/dev/null | awk '{print "  " $9, "(" $5 ")"}'

echo ""

exit $ERRORS
