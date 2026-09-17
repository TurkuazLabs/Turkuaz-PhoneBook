// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/services/launcher_service.go
// 📌 Amac: Native launcher is akisini ve update kurallarini yonetir.
// 📌 Modul - Go
// Version: 2.5.0
// Aciklama: Turkuaz-PhoneBook 2.5.0 launcher kimligiyle portable/installed update, kullanici tercihi override ve Java baslatma akislarini yonetir.
// Bagimli Oldugu Katman: Service

package services

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"runtime"
	"strconv"
	"strings"
	"time"

	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/language"
	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/repositories"
	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/tools"
	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/views"
)

const (
	defaultInstalledVersion = "0.0.0"
	launcherUserAgent       = "TurkuazPhoneBookLauncher/2.5.0"
)

type LauncherService struct {
	Root        string
	MutableRoot string
	Installed   bool
	YAML        repositories.SimpleYAMLRepository
	Splash      views.Splash
	Log         tools.Logger
}

type adoptiumAsset struct {
	Binary struct {
		Package struct {
			Link     string `json:"link"`
			Checksum string `json:"checksum"`
		} `json:"package"`
	} `json:"binary"`
}

func NewLauncherService(root string, splash views.Splash) LauncherService {
	installed := tools.IsInstalledDistribution(root)
	mutableRoot := tools.LauncherMutableRoot(root, installed)
	return LauncherService{
		Root: root, MutableRoot: mutableRoot, Installed: installed,
		YAML: repositories.SimpleYAMLRepository{}, Splash: splash, Log: tools.NewLogger(mutableRoot),
	}
}

func (s *LauncherService) Run() error {
	started := time.Now()
	s.Log.Write("INFO", "Native launcher baslatildi.")
	s.Splash.Update(language.StatusPreparing, 10)
	if err := tools.EnsureDirs(s.MutableRoot, "cache", "config", "logs", "updates", "updates/app-backups"); err != nil {
		return err
	}
	if !s.Installed {
		if err := tools.EnsureDirs(s.Root, "lib", "runtime"); err != nil {
			return err
		}
	}

	configPath := tools.AppPath(s.Root, "config/launcher.yml")
	statePath := tools.AppPath(s.MutableRoot, "config/state.yml")
	if err := s.ensureStateFile(statePath); err != nil {
		return err
	}
	config, err := s.YAML.Read(configPath)
	if err != nil {
		return fmt.Errorf("launcher config okunamadi: %w", err)
	}
	userPreferences, err := s.YAML.Read(tools.UserPreferencesPath())
	if err != nil {
		s.Log.Write("WARN", "Kullanici update tercihi okunamadi: "+err.Error())
	} else if value, ok := userPreferences["update_enabled"]; ok {
		config["update_enabled"] = value
	}
	state, err := s.YAML.Read(statePath)
	if err != nil {
		return fmt.Errorf("launcher state okunamadi: %w", err)
	}

	timeout := intValue(config, "update_timeout_seconds", 180)
	httpTool := tools.NewHTTPTool(timeout, launcherUserAgent)

	s.Splash.Update(language.StatusJava, 24)
	javaPath, err := s.ensureJava(config, httpTool)
	if err != nil {
		return err
	}

	s.Splash.Update(language.StatusDependencies, 48)
	sqlitePath, err := s.ensureSQLite(config, httpTool)
	if err != nil {
		return err
	}
	slf4jPath, err := s.ensureSlf4j(config, httpTool)
	if err != nil {
		return err
	}
	flatLafPath, err := s.ensureFlatLaf(config, httpTool)
	if err != nil {
		return err
	}

	s.Splash.Update(language.StatusUpdate, 70)
	state, exitRequested := s.updateFromGitHub(config, state, httpTool)
	if err := s.YAML.WriteState(statePath, mapValue(state, "app_version", defaultInstalledVersion), mapValue(state, "launcher_version", defaultInstalledVersion)); err != nil {
		s.Log.Write("WARN", "State yazilamadi: "+err.Error())
	}
	if exitRequested {
		s.Splash.Update(language.StatusInstallingUpdate, 100)
		time.Sleep(250 * time.Millisecond)
		return nil
	}

	s.Splash.Update(language.StatusOpening, 94)
	if err := s.startApplication(config, javaPath, sqlitePath, slf4jPath, flatLafPath); err != nil {
		return err
	}
	if elapsed := time.Since(started); elapsed < 850*time.Millisecond {
		time.Sleep(850*time.Millisecond - elapsed)
	}
	s.Splash.Update(language.StatusOpening, 100)
	time.Sleep(120 * time.Millisecond)
	s.Log.Write("INFO", "Telefon Rehberi baslatildi.")
	return nil
}

