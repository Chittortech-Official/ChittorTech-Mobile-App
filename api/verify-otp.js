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
    const { email, otp, token, expiresAt } = req.body || {};

    if (!email || !otp || !token || !expiresAt) {
      return res.status(400).json({
        success: false,
        message: 'Missing verification parameters (email, otp, token, expiresAt).'
      });
    }

    const cleanEmail = email.trim().toLowerCase();
    const cleanOtp = otp.toString().trim();
    const expiryTime = Number(expiresAt);

    // 1. Check expiration
    if (Date.now() > expiryTime) {
      return res.status(400).json({
        success: false,
        code: 'OTP_EXPIRED',
        message: 'The verification code has expired (validity is 5 minutes). Please request a new code.'
      });
    }

    // 2. Validate format (6 numeric digits)
    if (!/^\d{6}$/.test(cleanOtp)) {
      return res.status(400).json({
        success: false,
        code: 'INVALID_FORMAT',
        message: 'Please enter a valid 6-digit code.'
      });
    }

    // 3. Recompute cryptographic HMAC
    const secret = process.env.OTP_SECRET_KEY || 'ChittorTech_Secure_Auth_Salt_2026_Titan';
    const expectedToken = crypto
      .createHmac('sha256', secret)
      .update(`${cleanEmail}:${cleanOtp}:${expiryTime}`)
      .digest('hex');

    // 4. Timing-safe comparison to prevent timing attacks
    const bufferA = Buffer.from(token, 'hex');
    const bufferB = Buffer.from(expectedToken, 'hex');

    if (bufferA.length !== bufferB.length || !crypto.timingSafeEqual(bufferA, bufferB)) {
      return res.status(400).json({
        success: false,
        code: 'OTP_MISMATCH',
        message: 'Invalid verification code. Please check your email and try again.'
      });
    }

    // 5. Success
    return res.status(200).json({
      success: true,
      message: 'Verification successful. Session authorized.'
    });
  } catch (error) {
    console.error('Error verifying OTP:', error);
    return res.status(500).json({
      success: false,
      message: 'Server error during verification. Please try again.'
    });
  }
};
