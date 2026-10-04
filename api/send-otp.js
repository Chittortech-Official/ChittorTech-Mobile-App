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
    const { email, name, role } = req.body || {};

    if (!email || typeof email !== 'string' || !email.includes('@')) {
      return res.status(400).json({ success: false, message: 'A valid email address is required.' });
    }

    const cleanEmail = email.trim().toLowerCase();
    const recipientName = name ? name.trim() : cleanEmail.split('@')[0];
    const userRole = (role || 'client').toUpperCase();

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

    // 5. ChittorTech Branded HTML Email Template
    const htmlTemplate = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>ChittorTech Security Verification</title>
      <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f4f7fa; margin: 0; padding: 0; }
        .wrapper { width: 100%; max-width: 540px; margin: 30px auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; }
        .header { background: linear-gradient(135deg, #0284C7 0%, #0369A1 100%); padding: 32px 24px; text-align: center; color: #ffffff; }
        .header h1 { margin: 0; font-size: 24px; font-weight: 800; letter-spacing: 0.5px; }
        .header p { margin: 6px 0 0 0; font-size: 13px; color: rgba(255,255,255,0.85); }
        .content { padding: 32px 28px; text-align: center; }
        .greeting { font-size: 16px; font-weight: 600; color: #0f172a; margin-bottom: 8px; text-align: left; }
        .desc { font-size: 14px; color: #475569; line-height: 22px; text-align: left; margin-bottom: 24px; }
        .otp-box { background: #f8fafc; border: 2px dashed #0284C7; border-radius: 12px; padding: 20px 10px; margin: 24px 0; text-align: center; }
        .otp-code { font-size: 36px; font-weight: 800; letter-spacing: 8px; color: #0284C7; font-family: 'Courier New', Courier, monospace; }
        .expiry-tag { display: inline-block; margin-top: 8px; font-size: 12px; color: #dc2626; font-weight: 600; }
        .security-warning { background: #fffbeb; border-left: 4px solid #f59e0b; padding: 12px 14px; text-align: left; font-size: 12px; color: #92400e; border-radius: 6px; margin-top: 24px; line-height: 18px; }
        .footer { background: #f8fafc; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0; font-size: 12px; color: #64748b; }
      </style>
    </head>
    <body>
      <div class="wrapper">
        <div class="header">
          <h1>ChittorTech Security</h1>
          <p>Two-Factor Authentication · ${userRole} Portal</p>
        </div>
        <div class="content">
          <div class="greeting">Hello ${recipientName},</div>
          <div class="desc">
            We received a sign-in request for your ChittorTech account. Use the one-time verification code below to complete your authentication.
          </div>

          <div class="otp-box">
            <div class="otp-code">${otp}</div>
            <div class="expiry-tag">Valid for 5 minutes only</div>
          </div>

          <div class="security-warning">
            <strong>Security Notice:</strong> Never share this OTP with anyone. ChittorTech engineers and administrators will never contact you asking for this code. If you did not attempt to log in, please secure your account immediately.
          </div>
        </div>
        <div class="footer">
          &copy; 2026 ChittorTech (chittortech.in) · All rights reserved.<br>
          Automated security dispatch from business@chittortech.in
        </div>
      </div>
    </body>
    </html>
    `;

    // 6. Send the Email
    await transporter.sendMail({
      from: `"ChittorTech Security" <${smtpUser}>`,
      to: cleanEmail,
      subject: `Your ChittorTech Security Code: ${otp}`,
      text: `Your ChittorTech one-time security code is ${otp}. Valid for 5 minutes. Do not share this code.`,
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
      message: error.message || 'Failed to dispatch verification email via Titan Mail.'
    });
  }
};