func (s *LauncherService) ensureStateFile(statePath string) error {
	if _, err := os.Stat(statePath); err == nil {
		return nil
	}
	seed := tools.AppPath(s.Root, "config/state.yml")
	if _, err := os.Stat(seed); err == nil {
		if err := tools.CopyFile(seed, statePath); err == nil {
			return nil
		}
	}
	return s.YAML.WriteState(statePath, defaultInstalledVersion, defaultInstalledVersion)
}

func (s *LauncherService) ensureJava(config map[string]string, httpTool tools.HTTPTool) (string, error) {
	runtimeDir := tools.AppPath(s.Root, required(config, "java_runtime_dir"))
	javaName := "java"
	if runtime.GOOS == "windows" {
		javaName = "javaw.exe"
	}
	javaPath := filepath.Join(runtimeDir, "bin", javaName)
	if _, err := os.Stat(javaPath); err == nil {
		s.Log.Write("INFO", "Java runtime hazir: "+runtimeDir)
		return javaPath, nil
	}
	if s.Installed {
		return "", fmt.Errorf("kurulum Java runtime dosyasi eksik; Telefon Rehberi Setup ile Onar/Kur islemi yapin")
	}

	apiURL := required(config, "adoptium_assets_url")
	apiURL = strings.ReplaceAll(apiURL, "{major}", required(config, "java_major"))
	apiURL = strings.ReplaceAll(apiURL, "{arch}", resolveAdoptiumArch(config["java_architecture"]))
	apiURL = strings.ReplaceAll(apiURL, "{image}", required(config, "java_image_type"))
	apiURL = strings.ReplaceAll(apiURL, "{vendor}", required(config, "java_vendor"))
	apiURL = strings.ReplaceAll(apiURL, "{os}", resolveAdoptiumOS())
	s.Log.Write("INFO", "Adoptium metadata kontrol ediliyor.")
	body, err := httpTool.GetBytes(apiURL)
	if err != nil {
		return "", fmt.Errorf("Java metadata indirilemedi: %w", err)
	}
	var assets []adoptiumAsset
	if err := json.Unmarshal(body, &assets); err != nil || len(assets) == 0 {
		return "", fmt.Errorf("Adoptium uygun Java paketi dondurmedi")
	}
	pkg := assets[0].Binary.Package
	if pkg.Link == "" || pkg.Checksum == "" {
		return "", fmt.Errorf("Adoptium paket URL veya checksum eksik")
	}
	archiveName := "cache/temurin-jre.zip"
	if runtime.GOOS != "windows" {
		archiveName = "cache/temurin-jre.tar.gz"
	}
	archive := tools.AppPath(s.MutableRoot, archiveName)
	extract := tools.AppPath(s.MutableRoot, "cache/temurin-extract")
	_ = os.RemoveAll(extract)
	_ = os.Remove(archive)
	if err := httpTool.Download(pkg.Link, archive); err != nil {
		return "", fmt.Errorf("Java indirilemedi: %w", err)
	}
	if err := tools.AssertSHA256(archive, pkg.Checksum); err != nil {
		return "", err
	}
	if runtime.GOOS == "windows" {
		if err := tools.ExtractZIP(archive, extract); err != nil {
			return "", fmt.Errorf("Java ZIP arsivi acilamadi: %w", err)
		}
	} else {
		if err := tools.ExtractTarGz(archive, extract); err != nil {
			return "", fmt.Errorf("Java TAR.GZ arsivi acilamadi: %w", err)
		}
	}
	javaHome, err := findJavaHome(extract, javaName)
	if err != nil {
		return "", err
	}
	_ = os.RemoveAll(runtimeDir)
	if err := copyDirectory(javaHome, runtimeDir); err != nil {
		return "", fmt.Errorf("Java runtime kopyalanamadi: %w", err)
	}
	_ = os.Remove(archive)
	_ = os.RemoveAll(extract)
	if _, err := os.Stat(javaPath); err != nil {
		return "", fmt.Errorf("portable Java kurulumu tamamlanamadi")
	}
	s.Log.Write("INFO", "Portable Java kuruldu: "+runtimeDir)
	return javaPath, nil
}

