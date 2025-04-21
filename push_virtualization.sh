#!/bin/bash

# Check if GitHub token is provided
if [ -z "$1" ]; then
  echo "Error: GitHub token is required."
  exit 1
fi

GITHUB_TOKEN=$1
GITHUB_USERNAME="hxjdjkzfh"
REPO_NAME="MultiCloneApp"
COMMIT_MESSAGE="Add complete project with virtualization components"

# Configure Git
git config --global user.name "Replit AI"
git config --global user.email "ai@replit.com"

# Make sure remote is set correctly
git remote set-url origin https://${GITHUB_TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git

# Create a new branch
echo "Creating new branch for full project..."
git checkout -b complete-project

# Create .gitignore to exclude Android SDK
echo "Creating proper .gitignore..."
cat > .gitignore << EOF
# Gradle files
.gradle/
build/

# Local configuration file (sdk path, etc)
local.properties

# Android Studio
.idea/
*.iml
.DS_Store
captures/

# Native build
.externalNativeBuild
.cxx/

# Generated files
bin/
gen/
out/

# Android SDK
/sdk/
/ndk/
EOF

# Add the entire project
echo "Adding the entire project..."
git add .

# Commit changes
echo "Committing changes..."
git commit -m "$COMMIT_MESSAGE"

# Push the new branch to GitHub
echo "Pushing full project to GitHub..."
git push -u origin complete-project

echo "Push completed. Now you can create a Pull Request on GitHub to merge these changes."