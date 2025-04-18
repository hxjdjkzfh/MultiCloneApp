#!/bin/bash

# This script configures GitHub credentials and pushes to the repo
# Usage: ./setup-github-and-push.sh <github_token> <repo_url> <commit_message>

# Check if all arguments are provided
if [ "$#" -lt 3 ]; then
    echo "Usage: $0 <github_token> <repo_url> <commit_message>"
    echo "Example: $0 ghp_123456789abcdef https://github.com/username/repo.git \"Initial commit\""
    exit 1
fi

TOKEN=$1
REPO_URL=$2
COMMIT_MSG=$3

# Extract the repo parts from URL
REPO_URL_WITH_TOKEN=${REPO_URL/https:\/\//https:\/\/$TOKEN@}

# Configure git
git config --global user.name "Automated Push"
git config --global user.email "automated@example.com"

# Initialize repository if not already done
git init

# Add all files
git add .

# Commit changes
git commit -m "$COMMIT_MSG"

# Add remote if it doesn't exist, or update it if it does
if git remote | grep -q "^origin$"; then
    git remote set-url origin "$REPO_URL_WITH_TOKEN"
else
    git remote add origin "$REPO_URL_WITH_TOKEN"
fi

# Push to GitHub
git push -u origin main

echo "✅ Repository pushed successfully to $REPO_URL"