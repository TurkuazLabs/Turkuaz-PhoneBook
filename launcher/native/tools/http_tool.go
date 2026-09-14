// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/http_tool.go
// 📌 Amac: Launcher internet indirme ve JSON metadata isteklerini yapar.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Timeout, User-Agent ve streaming dosya indirme destekli HTTP adaptorudur.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"time"
)

type HTTPTool struct {
	Client    *http.Client
	UserAgent string
}

func NewHTTPTool(timeoutSeconds int, userAgent string) HTTPTool {
	if timeoutSeconds < 1 {
		timeoutSeconds = 30
	}
	return HTTPTool{
		Client:    &http.Client{Timeout: time.Duration(timeoutSeconds) * time.Second},
		UserAgent: userAgent,
	}
}

func (h HTTPTool) request(url string) (*http.Response, error) {
	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}
	req.Header.Set("User-Agent", h.UserAgent)
	resp, err := h.Client.Do(req)
	if err != nil {
		return nil, err
	}
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		resp.Body.Close()
		return nil, fmt.Errorf("HTTP %d: %s", resp.StatusCode, url)
	}
	return resp, nil
}

func (h HTTPTool) GetBytes(url string) ([]byte, error) {
	resp, err := h.request(url)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	return io.ReadAll(resp.Body)
}

func (h HTTPTool) Download(url, destination string) error {
	resp, err := h.request(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	if err := os.MkdirAll(filepath.Dir(destination), 0o755); err != nil {
		return err
	}
	temporary := destination + ".download"
	_ = os.Remove(temporary)
	file, err := os.Create(temporary)
	if err != nil {
		return err
	}
	_, copyErr := io.Copy(file, resp.Body)
	closeErr := file.Close()
	if copyErr != nil {
		_ = os.Remove(temporary)
		return copyErr
	}
	if closeErr != nil {
		_ = os.Remove(temporary)
		return closeErr
	}
	return ReplaceFile(temporary, destination)
}
