#/*
# * Copyright 2014 Stormpath, Inc.
# * Modifications Copyright 2018 Okta, Inc.
# *
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# *
# *     http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# */
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