func (s *LauncherService) ensureSQLite(config map[string]string, httpTool tools.HTTPTool) (string, error) {
	target := tools.AppPath(s.Root, required(config, "sqlite_jdbc_path"))
	expected := required(config, "sqlite_jdbc_sha256")
	if _, err := os.Stat(target); err == nil {
		if tools.AssertSHA256(target, expected) == nil {
			return target, nil
		}
		_ = os.Remove(target)
	}
	url := strings.ReplaceAll(required(config, "sqlite_jdbc_url"), "{version}", required(config, "sqlite_jdbc_version"))
	if s.Installed {
		return "", fmt.Errorf("kurulum SQLite JDBC dosyasi eksik veya bozuk; Telefon Rehberi Setup ile Onar/Kur islemi yapin")
	}
	cache := tools.AppPath(s.MutableRoot, "cache/sqlite-jdbc.jar")
	if err := httpTool.Download(url, cache); err != nil {
		return "", fmt.Errorf("SQLite JDBC indirilemedi: %w", err)
	}
	if err := tools.AssertSHA256(cache, expected); err != nil {
		return "", err
	}
	if err := tools.ReplaceFile(cache, target); err != nil {
		return "", err
	}
	return target, nil
}

func (s *LauncherService) ensureSlf4j(config map[string]string, httpTool tools.HTTPTool) (string, error) {
	version := mapValue(config, "slf4j_version", "1.7.36")
	target := tools.AppPath(s.Root, strings.ReplaceAll(required(config, "slf4j_api_path"), "{version}", version))
	expected := required(config, "slf4j_api_sha256")
	if _, err := os.Stat(target); err == nil {
		if tools.AssertSHA256(target, expected) == nil {
			return target, nil
		}
		_ = os.Remove(target)
	}
	url := strings.ReplaceAll(required(config, "slf4j_api_url"), "{version}", version)
	if s.Installed {
		return "", fmt.Errorf("kurulum SLF4J API dosyasi eksik veya bozuk; Telefon Rehberi Setup ile Onar/Kur islemi yapin")
	}
	cache := tools.AppPath(s.MutableRoot, "cache/slf4j-api.jar")
	if err := httpTool.Download(url, cache); err != nil {
		return "", fmt.Errorf("SLF4J API indirilemedi: %w", err)
	}
	if err := tools.AssertSHA256(cache, expected); err != nil {
		return "", err
	}
	if err := tools.ReplaceFile(cache, target); err != nil {
		return "", err
	}
	return target, nil
}

func (s *LauncherService) ensureFlatLaf(config map[string]string, httpTool tools.HTTPTool) (string, error) {
	version := mapValue(config, "flatlaf_version", "3.7.2")
	target := tools.AppPath(s.Root, strings.ReplaceAll(mapValue(config, "flatlaf_path", "lib/flatlaf-{version}.jar"), "{version}", version))
	sidecar := target + ".sha256"
	if jarInfo, err := os.Stat(target); err == nil && !jarInfo.IsDir() {
		if raw, err := os.ReadFile(sidecar); err == nil {
			if expected, err := tools.ParseChecksum(string(raw)); err == nil && tools.AssertSHA256(target, expected) == nil {
				return target, nil
			}
		}
		_ = os.Remove(target)
		_ = os.Remove(sidecar)
	}
	jarURL := strings.ReplaceAll(mapValue(config, "flatlaf_url", "https://repo1.maven.org/maven2/com/formdev/flatlaf/{version}/flatlaf-{version}.jar"), "{version}", version)
	shaURL := strings.ReplaceAll(mapValue(config, "flatlaf_sha256_url", "https://repo1.maven.org/maven2/com/formdev/flatlaf/{version}/flatlaf-{version}.jar.sha256"), "{version}", version)
	if s.Installed {
		return "", fmt.Errorf("kurulum FlatLaf dosyasi eksik veya bozuk; Telefon Rehberi Setup ile Onar/Kur islemi yapin")
	}
	shaRaw, err := httpTool.GetBytes(shaURL)
	if err != nil {
		return "", fmt.Errorf("FlatLaf checksum indirilemedi: %w", err)
	}
	expected, err := tools.ParseChecksum(string(shaRaw))
	if err != nil {
		return "", err
	}
	cache := tools.AppPath(s.MutableRoot, "cache/flatlaf.jar")
	if err := httpTool.Download(jarURL, cache); err != nil {
		return "", fmt.Errorf("FlatLaf indirilemedi: %w", err)
	}
	if err := tools.AssertSHA256(cache, expected); err != nil {
		return "", err
	}
	if err := tools.ReplaceFile(cache, target); err != nil {
		return "", err
	}
	if err := os.WriteFile(sidecar, []byte(expected+"\n"), 0o644); err != nil {
		return "", err
	}
	return target, nil
}

