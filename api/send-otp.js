const nodemailer = require('nodemailer');
const crypto = require('crypto');

module.exports = async (req, res) => {
  // Handle CORS Preflight
  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ success: false, message: 'Method Not Allowed' });
  }

  try {
    const { email, name, role, platform } = req.body || {};

    if (!email || typeof email !== 'string' || !email.includes('@')) {
      return res.status(400).json({ success: false, message: 'A valid email address is required.' });
    }

    const cleanEmail = email.trim().toLowerCase();
    const recipientName = name ? name.trim() : cleanEmail.split('@')[0];
    const userRole = (role || 'client').toUpperCase();
    const isAdmin = userRole === 'ADMIN';
    const appPlatform = (platform || 'ChittorTech Official Android Mobile App').trim();

    // 1. Generate 6-digit cryptographically secure OTP
    const otp = crypto.randomInt(100000, 999999).toString();

    // 2. Set Expiry (5 minutes from now)
    const expiresAt = Date.now() + 5 * 60 * 1000;

    // 3. Cryptographic HMAC Token (Tamper-proof signature)
    const secret = process.env.OTP_SECRET_KEY || 'ChittorTech_Secure_Auth_Salt_2026_Titan';
    const token = crypto
      .createHmac('sha256', secret)
      .update(`${cleanEmail}:${otp}:${expiresAt}`)
      .digest('hex');

    // 4. Configure SMTP Transporter (GoDaddy / Titan compatible)
    const smtpUser = (process.env.TITAN_USER || process.env.SMTP_USER || 'business@chittortech.in').trim();
    const smtpPass = (process.env.TITAN_PASSWORD || process.env.SMTP_PASSWORD || '').trim();
    const smtpHost = (process.env.SMTP_HOST || 'smtpout.secureserver.net').trim();
    const smtpPort = Number(process.env.SMTP_PORT) || 465;

    if (!smtpPass) {
      console.warn('Mail password is not configured.');
      return res.status(500).json({
        success: false,
        message: 'Mail password not configured in Vercel environment variables.'
      });
    }

    const transporter = nodemailer.createTransport({
      host: smtpHost,
      port: smtpPort,
      secure: smtpPort === 465,
      auth: {
        user: smtpUser,
        pass: smtpPass
      },
      tls: {
        rejectUnauthorized: false
      }
    });

    const nowIST = new Date().toLocaleString('en-IN', {
      timeZone: 'Asia/Kolkata',
      dateStyle: 'full',
      timeStyle: 'medium'
    });

    // 5. Authentic ChittorTech HTML Email Template (Tailored for Mobile App)
    const headerBg = isAdmin 
      ? 'linear-gradient(135deg, #0f172a 0%, #1e293b 60%, #334155 100%)' 
      : 'linear-gradient(135deg, #0284C7 0%, #0369A1 100%)';
    const accentColor = isAdmin ? '#d97706' : '#0284C7';
    const portalName = isAdmin ? 'Administrator Control Room' : 'Corporate Client Portal';
    const badgeText = isAdmin ? '🛡️ MOBILE APP · ADMIN CONTROL ROOM' : '💼 MOBILE APP · CLIENT PORTAL';

    const htmlTemplate = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>ChittorTech Security Verification</title>
      <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 0; }
        .wrapper { width: 100%; max-width: 540px; margin: 30px auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; }
        .header { background: ${headerBg}; padding: 32px 24px; text-align: center; color: #ffffff; }
        .header h1 { margin: 0; font-size: 22px; font-weight: 800; letter-spacing: 0.5px; }
        .header p { margin: 6px 0 0 0; font-size: 12.5px; color: rgba(255,255,255,0.85); }
        .content { padding: 30px 28px; }
        .badge { display: inline-block; background: ${isAdmin ? '#fef3c7' : '#e0f2fe'}; border: 1px solid ${isAdmin ? '#fde68a' : '#bae6fd'}; color: ${isAdmin ? '#b45309' : '#0369a1'}; font-size: 11px; font-weight: 800; letter-spacing: 0.8px; padding: 6px 12px; border-radius: 20px; text-transform: uppercase; margin-bottom: 16px; }
        .greeting { font-size: 16px; font-weight: 700; color: #0f172a; margin-bottom: 8px; }
        .desc { font-size: 13.5px; color: #475569; line-height: 22px; margin-bottom: 20px; }
        .info-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 12px 14px; margin-bottom: 22px; font-size: 12.5px; }
        .info-row { display: flex; justify-content: space-between; padding: 4px 0; }
        .info-label { color: #64748b; font-weight: 600; }
        .info-val { color: #0f172a; font-weight: 700; }
        .otp-box { background: #ffffff; border: 2px dashed ${accentColor}; border-radius: 12px; padding: 20px 10px; margin: 18px 0; text-align: center; }
        .otp-code { font-size: 38px; font-weight: 800; letter-spacing: 8px; color: ${accentColor}; font-family: 'Courier New', Courier, monospace; }
        .expiry-tag { display: inline-block; margin-top: 8px; font-size: 12px; color: #dc2626; font-weight: 700; }
        .security-warning { background: #fffbeb; border-left: 4px solid #f59e0b; padding: 12px 14px; font-size: 12px; color: #92400e; border-radius: 6px; margin-top: 22px; line-height: 18px; }
        .footer { background: #f8fafc; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0; font-size: 11.5px; color: #64748b; }
      </style>
    </head>
    <body>
      <div class="wrapper">
        <div class="header">
          <h1>CHITTORTECH SECURITY</h1>
          <p>Two-Factor Authentication · ${portalName}</p>
        </div>
        <div class="content">
          <div class="badge">${badgeText}</div>
          <div class="greeting">Hello ${recipientName},</div>
          <div class="desc">
            A sign-in request was initiated for your account on the <strong>${appPlatform}</strong>. Use the one-time security code below to authorize your session.
          </div>

          <table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; margin-bottom: 22px; border-collapse: separate; overflow: hidden;">
            <tr>
              <td style="padding: 10px 14px; border-bottom: 1px solid #edf2f7; font-size: 13px; color: #64748b; font-weight: 600; width: 36%;">Application:</td>
              <td style="padding: 10px 14px; border-bottom: 1px solid #edf2f7; font-size: 13px; color: #0f172a; font-weight: 700; text-align: right;">${appPlatform}</td>
            </tr>
            <tr>
              <td style="padding: 10px 14px; border-bottom: 1px solid #edf2f7; font-size: 13px; color: #64748b; font-weight: 600; width: 36%;">Target Portal:</td>
              <td style="padding: 10px 14px; border-bottom: 1px solid #edf2f7; font-size: 13px; color: #0f172a; font-weight: 700; text-align: right;">${portalName}</td>
            </tr>
            <tr>
              <td style="padding: 10px 14px; font-size: 13px; color: #64748b; font-weight: 600; width: 36%;">Timestamp:</td>
              <td style="padding: 10px 14px; font-size: 13px; color: #0f172a; font-weight: 700; text-align: right;">${nowIST}</td>
            </tr>
          </table>

          <div class="otp-box">
            <div class="otp-code">${otp}</div>
            <div class="expiry-tag">⏳ Valid for 5 minutes only</div>
          </div>

          <div class="security-warning">
            <strong>Security Notice:</strong> Never share this OTP with anyone. ChittorTech administrators and engineers will never request this code. If you did not initiate this login from the mobile app, secure your account immediately.
          </div>
        </div>
        <div class="footer">
          &copy; 2026 ChittorTech (chittortech.in) · All rights reserved.<br>
          Authorized security dispatch from business@chittortech.in
        </div>
      </div>
    </body>
    </html>
    `;

    const subjectLine = isAdmin
      ? `🛡️ [Admin Access] ChittorTech Mobile App - OTP: ${otp}`
      : `🔐 [Client Portal] ChittorTech Mobile App - OTP: ${otp}`;

    // 6. Send the Email
    await transporter.sendMail({
      from: `"ChittorTech Security" <${smtpUser}>`,
      to: cleanEmail,
      subject: subjectLine,
      text: `Your ChittorTech one-time security code for ${appPlatform} is ${otp}. Valid for 5 minutes. Do not share this code.`,
      html: htmlTemplate
    });

    // 7. Return Response (DO NOT include the raw OTP!)
    return res.status(200).json({
      success: true,
      token,
      expiresAt,
      message: `Verification code sent to ${cleanEmail}`
    });
  } catch (error) {
    console.error('Error dispatching OTP email:', error);
    return res.status(500).json({
      success: false,
      message: error.message || 'Failed to dispatch verification email via GoDaddy SMTP.'
    });
  }
};
