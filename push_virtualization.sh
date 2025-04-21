#!/bin/bash

# Check if GitHub token is provided
if [ -z "$1" ]; then
  echo "Error: GitHub token is required."
  exit 1
fi

GITHUB_TOKEN=$1
GITHUB_USERNAME="hxjdjkzfh"
REPO_NAME="MultiCloneApp"
COMMIT_MESSAGE="Add virtualization components"

# Configure Git
git config --global user.name "Replit AI"
git config --global user.email "ai@replit.com"

# Make sure remote is set correctly
git remote set-url origin https://${GITHUB_TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git

# Create a new branch
echo "Creating new branch for virtualization components..."
git checkout -b virtualization-feature

# Add only the virtualization components and models
echo "Adding virtualization files..."
git add app/src/main/java/com/multiclone/app/virtualization/
git add app/src/main/java/com/multiclone/app/domain/models/
git add .gitignore

# Commit changes
echo "Committing changes..."
git commit -m "$COMMIT_MESSAGE"

# Push the new branch to GitHub
echo "Pushing new branch to GitHub..."
git push -u origin virtualization-feature

echo "Push completed. Now you can create a Pull Request on GitHub to merge these changes."