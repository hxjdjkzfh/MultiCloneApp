#!/bin/bash

# Создание папки для GitHub Actions
mkdir -p .github/workflows
cp github_actions_android.yml .github/workflows/android.yml

# Настройка Git
git config --global credential.helper store
git config --global user.name "MultiCloneApp"
git config --global user.email "multiclone@example.com"

# Создание файла с токеном
echo "https://github.com:$GITHUB_TOKEN" > ~/.git-credentials

# Инициализация Git
git init
git add .
git commit -m "Initial commit with virtualization components"
git branch -M main

# Добавление удаленного репозитория и пуш
git remote add origin https://github.com/hxjdjkzfh/MultiCloneApp.git
git push -f -u origin main

echo "Операция завершена!"