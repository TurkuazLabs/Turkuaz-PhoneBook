; 📄 Dosya Yolu: C:/Projects/TelefonRehberi/packaging/windows/TelefonRehberi.iss
; 📌 Amac: Turkuaz Telefon Rehberi Windows Program Files kurulum paketini tanimlar.
; 📌 Tool - InnoSetup
; Version: 1.1.0
; Aciklama: Program Files kurulumu ve /AUTOUPDATE sonrasi otomatik yeniden baslatma davranisini tanimlar.
; Bagimli Oldugu Katman: Tool | Config

#ifndef AppVersion
  #define AppVersion "2.37.0"
#endif
#ifndef PayloadDir
  #define PayloadDir "..\..\dist-installer\payload"
#endif
#ifndef OutputDir
  #define OutputDir "..\..\dist"
#endif

#define AppName "Turkuaz Telefon Rehberi"
#define AppPublisher "TurkuazLabs"
#define AppExeName "TelefonRehberi.exe"
#define AppId "{{A993B70A-4C66-49B2-9A80-62D8495FADE1}"

[Setup]
AppId={#AppId}
AppName={#AppName}
AppVersion={#AppVersion}
AppVerName={#AppName} {#AppVersion}
AppPublisher={#AppPublisher}
AppPublisherURL=https://www.turkuazlabs.com
AppSupportURL=https://www.turkuazlabs.com
DefaultDirName={autopf}\TurkuazLabs\TelefonRehberi
DefaultGroupName=TurkuazLabs\Telefon Rehberi
DisableProgramGroupPage=yes
PrivilegesRequired=admin
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
MinVersion=10.0.17763
OutputDir={#OutputDir}
OutputBaseFilename=TelefonRehberi-Setup-v{#AppVersion}
SetupIconFile={#PayloadDir}\assets\branding\app-icon.ico
UninstallDisplayIcon={app}\{#AppExeName}
UninstallDisplayName={#AppName}
Compression=lzma2/ultra64
SolidCompression=yes
WizardStyle=modern
CloseApplications=yes
RestartApplications=no
SetupLogging=yes
UsePreviousAppDir=yes
UsePreviousTasks=yes

[Languages]
Name: "turkish"; MessagesFile: "compiler:Languages\Turkish.isl"
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Masaustune kisayol olustur"; GroupDescription: "Ek kisayollar:"; Flags: unchecked

[Files]
Source: "{#PayloadDir}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\TurkuazLabs\Telefon Rehberi"; Filename: "{app}\{#AppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\assets\branding\app-icon.ico"
Name: "{autodesktop}\Telefon Rehberi"; Filename: "{app}\{#AppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\assets\branding\app-icon.ico"; Tasks: desktopicon

[Registry]
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\App Paths\{#AppExeName}"; ValueType: string; ValueName: ""; ValueData: "{app}\{#AppExeName}"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\App Paths\{#AppExeName}"; ValueType: string; ValueName: "Path"; ValueData: "{app}"; Flags: uninsdeletevalue

[Run]
Filename: "{app}\{#AppExeName}"; Description: "Telefon Rehberi'ni baslat"; WorkingDir: "{app}"; Flags: nowait postinstall skipifsilent runasoriginaluser
Filename: "{app}\{#AppExeName}"; WorkingDir: "{app}"; Flags: nowait runasoriginaluser; Check: IsAutoUpdateMode

[Code]
function IsAutoUpdateMode: Boolean;
var
  I: Integer;
begin
  Result := False;
  for I := 1 to ParamCount do
  begin
    if CompareText(ParamStr(I), '/AUTOUPDATE') = 0 then
    begin
      Result := True;
      Exit;
    end;
  end;
end;
