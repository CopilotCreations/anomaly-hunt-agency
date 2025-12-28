# Google Play Store Compliance Documentation

**App Name**: Dead Pixel Detective  
**Package Name**: com.deadpixeldetective  
**Version**: 1.0.0  
**Last Updated**: December 2024

This document maps Dead Pixel Detective's behavior to Google Play's developer policies to demonstrate compliance.

---

## 1. Privacy and Data Security

### Policy: User Data
**Requirement**: Apps must be transparent about data collection and handling.

**Our Compliance**:
| Requirement | Implementation |
|-------------|----------------|
| Privacy Policy | Provided in `privacy_policy.md` and accessible in-app |
| Data Collection Disclosure | We collect NO personal data |
| Data Transmission | No user data is transmitted externally |
| Secure Data Handling | Local data stored via encrypted DataStore |

### Policy: Permissions
**Requirement**: Only request permissions that are necessary.

**Our Compliance**:
| Permission | Justification |
|------------|---------------|
| INTERNET | Required for AdMob SDK to serve ads |
| ACCESS_NETWORK_STATE | Required for AdMob to check connectivity |

**We do NOT request**:
- Location permissions
- Camera/microphone access
- Contact access
- Storage access beyond app-private storage
- Phone state access
- Any dangerous permissions

---

## 2. Ads Policy

### Policy: AdMob Integration
**Requirement**: Ads must not interfere with app functionality or deceive users.

**Our Compliance**:
| Requirement | Implementation |
|-------------|----------------|
| Test Ad Units | Using official Google test ad unit IDs |
| Ad Placement | Banner ads at screen bottom, interstitials between levels only |
| User Experience | Ads never block gameplay; interstitials shown only at natural break points |
| Personalization | Configured for non-personalized ads |
| Close Button | Interstitial ads have clear close functionality |

### Ad Unit IDs (Test)
```
Banner: ca-app-pub-3940256099942544/6300978111
Interstitial: ca-app-pub-3940256099942544/1033173712
App ID: ca-app-pub-3940256099942544~3347511713
```

These are official Google test IDs that work on any device.

---

## 3. Deceptive Behavior

### Policy: No Misleading Claims
**Requirement**: Apps must not make misleading claims about functionality.

**Our Compliance**:
- The app clearly states it simulates "dead pixels" as a game mechanic
- No claims that the app can detect or repair actual screen defects
- Game description explicitly states anomalies are "simulated artifacts"
- About screen clarifies the nature of the game

### Policy: No Impersonation
**Requirement**: Apps must not impersonate other apps or system functions.

**Our Compliance**:
- Unique app identity with original branding
- Does not mimic system UI or diagnostic tools
- Clear game branding distinguishes it from system apps

---

## 4. Intellectual Property

### Policy: Original Content
**Requirement**: Apps must not infringe on intellectual property.

**Our Compliance**:
| Content Type | Status |
|--------------|--------|
| Code | Original, no copied code |
| Graphics | Programmatically generated, no external assets |
| Sounds | Not included (future: CC0 licensed only) |
| Icons | Original design |
| Name | Unique, not trademarked by others |

---

## 5. Spam and Minimum Functionality

### Policy: Meaningful User Experience
**Requirement**: Apps must provide meaningful value to users.

**Our Compliance**:
- Complete game experience with progressive difficulty
- Original gameplay mechanic (perception-based puzzle)
- Multiple levels with varying challenges
- Settings and accessibility options
- Smooth animations and responsive UI

---

## 6. Family Policy (If Applicable)

### Policy: Family-Friendly Content
**Requirement**: Apps marketed to children must follow additional guidelines.

**Our Compliance**:
| Requirement | Implementation |
|-------------|----------------|
| Age-Appropriate | Content suitable for all ages |
| No In-App Purchases | App is completely free |
| No Social Features | No chat, sharing, or social integration |
| No Personal Data | No accounts, no data collection |
| Ad Policy | Using family-friendly ad configuration |

---

## 7. Device and Network Abuse

### Policy: No Harmful Behavior
**Requirement**: Apps must not abuse device resources or networks.

**Our Compliance**:
| Concern | Mitigation |
|---------|------------|
| Battery Drain | Low-frequency animations, sensor updates throttled |
| CPU Usage | Efficient Compose rendering, no background processing |
| Network Usage | Minimal (ads only), works fully offline |
| Storage | Minimal local storage (preferences only) |

### Sensor Usage
```kotlin
// Sensor delay set to SENSOR_DELAY_UI (slowest appropriate rate)
sensorManager.registerListener(listener, sensor, SENSOR_DELAY_UI)
```

---

## 8. Content Rating

### Recommended Rating: Everyone (E)

**Justification**:
- No violence or mature themes
- No user-generated content
- No real gambling
- No crude humor
- No references to drugs, alcohol, or tobacco

---

## 9. App Bundle and Signing

### Build Configuration
```kotlin
buildTypes {
    debug {
        isDebuggable = true
        isMinifyEnabled = false
    }
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(...)
    }
}
```

### Signing
- Debug: Auto-generated debug keystore
- Release: Requires creation of release keystore before Play Store submission

---

## 10. Target SDK Compliance

### Policy: Modern SDK Targeting
**Requirement**: New apps must target recent Android SDK.

**Our Compliance**:
```kotlin
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 29  // Android 10
        targetSdk = 34  // Android 14
    }
}
```

---

## 11. Accessibility

### Implementation
| Feature | Status |
|---------|--------|
| High Contrast Mode | ✅ Implemented |
| Reduced Motion | ✅ Implemented |
| Haptic Feedback Toggle | ✅ Implemented |
| Content Descriptions | ✅ On interactive elements |
| Touch Target Size | ✅ Minimum 48dp |

---

## Summary Checklist

| Policy Area | Compliant |
|-------------|-----------|
| Privacy & Data Security | ✅ |
| Ads Policy | ✅ |
| Deceptive Behavior | ✅ |
| Intellectual Property | ✅ |
| Spam & Minimum Functionality | ✅ |
| Family Policy | ✅ |
| Device & Network Abuse | ✅ |
| Content Rating | ✅ |
| App Bundle & Signing | ✅ |
| Target SDK | ✅ |
| Accessibility | ✅ |

---

## Contact for Policy Questions

For any policy-related inquiries, please contact through the Google Play Console or the app store listing.

---

*This document should be updated whenever app functionality or policies change.*
