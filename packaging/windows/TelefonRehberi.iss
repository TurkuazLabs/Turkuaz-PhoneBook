; 📄 Dosya Yolu: C:/Projects/TelefonRehberi/packaging/windows/TelefonRehberi.iss
; 📌 Amac: Turkuaz Windows Program Files kurulum paketini ve dil bazli urun adini tanimlar.
; 📌 Tool - InnoSetup
; Version: 1.6.1
; Aciklama: Surume ozel cache-busting kisayol/uninstall ICO yolu, cok-cozunurluklu Windows ikonu ve Turkce/English urun adini kullanir.
; Bagimli Oldugu Katman: Tool | Config | Language

#ifndef AppVersion
  #define AppVersion "2.41.0"
#endif
#ifndef PayloadDir
  #define PayloadDir "..\..\dist-installer\payload"
#endif
#ifndef OutputDir
  #define OutputDir "..\..\dist"
#endif

#define AppPublisher "TurkuazLabs"
#define AppExeName "TelefonRehberi.exe"
#define AppId "{{A993B70A-4C66-49B2-9A80-62D8495FADE1}"

[Setup]
AppId={#AppId}
AppName={cm:ProductName}
AppVersion={#AppVersion}
AppVerName={cm:ProductName} {#AppVersion}
AppPublisher={#AppPublisher}
AppPublisherURL=https://www.turkuazlabs.com
AppSupportURL=https://www.turkuazlabs.com
VersionInfoProductName=Turkuaz
VersionInfoDescription=Turkuaz Setup
DefaultDirName={autopf}\TurkuazLabs\TelefonRehberi
DefaultGroupName=TurkuazLabs\{cm:ProductName}
DisableProgramGroupPage=yes
PrivilegesRequired=admin
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
MinVersion=10.0.17763
OutputDir={#OutputDir}
OutputBaseFilename=TelefonRehberi-Setup-v{#AppVersion}
SetupIconFile={#PayloadDir}\assets\branding\app-icon.ico
UninstallDisplayIcon={app}\assets\branding\app-icon-v{#AppVersion}.ico
UninstallDisplayName={cm:ProductName}
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

[CustomMessages]
turkish.ProductName=Turkuaz Telefon Rehberi
english.ProductName=Turkuaz PhoneBook
turkish.CreateDesktopShortcut=Masaustune kisayol olustur
english.CreateDesktopShortcut=Create a desktop shortcut
turkish.AdditionalShortcuts=Ek kisayollar:
english.AdditionalShortcuts=Additional shortcuts:
turkish.LaunchProduct=Turkuaz Telefon Rehberi'ni baslat
english.LaunchProduct=Launch Turkuaz PhoneBook

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopShortcut}"; GroupDescription: "{cm:AdditionalShortcuts}"; Flags: unchecked

[Files]
Source: "{#PayloadDir}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\TurkuazLabs\{cm:ProductName}"; Filename: "{app}\{#AppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\assets\branding\app-icon-v{#AppVersion}.ico"
Name: "{autodesktop}\{cm:ProductName}"; Filename: "{app}\{#AppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\assets\branding\app-icon-v{#AppVersion}.ico"; Tasks: desktopicon

[Registry]
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\App Paths\{#AppExeName}"; ValueType: string; ValueName: ""; ValueData: "{app}\{#AppExeName}"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\App Paths\{#AppExeName}"; ValueType: string; ValueName: "Path"; ValueData: "{app}"; Flags: uninsdeletevalue

[Run]
Filename: "{app}\{#AppExeName}"; Description: "{cm:LaunchProduct}"; WorkingDir: "{app}"; Flags: nowait postinstall skipifsilent runasoriginaluser
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
