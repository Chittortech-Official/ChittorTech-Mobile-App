# ChittorTech System Architecture & 2FA Setup Documentation

> **Project:** ChittorTech Official Mobile App & Ecosystem  
> **Last Updated:** October 2026  
> **Purpose:** Permanent record of authentication, SMTP, hosting, and cloud infrastructure setup so no details are lost.

---

## 📌 High-Level Architecture Overview

```
                      ┌──────────────────────────────────────────────┐
                      │             CHITTORTECH ECOSYSTEM            │
                      └──────────────────────┬───────────────────────┘
                                             │
      ┌──────────────────────────────────────┼──────────────────────────────────────┐
      │                                      │                                      │
      ▼                                      ▼                                      ▼
[ Android Mobile App ]             [ Official Website ]                  [ 2FA OTP Microservice ]
- Kotlin + Jetpack Compose         - Hosted on Firebase Hosting          - Node.js Serverless on Vercel
- Firebase Firestore & Auth        - Domain: chittortech.in (GoDaddy)    - Account: chittortech@gmail.com
- Dynamic 2FA OTP Flow             - Spark Free Plan                     - Endpoints: /api/send-otp
                                                                                    /api/verify-otp
      │                                      │                                      │
      └──────────────────────────────────────┴──────────────────────────────────────┘
                                             │
                                             ▼
                                  [ Titan Mail SMTP Server ]
                                  - Server: smtp.titan.email:465
                                  - Official Email: business@chittortech.in
```

---

## 1. 📧 Official Email & SMTP Configuration

| Parameter | Value | Notes |
| :--- | :--- | :--- |
| **Official Email** | `business@chittortech.in` | Sender for all 2FA security codes & alerts |
| **SMTP Host** | `smtp.titan.email` | Official Titan Mail server |
| **Port** | `465` (SSL) / `587` (TLS) | Encrypted transport |
| **Authentication** | Username: `business@chittortech.in`<br>Password: Configured in Vercel Secrets | Passwords are never stored in the Android APK |

---

## 2. ⚡ Vercel Serverless OTP Microservice

* **Vercel Account:** `chittortech@gmail.com`
* **Local Source Directory:** `vercel-otp-service/`
* **Deployment URL:** Configurable (e.g. `https://chittortech-otp-service.vercel.app`)

### Environment Variables Configured in Vercel:
| Key | Example / Description |
| :--- | :--- |
| `TITAN_USER` | `business@chittortech.in` |
| `TITAN_PASSWORD` | *(Your Titan Mail inbox/app password)* |
| `OTP_SECRET_KEY` | *(Random 32-character secret for HMAC SHA-256 OTP signing)* |

### Endpoints:
1. `POST /api/send-otp`
   - Accepts `{ email, name, role }`
   - Generates 6-digit random code
   - Dispatches branded HTML email from `business@chittortech.in`
   - Returns tamper-proof cryptographic HMAC token + 5-minute expiry (raw OTP is **never** returned in the response to protect against packet sniffing).
2. `POST /api/verify-otp`
   - Accepts `{ email, otp, token, expiresAt }`
   - Recomputes HMAC signature and performs timing-safe comparison (`crypto.timingSafeEqual`).
   - Returns `{ success: true }` if valid and not expired.

---

## 3. 🔥 Firebase Infrastructure & Hosting

* **Website Hosting:** Firebase Hosting (Free Spark Plan).
* **Domain:** `chittortech.in` registered via GoDaddy, pointing to Firebase Hosting.
* **Database:** Cloud Firestore (shared between website and mobile app).
  * `users/{email}`: User profiles, credentials, and roles (`admin`, `client`, `guest`).
  * `projects/`: Client active projects, phases, milestone progress.
  * `invoices/`: Billing, line items, payment status.
  * `tickets/`: Support requests and resolution logs.
  * `leads/`: Incoming web & app leads (outbound B2B leads kept separate).
  * `settings/auth`: Holds dynamic config like `{ "vercelBaseUrl": "https://your-vercel-domain.vercel.app" }`.

---

## 4. 📱 Android Mobile App Authentication Flow

### Mode 1: Guest Explorer Mode (Storefront / Vyapar)
* **Access:** 1-Tap Instant Entry.
* **Requirement:** No email, password, or OTP needed.
* **Purpose:** Public clients explore services, AI chatbots, and portfolio without barrier.

### Mode 2: Corporate Client Portal
* **Step 1:** Client inputs registered Email + Password.
* **Step 2:** App queries Firestore `users` to verify credentials. If wrong, error shown immediately (no email sent).
* **Step 3:** If credentials match, Vercel triggers 6-digit OTP email from `business@chittortech.in`.
* **Step 4:** Client enters OTP on 6-digit interactive dialog (5-min timer, 30s resend cooldown).
* **Step 5:** App verifies with Vercel and authorizes entry to Client Dashboard.

### Mode 3: Administrator Control Room
* **Step 1:** Admin inputs Admin Email + Password.
* **Step 2:** App verifies `role == "admin"` in Firestore `users`. Non-admins are rejected immediately.
* **Step 3:** 6-digit OTP sent to admin email via Titan Mail.
* **Step 4:** Admin enters OTP $\rightarrow$ Access granted to 5 Admin Tabs (Dashboard, Projects, Invoices, Tickets, Leads).

---

## 5. 🛡️ Security & Anti-Hijack Guarantees

1. **No Stored Passwords in Mobile Code:** Neither the Titan Mail password nor the raw OTP are placed inside Kotlin APK code. Decompiling the APK yields zero server secrets.
2. **Server-Side Validation:** The mobile app cannot bypass the check because the cryptographic HMAC signature requires the server secret key.
3. **5-Minute Expiry (TTL):** Every OTP expires in 300 seconds automatically.
4. **Replay Protection:** Once an OTP is used or expires, the HMAC token becomes invalid.
5. **No Email Spam:** OTPs are only dispatched if credentials match a valid registered account.