func (s *LauncherService) updateFromGitHub(config, state map[string]string, httpTool tools.HTTPTool) (map[string]string, bool) {
	if !boolValue(config, "update_enabled", false) {
		return state, false
	}
	manifestURL := githubAssetURL(config, required(config, "update_manifest_asset"))
	manifestPath := tools.AppPath(s.MutableRoot, "cache/update-manifest.yml")
	if err := httpTool.Download(manifestURL, manifestPath); err != nil {
		s.Log.Write("WARN", "GitHub update kontrolu atlandi: "+err.Error())
		return state, false
	}
	manifest, err := s.YAML.Read(manifestPath)
	if err != nil {
		s.Log.Write("WARN", "Update manifest okunamadi: "+err.Error())
		return state, false
	}

	if s.Installed {
		return s.updateInstalledDistribution(config, state, manifest, httpTool)
	}
	return s.updatePortableDistribution(config, state, manifest, httpTool), false
}

func (s *LauncherService) updateInstalledDistribution(config, state, manifest map[string]string, httpTool tools.HTTPTool) (map[string]string, bool) {
	if runtime.GOOS != "windows" || !boolValue(config, "installed_update_enabled", true) {
		return state, false
	}
	versionFile := tools.AppPath(s.Root, "config/version.yml")
	localVersion := mapValue(state, "app_version", defaultInstalledVersion)
	if versionValues, err := s.YAML.Read(versionFile); err == nil {
		localVersion = mapValue(versionValues, "app_version", localVersion)
	}
	newVersion := required(manifest, "app_version")
	if !tools.VersionGreater(newVersion, localVersion) {
		state["app_version"] = localVersion
		return state, false
	}

	assetKey := mapValue(config, "installed_update_asset_key", "windows_setup_asset")
	shaKey := mapValue(config, "installed_update_sha_key", "windows_setup_sha256")
	asset := mapValue(manifest, assetKey, "")
	expectedSHA := mapValue(manifest, shaKey, "")
	if asset != "" && filepath.Base(asset) != asset {
		s.Log.Write("WARN", "Kurulumlu surum update asset adi gecersiz.")
		return state, false
	}
	if asset == "" || expectedSHA == "" {
		s.Log.Write("WARN", "Kurulumlu surum update asseti manifestte bulunamadi.")
		return state, false
	}

	setupPath := tools.AppPath(s.MutableRoot, filepath.ToSlash(filepath.Join("cache", asset)))
	setupReady := false
	if _, err := os.Stat(setupPath); err == nil {
		setupReady = tools.AssertSHA256(setupPath, expectedSHA) == nil
	}
	if !setupReady {
		_ = os.Remove(setupPath)
		if err := httpTool.Download(githubAssetURL(config, asset), setupPath); err != nil {
			s.Log.Write("WARN", "Setup update indirilemedi: "+err.Error())
			return state, false
		}
		if err := tools.AssertSHA256(setupPath, expectedSHA); err != nil {
			s.Log.Write("WARN", "Setup update SHA-256 dogrulamasi basarisiz: "+err.Error())
			_ = os.Remove(setupPath)
			return state, false
		}
	}

	arguments := strings.Fields(mapValue(config, "installed_update_arguments", "/VERYSILENT /SUPPRESSMSGBOXES /NORESTART /CLOSEAPPLICATIONS /AUTOUPDATE"))
	// Inno Setup kendi PrivilegesRequired=admin ayariyla UAC ister. Launcher setup'i
	// normal kullanici tokeniyla baslatir ki Inno /AUTOUPDATE sonrasi runasoriginaluser
	// ile uygulamayi tekrar yonetici olmadan acabilsin.
	if err := tools.StartDetached(setupPath, arguments, filepath.Dir(setupPath)); err != nil {
		s.Log.Write("WARN", "Setup update baslatilamadi: "+err.Error())
		return state, false
	}
	s.Log.Write("INFO", "Kurulumlu surum guncellemesi baslatildi: "+localVersion+" -> "+newVersion)
	return state, true
}

