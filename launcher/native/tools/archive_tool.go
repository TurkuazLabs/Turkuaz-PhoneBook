// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/archive_tool.go
// 📌 Amac: Portable Java ZIP arsivini guvenli acar.
// 📌 Modul - Go
// Version: 2.1.0
// Aciklama: ZIP ve TAR.GZ path traversal korumali archive adaptorudur.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"archive/tar"
	"archive/zip"
	"compress/gzip"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"
)

func ExtractZIP(source, destination string) error {
	reader, err := zip.OpenReader(source)
	if err != nil {
		return err
	}
	defer reader.Close()
	if err := os.MkdirAll(destination, 0o755); err != nil {
		return err
	}
	cleanRoot := filepath.Clean(destination) + string(os.PathSeparator)
	for _, item := range reader.File {
		target := filepath.Join(destination, item.Name)
		cleanTarget := filepath.Clean(target)
		if !strings.HasPrefix(cleanTarget+string(os.PathSeparator), cleanRoot) {
			return fmt.Errorf("guvensiz ZIP girdisi: %s", item.Name)
		}
		if item.FileInfo().IsDir() {
			if err := os.MkdirAll(cleanTarget, 0o755); err != nil {
				return err
			}
			continue
		}
		if err := os.MkdirAll(filepath.Dir(cleanTarget), 0o755); err != nil {
			return err
		}
		in, err := item.Open()
		if err != nil {
			return err
		}
		out, err := os.OpenFile(cleanTarget, os.O_CREATE|os.O_TRUNC|os.O_WRONLY, item.Mode())
		if err != nil {
			in.Close()
			return err
		}
		_, copyErr := io.Copy(out, in)
		closeErr := out.Close()
		in.Close()
		if copyErr != nil {
			return copyErr
		}
		if closeErr != nil {
			return closeErr
		}
	}
	return nil
}

// ExtractTarGz guvenli TAR.GZ arsivlerini destination altina acar.
func ExtractTarGz(source, destination string) error {
	file, err := os.Open(source)
	if err != nil {
		return err
	}
	defer file.Close()

	gzipReader, err := gzip.NewReader(file)
	if err != nil {
		return err
	}
	defer gzipReader.Close()

	if err := os.MkdirAll(destination, 0o755); err != nil {
		return err
	}
	cleanRoot := filepath.Clean(destination) + string(os.PathSeparator)
	tarReader := tar.NewReader(gzipReader)
	for {
		header, err := tarReader.Next()
		if err == io.EOF {
			break
		}
		if err != nil {
			return err
		}
		target := filepath.Join(destination, header.Name)
		cleanTarget := filepath.Clean(target)
		if !strings.HasPrefix(cleanTarget+string(os.PathSeparator), cleanRoot) {
			return fmt.Errorf("guvensiz TAR girdisi: %s", header.Name)
		}
		switch header.Typeflag {
		case tar.TypeDir:
			if err := os.MkdirAll(cleanTarget, os.FileMode(header.Mode)); err != nil {
				return err
			}
		case tar.TypeReg, tar.TypeRegA:
			if err := os.MkdirAll(filepath.Dir(cleanTarget), 0o755); err != nil {
				return err
			}
			out, err := os.OpenFile(cleanTarget, os.O_CREATE|os.O_TRUNC|os.O_WRONLY, os.FileMode(header.Mode))
			if err != nil {
				return err
			}
			_, copyErr := io.Copy(out, tarReader)
			closeErr := out.Close()
			if copyErr != nil {
				return copyErr
			}
			if closeErr != nil {
				return closeErr
			}
		case tar.TypeSymlink:
			if err := os.MkdirAll(filepath.Dir(cleanTarget), 0o755); err != nil {
				return err
			}
			resolvedLink := filepath.Clean(filepath.Join(filepath.Dir(cleanTarget), header.Linkname))
			if !strings.HasPrefix(resolvedLink+string(os.PathSeparator), cleanRoot) {
				return fmt.Errorf("guvensiz TAR symlink girdisi: %s", header.Name)
			}
			_ = os.Remove(cleanTarget)
			if err := os.Symlink(header.Linkname, cleanTarget); err != nil {
				return err
			}
		}
	}
	return nil
}
