// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/log_tool.go
// 📌 Amac: Native launcher calisma loglarini gunluk dosyaya yazar.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Launcher diagnostik log adaptorudur.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"fmt"
	"os"
	"path/filepath"
	"sync"
	"time"
)

type Logger struct {
	path string
	mu   sync.Mutex
}

func NewLogger(root string) Logger {
	name := "launcher-" + time.Now().Format("20060102") + ".log"
	return Logger{path: filepath.Join(root, "logs", name)}
}

func (l *Logger) Write(level, message string) {
	l.mu.Lock()
	defer l.mu.Unlock()
	_ = os.MkdirAll(filepath.Dir(l.path), 0o755)
	file, err := os.OpenFile(l.path, os.O_CREATE|os.O_APPEND|os.O_WRONLY, 0o644)
	if err != nil {
		return
	}
	defer file.Close()
	_, _ = fmt.Fprintf(file, "[%s] [%s] %s\n", time.Now().Format("2006-01-02 15:04:05"), level, message)
}
