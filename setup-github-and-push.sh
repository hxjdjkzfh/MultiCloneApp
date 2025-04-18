#!/bin/bash

# Define colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}MultiClone App GitHub Setup Script${NC}"
echo "This script will set up a GitHub repository and push the code."
echo

# Check if git is installed
if ! command -v git &> /dev/null; then
    echo -e "${RED}Error: Git is not installed. Please install git first.${NC}"
    exit 1
fi

# Get GitHub username
read -p "Enter your GitHub username: " GITHUB_USERNAME

# Create a new repository on GitHub
echo -e "\n${YELLOW}Creating a new repository on GitHub...${NC}"
read -p "Enter a name for your repository (default: multiclone-app): " REPO_NAME
REPO_NAME=${REPO_NAME:-multiclone-app}

echo -e "\n${YELLOW}Initializing local git repository...${NC}"
git init
git add .
git commit -m "Initial commit for MultiClone App"

# Add GitHub as remote and push
echo -e "\n${YELLOW}Adding GitHub as remote and pushing...${NC}"
git remote add origin https://github.com/$GITHUB_USERNAME/$REPO_NAME.git
git branch -M main
git push -u origin main

echo -e "\n${GREEN}Code has been pushed to GitHub!${NC}"
echo -e "Repository URL: https://github.com/$GITHUB_USERNAME/$REPO_NAME"

# Instructions for setting up GitHub Secrets for signing
echo -e "\n${YELLOW}To enable APK signing in GitHub Actions, add the following secrets in your repository:${NC}"
echo -e "1. ${GREEN}SIGNING_KEY${NC} - Base64-encoded keystore file"
echo -e "2. ${GREEN}SIGNING_KEY_ALIAS${NC} - Keystore alias"
echo -e "3. ${GREEN}SIGNING_STORE_PASSWORD${NC} - Keystore password"
echo -e "4. ${GREEN}SIGNING_KEY_PASSWORD${NC} - Key password"

echo -e "\n${YELLOW}Instructions for generating a keystore:${NC}"
echo "keytool -genkey -v -keystore multiclone.keystore -alias multiclone -keyalg RSA -keysize 2048 -validity 10000"

echo -e "\n${YELLOW}To convert the keystore to base64 for GitHub secrets:${NC}"
echo "openssl base64 < multiclone.keystore | tr -d '\n' | tee multiclone.keystore.base64.txt"

echo -e "\n${GREEN}Setup complete!${NC}"
echo "The GitHub Actions workflow will build and sign your APK when you push changes to main."
echo "Find built APKs in the 'Actions' tab of your GitHub repository."