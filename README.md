# capacitor-barcode-scanner

Simple Barcode scanner for capacitor, shows popup camera view to scan.
Supports code 128 and QR
Uses Google MLKit in android, and AVFoundation on iOS

## Install

```bash
npm install capacitor-barcode-scanner
npx cap sync
```

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
