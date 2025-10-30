#!/bin/bash
# Script to clean generated sources before Maven clean
# This helps avoid issues with large directories on macOS

echo "Cleaning generated sources..."

TARGET_DIR="api/target/generated-sources/openapi"

if [ -d "$TARGET_DIR" ]; then
    echo "Removing $TARGET_DIR..."
    rm -rf "$TARGET_DIR"
    echo "Done!"
else
    echo "$TARGET_DIR does not exist, nothing to clean."
fi

exit 0

