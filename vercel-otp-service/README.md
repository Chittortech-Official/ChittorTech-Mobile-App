# ChittorTech 2FA OTP Service (Vercel Serverless)

A 100% Free, bank-grade, serverless micro-service that sends one-time verification codes (2FA OTP) using your official domain email **`business@chittortech.in`** via Titan Mail SMTP (`smtp.titan.email:465`).

---

## 🚀 How to Deploy to Vercel (Takes 60 seconds)

### Method 1: Deploy with Vercel CLI (Super Fast)
1. Open PowerShell or Terminal in this folder (`vercel-otp-service`):
   ```bash
   cd vercel-otp-service
   npx vercel
   ```
2. Follow the prompt (press Enter for defaults). It will give you a live production URL like:
   `https://chittortech-otp-service.vercel.app`

### Method 2: Deploy via GitHub / Vercel Dashboard
1. Push this folder to a GitHub repository (e.g. `chittortech-otp-service`).
2. Go to [vercel.com](https://vercel.com) and click **"Add New Project"**.
3. Import your GitHub repo and click **Deploy**.

---

## 🔑 Environment Variables to Set in Vercel

In your Vercel Project Dashboard $\rightarrow$ **Settings** $\rightarrow$ **Environment Variables**, add the following:

| Variable Name | Value | Description |
| :--- | :--- | :--- |
| `TITAN_USER` | `business@chittortech.in` | Your Titan Mail address |
| `TITAN_PASSWORD` | `<your-titan-mail-password>` | Your Titan Mail inbox password |
| `OTP_SECRET_KEY` | *(Any random 32-character secret)* | Used to cryptographically sign OTP tokens |

---

## 📡 API Endpoints

### 1. Send OTP: `POST /api/send-otp`
**Request Body (JSON):**
```json
{
  "email": "client@example.com",
  "name": "Aman Verma",
  "role": "CLIENT"
}
```
**Response (JSON):**
```json
{
  "success": true,
  "token": "4a7b8e...",
  "expiresAt": 1728059400000,
  "message": "Verification code sent to client@example.com"
}
```

### 2. Verify OTP: `POST /api/verify-otp`
**Request Body (JSON):**
```json
{
  "email": "client@example.com",
  "otp": "482910",
  "token": "4a7b8e...",
  "expiresAt": 1728059400000
}
```
**Response (JSON):**
```json
{
  "success": true,
  "message": "Verification successful. Session authorized."
}
```