func (s *LauncherService) updatePortableDistribution(config, state, manifest map[string]string, httpTool tools.HTTPTool) map[string]string {
	installedApp := mapValue(state, "app_version", defaultInstalledVersion)
	newApp := required(manifest, "app_version")
	if tools.VersionGreater(newApp, installedApp) {
		asset := required(manifest, "app_asset")
		staged := tools.AppPath(s.MutableRoot, "cache/TelefonRehberi.new.jar")
		updateErr := httpTool.Download(githubAssetURL(config, asset), staged)
		if updateErr == nil {
			updateErr = tools.AssertSHA256(staged, required(manifest, "app_sha256"))
		}
		if updateErr == nil {
			appPath := tools.AppPath(s.Root, required(config, "app_jar"))
			backupDir := tools.AppPath(s.MutableRoot, "updates/app-backups")
			_ = os.MkdirAll(backupDir, 0o755)
			if _, statErr := os.Stat(appPath); statErr == nil {
				backup := filepath.Join(backupDir, fmt.Sprintf("TelefonRehberi-%s-%s.jar", installedApp, time.Now().Format("20060102-150405")))
				_ = tools.CopyFile(appPath, backup)
			}
			updateErr = tools.ReplaceFile(staged, appPath)
		}
		if updateErr == nil {
			state["app_version"] = newApp
			_ = tools.RotateFiles(tools.AppPath(s.MutableRoot, "updates/app-backups"), "TelefonRehberi-*.jar", intValue(config, "backup_keep_count", 5))
			s.Log.Write("INFO", "Uygulama guncellendi: "+installedApp+" -> "+newApp)
		} else {
			s.Log.Write("WARN", "Uygulama update basarisiz: "+updateErr.Error())
		}
	}

	installedLauncher := mapValue(state, "launcher_version", defaultInstalledVersion)
	newLauncher := required(manifest, "launcher_version")
	if tools.VersionGreater(newLauncher, installedLauncher) {
		assetKey, shaKey := launcherManifestKeys()
		asset := mapValue(manifest, assetKey, mapValue(manifest, "launcher_asset", ""))
		expectedSha := mapValue(manifest, shaKey, mapValue(manifest, "launcher_sha256", ""))
		if asset == "" || expectedSha == "" {
			s.Log.Write("WARN", "Platform launcher update asseti manifestte bulunamadi.")
			return state
		}
		pending := PendingLauncher(s.Root)
		updateErr := httpTool.Download(githubAssetURL(config, asset), pending)
		if updateErr == nil {
			updateErr = tools.AssertSHA256(pending, expectedSha)
		}
		if updateErr == nil && runtime.GOOS != "windows" {
			updateErr = os.Chmod(pending, 0o755)
		}
		if updateErr == nil {
			state["launcher_version"] = newLauncher
			s.Log.Write("INFO", "Native launcher update sonraki acilis icin hazirlandi: "+newLauncher)
		} else {
			s.Log.Write("WARN", "Launcher update basarisiz: "+updateErr.Error())
		}
	}
	return state
}

func (s *LauncherService) startApplication(config map[string]string, javaPath, sqlitePath, slf4jPath, flatLafPath string) error {
	appPath := tools.AppPath(s.Root, required(config, "app_jar"))
	if _, err := os.Stat(appPath); err != nil {
		return fmt.Errorf("uygulama JAR bulunamadi: %s", appPath)
	}
	classpathSeparator := string(os.PathListSeparator)
	classPath := strings.Join([]string{appPath, sqlitePath, slf4jPath, flatLafPath}, classpathSeparator)
	args := []string{"-Dfile.encoding=UTF-8", "--add-modules", "jdk.httpserver", "-cp", classPath, required(config, "app_main_class")}
	return tools.StartDetached(javaPath, args, s.Root)
}

