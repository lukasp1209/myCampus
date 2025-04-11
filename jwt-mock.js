// Simple JWT Generator in JavaScript
function generateJWT(email, role, secret = 'your-256-bit-secret') {
    // Header
    const header = {
        alg: 'HS256',
        typ: 'JWT'
    };

    // Payload (claims)
    const payload = {
        email: email,
        role: role,
        iat: Math.floor(Date.now() / 1000), // Issued at (current time)
        exp: Math.floor(Date.now() / 1000) + (7 * 24 * 60 * 60) // Expires in 1 hour
    };

    // Encode to base64
    const base64Encode = (str) => {
        return Buffer.from(JSON.stringify(str))
            .toString('base64')
            .replace(/=/g, '')
            .replace(/\+/g, '-')
            .replace(/\//g, '_');
    };

    const encodedHeader = base64Encode(header);
    const encodedPayload = base64Encode(payload);

    // Create signature
    const crypto = require('crypto');
    const signature = crypto
        .createHmac('sha256', secret)
        .update(encodedHeader + '.' + encodedPayload)
        .digest('base64')
        .replace(/=/g, '')
        .replace(/\+/g, '-')
        .replace(/\//g, '_');

    // Combine to create JWT
    return encodedHeader + '.' + encodedPayload + '.' + signature;
}

// Example usage
const email = "user@example.com";
const role = "admin";
const jwtToken = generateJWT(email, role);

console.log("Generated JWT Token:");
console.log(jwtToken);