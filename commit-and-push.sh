#!/bin/bash

# Check if the GitHub token is provided as an argument or already set
if [ -n "$1" ]; then
  GITHUB_TOKEN="$1"
elif [ -z "$GITHUB_TOKEN" ]; then
  read -p "Please enter your GitHub token: " GITHUB_TOKEN
  if [ -z "$GITHUB_TOKEN" ]; then
    echo "Error: GitHub token is required."
    exit 1
  fi
fi

# Configure git if not already done
if [ -z "$(git config --get user.email)" ]; then
  echo "Setting up Git configuration..."
  git config --global user.email "multiclone@example.com"
  git config --global user.name "MultiClone Developer"
fi

# Make script executable
chmod +x commit-and-push.sh

# Ask for repository details if not provided as arguments
if [ -n "$2" ]; then
  GITHUB_USERNAME="$2"
else
  read -p "Enter your GitHub username: " GITHUB_USERNAME
  if [ -z "$GITHUB_USERNAME" ]; then
    echo "Error: GitHub username is required."
    exit 1
  fi
fi

if [ -n "$3" ]; then
  REPO_NAME="$3"
else
  read -p "Enter the repository name (default: MultiCloneApp): " REPO_NAME
  REPO_NAME=${REPO_NAME:-MultiCloneApp}
fi

# Create GitHub repository URL with token
REPO_URL="https://${GITHUB_TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git"

# Initialize Git repository if not already done
if [ ! -d .git ]; then
  echo "Initializing Git repository..."
  git init
  git branch -M main
fi

# Stage all files
echo "Staging files..."
git add .

# Ask for custom commit message or use default
if [ -n "$4" ]; then
  COMMIT_MESSAGE="$4"
else
  read -p "Enter commit message (leave empty for default): " CUSTOM_MESSAGE
  if [ -z "$CUSTOM_MESSAGE" ]; then
    COMMIT_MESSAGE="Implement virtualization components and infrastructure

- Created CloneEnvironment for virtual environment management
- Added ClonedAppInstaller for handling app installation and updates
- Enhanced VirtualAppEngine to use the new installer
- Implemented VirtualAppManager and virtualization service
- Updated CloneProxyActivity for proper environment setup
- Fixed repository method inconsistencies
- Added notification resources and service infrastructure
- Configured file provider for secure file sharing between clones"
  else
    COMMIT_MESSAGE="$CUSTOM_MESSAGE"
  fi
fi

# Commit changes
echo "Committing changes..."
git commit -m "$COMMIT_MESSAGE"

# Check if remote exists, if not add it
if ! git remote | grep -q origin; then
  echo "Adding remote origin..."
  git remote add origin "$REPO_URL"
else
  echo "Updating remote origin..."
  git remote set-url origin "$REPO_URL"
fi

# Push changes
echo "Pushing to GitHub..."
git push -u origin main

echo "✅ Changes committed and pushed successfully to ${GITHUB_USERNAME}/${REPO_NAME}!"
echo "Repository URL: https://github.com/${GITHUB_USERNAME}/${REPO_NAME}"