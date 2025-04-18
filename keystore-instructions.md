# Signing Key Setup for GitHub Actions

To enable the automatic signing of release APKs in GitHub Actions, you need to set up the following repository secrets:

## Required Secrets

1. **SIGNING_KEY**: Base64-encoded keystore file
2. **SIGNING_KEY_ALIAS**: The alias name of your key in the keystore
3. **SIGNING_STORE_PASSWORD**: The password to access the keystore
4. **SIGNING_KEY_PASSWORD**: The password for your key in the keystore

## How to Generate a Keystore and Set Up Secrets

### 1. Generate a Keystore (if you don't have one already)

```bash
keytool -genkey -v -keystore multiclone-keystore.jks -alias multiclone -keyalg RSA -keysize 2048 -validity 10000
```

Follow the prompts to enter your details and passwords.

### 2. Base64 Encode Your Keystore

```bash
openssl base64 -in multiclone-keystore.jks -out multiclone-keystore-base64.txt
```

### 3. Add Secrets to GitHub Repository

1. Go to your GitHub repository
2. Click on "Settings" → "Secrets and variables" → "Actions"
3. Click "New repository secret" and add each of the following:

- Name: `SIGNING_KEY`
  Value: *Contents of multiclone-keystore-base64.txt*
  
- Name: `SIGNING_KEY_ALIAS`
  Value: `multiclone` (or whatever alias you chose)
  
- Name: `SIGNING_STORE_PASSWORD`
  Value: *Your keystore password*
  
- Name: `SIGNING_KEY_PASSWORD`
  Value: *Your key password*

Once these secrets are set up, the GitHub Actions workflow will automatically sign your release APKs.

**IMPORTANT**: Keep your keystore and passwords secure. Never commit them to your repository.