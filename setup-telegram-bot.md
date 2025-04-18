# Setting Up Telegram Bot for APK Delivery

This guide will help you set up a Telegram bot to receive APK builds from the GitHub Actions workflow.

## Step 1: Create a Telegram Bot

1. Open Telegram and search for "BotFather" (@BotFather)
2. Start a conversation with BotFather by clicking "Start"
3. Send the command `/newbot`
4. Follow the prompts to:
   - Set a name for your bot (e.g., "MultiClone Build Bot")
   - Choose a username for your bot (must end with "bot", e.g., "multiclone_build_bot")
5. After successful creation, BotFather will provide your bot token. **Save this token securely** - you'll need it for GitHub Actions.

## Step 2: Get Your Telegram Chat ID

1. Start a conversation with your newly created bot
2. Send a message to the bot (any message will do)
3. Open a web browser and go to: `https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates`
   - Replace `<YOUR_BOT_TOKEN>` with the token from BotFather
4. Look for the `"chat":{"id":` value in the JSON response - this is your Chat ID
   - It will be a numeric value like `123456789`

## Step 3: Add Secrets to GitHub Repository

1. Go to your GitHub repository
2. Navigate to Settings → Secrets and variables → Actions
3. Add the following repository secrets:
   - `TELEGRAM_TOKEN`: Your bot token from BotFather
   - `TELEGRAM_TO`: Your chat ID from step 2

## Step 4: Configure Android Signing Keys

For signing the APK, you'll also need to add these secrets:
- `SIGNING_KEY`: Base64-encoded keystore file
- `SIGNING_KEY_ALIAS`: Keystore alias
- `SIGNING_STORE_PASSWORD`: Keystore password
- `SIGNING_KEY_PASSWORD`: Key password

## That's it!

Now when you push to the main branch or manually run the workflow, your signed APK will be built and sent to your Telegram bot automatically.