func (s *LauncherService) ApplyLauncherUpdate(currentExe, nextExe, root string) error {
	s.Log.Write("INFO", language.StatusLauncherUpdate)
	for attempt := 0; attempt < 30; attempt++ {
		if _, err := os.Stat(nextExe); err != nil {
			return fmt.Errorf("bekleyen launcher bulunamadi: %s", nextExe)
		}
		if err := os.Remove(currentExe); err == nil || os.IsNotExist(err) {
			if err := tools.ReplaceFile(nextExe, currentExe); err != nil {
				return err
			}
			if runtime.GOOS != "windows" {
				if err := os.Chmod(currentExe, 0o755); err != nil {
					return err
				}
			}
			return tools.StartDetached(currentExe, nil, root)
		}
		time.Sleep(200 * time.Millisecond)
	}
	return fmt.Errorf("launcher dosyasi guncelleme icin serbest birakilmadi")
}

func PendingLauncher(root string) string {
	name := "cache/TelefonRehberi.next"
	if runtime.GOOS == "windows" {
		name += ".exe"
	}
	return tools.AppPath(root, name)
}

func PrepareLauncherUpdateHelper(root, currentExe, pending string) error {
	helperName := "cache/TelefonRehberiUpdater"
	if runtime.GOOS == "windows" {
		helperName += ".exe"
	}
	helper := tools.AppPath(root, helperName)
	if err := tools.CopyFile(currentExe, helper); err != nil {
		return err
	}
	return tools.StartDetached(helper, []string{"--apply-launcher", currentExe, pending, root}, root)
}

func githubAssetURL(config map[string]string, asset string) string {
	url := required(config, "github_latest_asset_url")
	url = strings.ReplaceAll(url, "{owner}", required(config, "github_owner"))
	url = strings.ReplaceAll(url, "{repo}", required(config, "github_repo"))
	return strings.ReplaceAll(url, "{asset}", asset)
}

func required(values map[string]string, key string) string {
	value := strings.TrimSpace(values[key])
	if value == "" {
		panic("eksik ayar: " + key)
	}
	return value
}

func mapValue(values map[string]string, key, fallback string) string {
	if value := strings.TrimSpace(values[key]); value != "" {
		return value
	}
	return fallback
}

func intValue(values map[string]string, key string, fallback int) int {
	value, err := strconv.Atoi(strings.TrimSpace(values[key]))
	if err != nil {
		return fallback
	}
	return value
}

func boolValue(values map[string]string, key string, fallback bool) bool {
	value := strings.ToLower(strings.TrimSpace(values[key]))
	if value == "" {
		return fallback
	}
	return value == "true" || value == "1" || value == "yes" || value == "on"
}

func resolveAdoptiumArch(configured string) string {
	if configured != "" && configured != "auto" {
		return configured
	}
	if runtime.GOARCH == "arm64" {
		return "aarch64"
	}
	return "x64"
}

func resolveAdoptiumOS() string {
	switch runtime.GOOS {
	case "windows":
		return "windows"
	case "darwin":
		return "mac"
	default:
		return "linux"
	}
}

func launcherManifestKeys() (string, string) {
	if runtime.GOOS == "windows" {
		return "launcher_windows_asset", "launcher_windows_sha256"
	}
	if runtime.GOOS == "linux" {
		return "launcher_linux_asset", "launcher_linux_sha256"
	}
	return "launcher_asset", "launcher_sha256"
}

func findJavaHome(root, javaName string) (string, error) {
	var result string
	err := filepath.Walk(root, func(path string, info os.FileInfo, err error) error {
		if err != nil || result != "" {
			return err
		}
		if !info.IsDir() && strings.EqualFold(info.Name(), javaName) && strings.EqualFold(filepath.Base(filepath.Dir(path)), "bin") {
			result = filepath.Dir(filepath.Dir(path))
		}
		return nil
	})
	if err != nil {
		return "", err
	}
	if result == "" {
		return "", fmt.Errorf("indirilen Java paketinde %s bulunamadi", javaName)
	}
	return result, nil
}

func copyDirectory(source, destination string) error {
	return filepath.Walk(source, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		rel, err := filepath.Rel(source, path)
		if err != nil {
			return err
		}
		target := filepath.Join(destination, rel)
		if info.IsDir() {
			return os.MkdirAll(target, info.Mode())
		}
		return tools.CopyFile(path, target)
	})
}
