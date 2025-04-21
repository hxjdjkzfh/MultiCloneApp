#!/bin/bash

# Check if GitHub token is provided
if [ -z "$1" ]; then
  echo "Error: GitHub token is required."
  exit 1
fi

GITHUB_TOKEN=$1
GITHUB_USERNAME="hxjdjkzfh"
REPO_NAME="MultiCloneApp"
COMMIT_MESSAGE="Add initial virtualization components"

# Configure Git
git config --global user.name "Replit AI"
git config --global user.email "ai@replit.com"

# Check if .git directory exists, if not initialize the repository
if [ ! -d ".git" ]; then
  echo "Initializing Git repository..."
  git init
  git remote add origin https://${GITHUB_TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git
else
  # Make sure remote is set correctly
  git remote set-url origin https://${GITHUB_TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git
fi

# Create .gitignore if it doesn't exist
if [ ! -f ".gitignore" ]; then
  echo "Creating .gitignore file..."
  cat > .gitignore << EOF
# Built application files
*.apk
*.aar
*.ap_
*.aab

# Files for the ART/Dalvik VM
*.dex

# Java class files
*.class

# Generated files
bin/
gen/
out/

# Gradle files
.gradle/
build/

# Local configuration file (sdk path, etc)
local.properties

# Proguard folder
proguard/

# Log Files
*.log

# Android Studio Navigation editor temp files
.navigation/

# Android Studio captures folder
captures/

# IntelliJ
*.iml
.idea/
.idea/workspace.xml
.idea/tasks.xml
.idea/gradle.xml
.idea/assetWizardSettings.xml
.idea/dictionaries
.idea/libraries
.idea/caches

# Keystore files
*.jks
*.keystore

# External native build folder generated in Android Studio 2.2 and later
.externalNativeBuild
.cxx/

# Google Services (e.g. APIs or Firebase)
google-services.json

# Version control
vcs.xml

# lint
lint/intermediates/
lint/generated/
lint/outputs/
lint/tmp/

# MacOS
.DS_Store

# Replit specific
.replit
.upm/
.config/
EOF
fi

# Create basic project structure if it doesn't exist
mkdir -p app/src/main/java/com/multiclone/app
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/res/values
mkdir -p app/src/androidTest/java/com/multiclone/app
mkdir -p app/src/test/java/com/multiclone/app

# Add all files to git
echo "Adding files to git..."
git add .

# Commit changes
echo "Committing changes..."
git commit -m "$COMMIT_MESSAGE"

# Push to GitHub
echo "Pushing to GitHub..."
git push -u origin master || git push -u origin main

echo "Push completed."