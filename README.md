# capacitor-barcode-scanner

Simple Barcode scanner for capacitor, featuring an embedded camera view for scanning.
Supports code 128 and QR
Uses Google MLKit in android.
iOS is not supported yet.

This branch base on 0.0.2 for capacitor 3.

## Install

```bash
npm install https://github.com/purplewish07/capacitor-barcode-scanner.git
npx cap sync
```

### Capacitor Compatibility

| Plugin Version | Capacitor Version |
|----------------|-------------------|
| 0.0.2          | Capacitor 3       |

#### iOS
In XCode -> App info.plist add key NSCameraUsageDescription

## API

<docgen-index>

* [`scan()`](#scan)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### scan()

```typescript
scan() => Promise<ScanResult>
```

Start scan screen
This promise will fail if permission for camera is denied

**Returns:** <code>Promise&lt;<a href="#scanresult">ScanResult</a>&gt;</code>

--------------------


### Interfaces


#### ScanResult

Represents a Scan Result

| Prop         | Type                 | Description                                   |
| ------------ | -------------------- | --------------------------------------------- |
| **`result`** | <code>boolean</code> | sucess status, its true when scanner got code |
| **`code`**   | <code>string</code>  | scanned code                                  |

</docgen-api>
