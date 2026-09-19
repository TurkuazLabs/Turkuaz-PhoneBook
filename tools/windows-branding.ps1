# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/windows-branding.ps1
# 📌 Amac: Windows Setup/portable icin kirpilmis ve Light/Dark palete renklendirilmis cok-cozunurluklu ICO dosyalari uretir.
# 📌 Tool - PowerShell
# Version: 1.0.0
# Aciklama: app-icon-512.png kaynakli neutral, light (#128C7E) ve dark (#00A884) 16/24/32/48/64/128/256 px ICO seti uretir.
# Bagimli Oldugu Katman: Tool | Config

function Convert-HexColor([string]$Hex) {
    $value = $Hex.Trim().TrimStart('#')
    if ($value.Length -ne 6) { throw "Gecersiz renk: $Hex" }
    return [System.Drawing.Color]::FromArgb(
        255,
        [Convert]::ToInt32($value.Substring(0,2),16),
        [Convert]::ToInt32($value.Substring(2,2),16),
        [Convert]::ToInt32($value.Substring(4,2),16)
    )
}

function Write-TurkuazWindowsIcon([string]$SourcePng,[string]$DestinationIco,[string]$AccentHex = '') {
    if (-not (Test-Path -LiteralPath $SourcePng)) { throw "Windows ikon kaynagi bulunamadi: $SourcePng" }
    Add-Type -AssemblyName System.Drawing

    $original = [System.Drawing.Bitmap]::FromFile($SourcePng)
    try {
        $source = [System.Drawing.Bitmap]::new($original.Width,$original.Height,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g0 = [System.Drawing.Graphics]::FromImage($source)
        try { $g0.DrawImage($original,0,0,$original.Width,$original.Height) } finally { $g0.Dispose() }

        try {
            $accent = $null
            if (-not [string]::IsNullOrWhiteSpace($AccentHex)) { $accent = Convert-HexColor $AccentHex }

            $minX=$source.Width; $minY=$source.Height; $maxX=-1; $maxY=-1
            for($y=0;$y -lt $source.Height;$y++){
                for($x=0;$x -lt $source.Width;$x++){
                    $pixel=$source.GetPixel($x,$y)
                    if($pixel.A -le 8){ continue }
                    if($x -lt $minX){$minX=$x}; if($y -lt $minY){$minY=$y}
                    if($x -gt $maxX){$maxX=$x}; if($y -gt $maxY){$maxY=$y}

                    if($null -ne $accent){
                        $max=[Math]::Max($pixel.R,[Math]::Max($pixel.G,$pixel.B))
                        $min=[Math]::Min($pixel.R,[Math]::Min($pixel.G,$pixel.B))
                        $isGlyph=($max-$min -lt 42 -and $max -gt 174)
                        if($isGlyph){
                            if($AccentHex -eq '#00A884'){$glyph=[System.Drawing.Color]::FromArgb($pixel.A,236,253,250)}
                            else{$glyph=[System.Drawing.Color]::FromArgb($pixel.A,255,255,255)}
                            $source.SetPixel($x,$y,$glyph)
                        } else {
                            $brightness=[Math]::Max(0.28,[Math]::Min(1.0,$max/255.0))
                            if($AccentHex -eq '#00A884'){$scale=0.72+(0.48*$brightness)}
                            else{$scale=0.62+(0.48*$brightness)}
                            $r=[Math]::Min(255,[int][Math]::Round($accent.R*$scale))
                            $g=[Math]::Min(255,[int][Math]::Round($accent.G*$scale))
                            $b=[Math]::Min(255,[int][Math]::Round($accent.B*$scale))
                            $source.SetPixel($x,$y,[System.Drawing.Color]::FromArgb($pixel.A,$r,$g,$b))
                        }
                    }
                }
            }

            if($maxX -lt $minX -or $maxY -lt $minY){ throw "Ikonda gorunur piksel bulunamadi." }
            $cropWidth=$maxX-$minX+1; $cropHeight=$maxY-$minY+1
            $srcRect=[System.Drawing.Rectangle]::new($minX,$minY,$cropWidth,$cropHeight)
            $sizes=@(16,24,32,48,64,128,256)
            $frames=@()

            foreach($size in $sizes){
                $canvas=[System.Drawing.Bitmap]::new([int]$size,[int]$size,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
                try{
                    $g=[System.Drawing.Graphics]::FromImage($canvas)
                    try{
                        $g.Clear([System.Drawing.Color]::Transparent)
                        $g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                        $g.SmoothingMode=[System.Drawing.Drawing2D.SmoothingMode]::HighQuality
                        $g.PixelOffsetMode=[System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
                        $margin=[Math]::Max(1,[int][Math]::Round($size*0.04))
                        $available=$size-(2*$margin)
                        $ratio=[Math]::Min([double]$available/$cropWidth,[double]$available/$cropHeight)
                        $w=[Math]::Max(1,[int][Math]::Round($cropWidth*$ratio))
                        $h=[Math]::Max(1,[int][Math]::Round($cropHeight*$ratio))
                        $target=[System.Drawing.Rectangle]::new([int](($size-$w)/2),[int](($size-$h)/2),$w,$h)
                        $g.DrawImage($source,$target,$srcRect,[System.Drawing.GraphicsUnit]::Pixel)
                    }finally{$g.Dispose()}
                    $m=[System.IO.MemoryStream]::new()
                    try{$canvas.Save($m,[System.Drawing.Imaging.ImageFormat]::Png);$frames+=,$m.ToArray()}finally{$m.Dispose()}
                }finally{$canvas.Dispose()}
            }

            $dir=Split-Path -Parent $DestinationIco
            if(-not (Test-Path -LiteralPath $dir)){New-Item -ItemType Directory -Path $dir -Force|Out-Null}
            $stream=[System.IO.File]::Open($DestinationIco,[System.IO.FileMode]::Create,[System.IO.FileAccess]::Write,[System.IO.FileShare]::None)
            $writer=[System.IO.BinaryWriter]::new($stream)
            try{
                $writer.Write([UInt16]0);$writer.Write([UInt16]1);$writer.Write([UInt16]$sizes.Count)
                $offset=[UInt32](6+(16*$sizes.Count))
                for($i=0;$i -lt $sizes.Count;$i++){
                    $size=[int]$sizes[$i];$frame=[byte[]]$frames[$i]
                    if($size -ge 256){$dimension=[byte]0}else{$dimension=[byte]$size}
                    $writer.Write($dimension);$writer.Write($dimension);$writer.Write([byte]0);$writer.Write([byte]0)
                    $writer.Write([UInt16]1);$writer.Write([UInt16]32);$writer.Write([UInt32]$frame.Length);$writer.Write([UInt32]$offset)
                    $offset=[UInt32]($offset+$frame.Length)
                }
                foreach($frame in $frames){$writer.Write([byte[]]$frame)}
            }finally{$writer.Dispose();$stream.Dispose()}
        } finally { $source.Dispose() }
    } finally { $original.Dispose() }
}

function Write-TurkuazWindowsIconSet([string]$SourcePng,[string]$DestinationDirectory) {
    Write-TurkuazWindowsIcon $SourcePng (Join-Path $DestinationDirectory 'app-icon.ico')
    Write-TurkuazWindowsIcon $SourcePng (Join-Path $DestinationDirectory 'app-icon-light.ico') '#128C7E'
    Write-TurkuazWindowsIcon $SourcePng (Join-Path $DestinationDirectory 'app-icon-dark.ico') '#00A884'
    Write-Host "[OK] Windows tema ikonlari uretildi: neutral/light/dark"
}
