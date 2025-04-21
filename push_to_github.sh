#!/bin/bash

# Создание папки для GitHub Actions
mkdir -p .github/workflows
cp github_actions_android.yml .github/workflows/android.yml

# Настройка Git
git config --global user.name "MultiCloneApp"
git config --global user.email "multiclone@example.com"

# Инициализация Git
git init
git add .
git commit -m "Initial commit with virtualization components"
git branch -M main

# Добавление удаленного репозитория и пуш с токеном
TOKEN="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"
git remote add origin https://${TOKEN}@github.com/hxjdjkzfh/MultiCloneApp.git
git push -f -u origin main

echo "Операция завершена!"