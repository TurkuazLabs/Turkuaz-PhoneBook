// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/repositories/yaml_repository.go
// 📌 Amac: Launcher YAML config ve state dosyalarini okur ve yazar.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Basit key:value YAML repository katmanidir.
// Bagimli Oldugu Katman: Repository

package repositories

import (
	"bufio"
	"fmt"
	"os"
	"path/filepath"
	"strings"
)

type SimpleYAMLRepository struct{}

func (SimpleYAMLRepository) Read(path string) (map[string]string, error) {
	result := map[string]string{}
	file, err := os.Open(path)
	if err != nil {
		if os.IsNotExist(err) {
			return result, nil
		}
		return nil, err
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		if line == "" || strings.HasPrefix(line, "#") {
			continue
		}
		idx := strings.Index(line, ":")
		if idx < 1 {
			continue
		}
		key := strings.TrimSpace(line[:idx])
		value := strings.TrimSpace(line[idx+1:])
		if len(value) >= 2 {
			if (strings.HasPrefix(value, "\"") && strings.HasSuffix(value, "\"")) ||
				(strings.HasPrefix(value, "'") && strings.HasSuffix(value, "'")) {
				value = value[1 : len(value)-1]
			}
		}
		result[key] = value
	}
	return result, scanner.Err()
}

func (SimpleYAMLRepository) WriteState(path, appVersion, launcherVersion string) error {
	if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
		return err
	}
	content := fmt.Sprintf(`# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/config/state.yml
# 📌 Amac: Kurulu uygulama ve native launcher surum durumunu tutar.
# 📌 Modul - YAML
# Version: 2.0.0
# Aciklama: GitHub updater tarafindan yonetilen yerel surum bilgisidir.
# Bagimli Oldugu Katman: Repository

app_version: "%s"
launcher_version: "%s"
`, appVersion, launcherVersion)
	return os.WriteFile(path, []byte(content), 0o644)
}
