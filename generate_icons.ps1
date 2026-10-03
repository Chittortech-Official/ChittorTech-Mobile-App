Add-Type -AssemblyName System.Drawing

$srcPath = "public\icon.png"
if (-not (Test-Path $srcPath)) {
    Write-Error "Source file not found: $srcPath"
    exit 1
}

$origBmp = [System.Drawing.Bitmap]::FromFile((Resolve-Path $srcPath))

# Bounding box with small padding
$cropX = 160
$cropY = 260
$cropW = 705
$cropH = 510

$cropRect = New-Object System.Drawing.Rectangle $cropX, $cropY, $cropW, $cropH
$logoCrop = $origBmp.Clone($cropRect, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)

# Density maps
# Adaptive foreground sizes (108dp base)
$adaptiveSizes = @{
    "mipmap-mdpi"    = 108
    "mipmap-hdpi"    = 162
    "mipmap-xhdpi"   = 216
    "mipmap-xxhdpi"  = 324
    "mipmap-xxxhdpi" = 432
}

# Legacy icon sizes (48dp base)
$legacySizes = @{
    "mipmap-mdpi"    = 48
    "mipmap-hdpi"    = 72
    "mipmap-xhdpi"   = 96
    "mipmap-xxhdpi"  = 144
    "mipmap-xxxhdpi" = 192
}

$resBase = "app\src\main\res"

function SetupGraphics($g) {
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
}

# 1. Generate Adaptive Foreground Icons (ic_launcher_foreground.png)
foreach ($density in $adaptiveSizes.Keys) {
    $size = $adaptiveSizes[$density]
    $dir = Join-Path $resBase $density
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    
    $bmp = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    SetupGraphics $g
    
    # White background for adaptive layer
    $g.Clear([System.Drawing.Color]::White)
    
    # Scale logo to fit safe zone: 50% width
    $targetW = [int]($size * 0.50)
    $targetH = [int]($targetW * ($cropH / $cropW))
    $targetX = [int](($size - $targetW) / 2)
    $targetY = [int](($size - $targetH) / 2)
    
    $destRect = New-Object System.Drawing.Rectangle $targetX, $targetY, $targetW, $targetH
    $g.DrawImage($logoCrop, $destRect, 0, 0, $cropW, $cropH, [System.Drawing.GraphicsUnit]::Pixel)
    $g.Dispose()
    
    $outPath = Join-Path $dir "ic_launcher_foreground.png"
    $bmp.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "Generated adaptive foreground: $outPath ($size x $size, logo: $targetW x $targetH)"
}

# 2. Generate Legacy Square/Standard Icons (ic_launcher.png)
foreach ($density in $legacySizes.Keys) {
    $size = $legacySizes[$density]
    $dir = Join-Path $resBase $density
    
    $bmp = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    SetupGraphics $g
    
    $g.Clear([System.Drawing.Color]::White)
    
    # For legacy icon, 70% width
    $targetW = [int]($size * 0.70)
    $targetH = [int]($targetW * ($cropH / $cropW))
    $targetX = [int](($size - $targetW) / 2)
    $targetY = [int](($size - $targetH) / 2)
    
    $destRect = New-Object System.Drawing.Rectangle $targetX, $targetY, $targetW, $targetH
    $g.DrawImage($logoCrop, $destRect, 0, 0, $cropW, $cropH, [System.Drawing.GraphicsUnit]::Pixel)
    $g.Dispose()
    
    $outPath = Join-Path $dir "ic_launcher.png"
    $bmp.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "Generated legacy ic_launcher: $outPath ($size x $size)"
}

# 3. Generate Legacy Round Icons (ic_launcher_round.png)
foreach ($density in $legacySizes.Keys) {
    $size = $legacySizes[$density]
    $dir = Join-Path $resBase $density
    
    $bmp = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    SetupGraphics $g
    
    # Transparent background
    $g.Clear([System.Drawing.Color]::Transparent)
    
    # Fill white circle
    $brush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::White)
    $g.FillEllipse($brush, 0, 0, $size - 1, $size - 1)
    $brush.Dispose()
    
    # Draw logo centered inside circle (65% width)
    $targetW = [int]($size * 0.65)
    $targetH = [int]($targetW * ($cropH / $cropW))
    $targetX = [int](($size - $targetW) / 2)
    $targetY = [int](($size - $targetH) / 2)
    
    $destRect = New-Object System.Drawing.Rectangle $targetX, $targetY, $targetW, $targetH
    $g.DrawImage($logoCrop, $destRect, 0, 0, $cropW, $cropH, [System.Drawing.GraphicsUnit]::Pixel)
    $g.Dispose()
    
    $outPath = Join-Path $dir "ic_launcher_round.png"
    $bmp.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "Generated legacy ic_launcher_round: $outPath ($size x $size)"
}

# 4. Generate Dedicated High-Res Splash Icon (splash_icon.png)
$splashSize = 512
$splashBmp = New-Object System.Drawing.Bitmap $splashSize, $splashSize, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($splashBmp)
SetupGraphics $g
$g.Clear([System.Drawing.Color]::White)

$targetW = [int]($splashSize * 0.52)
$targetH = [int]($targetW * ($cropH / $cropW))
$targetX = [int](($splashSize - $targetW) / 2)
$targetY = [int](($splashSize - $targetH) / 2)

$destRect = New-Object System.Drawing.Rectangle $targetX, $targetY, $targetW, $targetH
$g.DrawImage($logoCrop, $destRect, 0, 0, $cropW, $cropH, [System.Drawing.GraphicsUnit]::Pixel)
$g.Dispose()

$splashOut = "app\src\main\res\drawable\splash_icon.png"
$splashBmp.Save($splashOut, [System.Drawing.Imaging.ImageFormat]::Png)
$splashBmp.Dispose()
Write-Host "Generated splash icon: $splashOut ($splashSize x $splashSize)"

# Cleanup
$logoCrop.Dispose()
$origBmp.Dispose()
Write-Host "All icons generated successfully!"
