#!/bin/bash

# Configure git if not already done
if [ -z "$(git config --get user.email)" ]; then
  git config --global user.email "multiclone@example.com"
  git config --global user.name "MultiClone Developer"
fi

# Make script executable
chmod +x commit-and-push.sh

# Check if we have the GitHub token
if [ -z "$GITHUB_TOKEN" ]; then
  echo "Error: GitHub token not found. Please set the GITHUB_TOKEN environment variable."
  exit 1
fi

# Create GitHub repository URL with token
REPO_URL="https://${GITHUB_TOKEN}@github.com/user/multicloneapp.git"

# Initialize Git repository if not already done
if [ ! -d .git ]; then
  git init
  git branch -M main
fi

# Stage all files
git add .

# Commit changes
git commit -m "Implement virtualization components and infrastructure

- Created CloneEnvironment for virtual environment management
- Added ClonedAppInstaller for handling app installation and updates
- Enhanced VirtualAppEngine to use the new installer
- Implemented VirtualAppManager and virtualization service
- Updated CloneProxyActivity for proper environment setup
- Fixed repository method inconsistencies
- Added notification resources and service infrastructure
- Configured file provider for secure file sharing between clones"

# Check if remote exists, if not add it
if ! git remote | grep -q origin; then
  git remote add origin "$REPO_URL"
fi

# Push changes
git push -u origin main

echo "Changes committed and pushed successfully!"