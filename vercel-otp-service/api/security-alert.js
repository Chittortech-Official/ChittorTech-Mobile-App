const nodemailer = require('nodemailer');

module.exports = async (req, res) => {
  // Handle CORS Preflight
  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ success: false, message: 'Method Not Allowed' });
  }

  try {
    const { attemptedEmail, reason, platform } = req.body || {};

    const cleanEmail = (attemptedEmail || 'Unknown').trim();
    const failureReason = (reason || 'Incorrect Master Access Key').trim();
    const appPlatform = (platform || 'ChittorTech Official Android Mobile App').trim();

    // Configure GoDaddy SMTP Transporter
    const smtpUser = (process.env.TITAN_USER || process.env.SMTP_USER || 'business@chittortech.in').trim();
    const smtpPass = (process.env.TITAN_PASSWORD || process.env.SMTP_PASSWORD || '').trim();
    const smtpHost = (process.env.SMTP_HOST || 'smtpout.secureserver.net').trim();
    const smtpPort = Number(process.env.SMTP_PORT) || 465;

    if (!smtpPass) {
      console.warn('Mail password is not configured.');
      return res.status(500).json({
        success: false,
        message: 'Mail password not configured.'
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

    // Alert Recipients: Kush Sharma & Lav Sharma
    const alertRecipients = ['kushsharma.cor@gmail.com', 'lavsharma.cor@gmail.com'];

    const htmlAlert = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>ChittorTech Security Alert</title>
      <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 0; }
        .wrapper { width: 100%; max-width: 580px; margin: 30px auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(220,38,38,0.12); border: 1px solid #fee2e2; }
        .header { background: linear-gradient(135deg, #b91c1c 0%, #dc2626 50%, #991b1b 100%); padding: 32px 24px; text-align: center; color: #ffffff; }
        .header h1 { margin: 0; font-size: 22px; font-weight: 800; letter-spacing: 0.5px; }
        .header p { margin: 6px 0 0 0; font-size: 13px; color: rgba(255,255,255,0.9); }
        .content { padding: 32px 28px; }
        .alert-badge { display: inline-block; background: #fef2f2; border: 1px solid #fecaca; color: #dc2626; font-size: 11px; font-weight: 800; letter-spacing: 1px; padding: 6px 12px; border-radius: 20px; text-transform: uppercase; margin-bottom: 16px; }
        .title { font-size: 18px; font-weight: 800; color: #0f172a; margin-bottom: 10px; }
        .desc { font-size: 13.5px; color: #475569; line-height: 22px; margin-bottom: 22px; }
        .table-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px; margin-bottom: 24px; }
        .table-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #edf2f7; font-size: 13px; }
        .table-row:last-child { border-bottom: none; }
        .label { color: #64748b; font-weight: 600; width: 35%; }
        .val { color: #0f172a; font-weight: 700; width: 65%; text-align: right; word-break: break-all; }
        .danger-val { color: #dc2626; font-weight: 800; width: 65%; text-align: right; }
        .warning-card { background: #fffbeb; border-left: 4px solid #f59e0b; padding: 14px 16px; border-radius: 8px; font-size: 12.5px; color: #92400e; line-height: 20px; margin-bottom: 24px; }
        .footer { background: #f8fafc; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0; font-size: 12px; color: #64748b; }
      </style>
    </head>
    <body>
      <div class="wrapper">
        <div class="header">
          <h1>🚨 SECURITY ALERT</h1>
          <p>ChittorTech Administrator Access Watchdog</p>
        </div>
        <div class="content">
          <div class="alert-badge">Unauthorized Attempt Blocked</div>
          <div class="title">Failed Admin Sign-In Attempt Detected</div>
          <div class="desc">
            A login attempt to the <strong>ChittorTech Administrator Portal</strong> failed authentication on the <strong>Official Mobile App</strong>. Access was denied immediately.
          </div>

          <div class="table-box">
            <div class="table-row">
              <span class="label">Target Platform:</span>
              <span class="val">${appPlatform}</span>
            </div>
            <div class="table-row">
              <span class="label">Attempted Email:</span>
              <span class="danger-val">${cleanEmail}</span>
            </div>
            <div class="table-row">
              <span class="label">Status:</span>
              <span class="danger-val">ACCESS DENIED (${failureReason})</span>
            </div>
            <div class="table-row">
              <span class="label">Timestamp:</span>
              <span class="val">${nowIST}</span>
            </div>
          </div>

          <div class="warning-card">
            <strong>Action Recommended:</strong> If neither Kush Sharma nor Lav Sharma initiated this attempt, an unauthorized party may be attempting to guess the admin credentials. No OTP was issued.
          </div>
        </div>
        <div class="footer">
          &copy; 2026 ChittorTech (chittortech.in) · Automated Security Daemon<br>
          Alert delivered to verified founders: kushsharma.cor@gmail.com & lavsharma.cor@gmail.com
        </div>
      </div>
    </body>
    </html>
    `;

    // Send Alert to Both Founders
    await transporter.sendMail({
      from: `"ChittorTech Security Alert" <${smtpUser}>`,
      to: alertRecipients.join(', '),
      subject: `🚨 [SECURITY ALERT] Failed Admin Login Attempt on ChittorTech Mobile App (${cleanEmail})`,
      text: `SECURITY ALERT: Failed admin login attempt on ChittorTech Mobile App for ${cleanEmail} at ${nowIST}. Reason: ${failureReason}.`,
      html: htmlAlert
    });

    return res.status(200).json({
      success: true,
      message: 'Security intrusion alert sent to founders.'
    });
  } catch (error) {
    console.error('Error sending security alert:', error);
    return res.status(500).json({
      success: false,
      message: error.message || 'Failed to dispatch security alert.'
    });
  }
};
