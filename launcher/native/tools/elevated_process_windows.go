// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/elevated_process_windows.go
// 📌 Amac: Windows kurulum guncellemesini UAC ile yonetici olarak baslatir.
// 📌 Tool - Go
// Version: 1.0.0
// Aciklama: Inno Setup EXE dosyasini ShellExecuteW runas ile konsolsuz calistirir.
// Bagimli Oldugu Katman: Tool

//go:build windows

package tools

import (
	"fmt"
	"strings"
	"syscall"
	"unsafe"
)

const shellExecuteSuccessThreshold = 32

var (
	shell32DLL        = syscall.NewLazyDLL("shell32.dll")
	shellExecuteWProc = shell32DLL.NewProc("ShellExecuteW")
)

func StartElevated(executable string, args []string, workingDirectory string) error {
	operation, _ := syscall.UTF16PtrFromString("runas")
	file, _ := syscall.UTF16PtrFromString(executable)
	parameters, _ := syscall.UTF16PtrFromString(joinWindowsCommandLine(args))
	directory, _ := syscall.UTF16PtrFromString(workingDirectory)

	result, _, callErr := shellExecuteWProc.Call(
		0,
		uintptr(unsafe.Pointer(operation)),
		uintptr(unsafe.Pointer(file)),
		uintptr(unsafe.Pointer(parameters)),
		uintptr(unsafe.Pointer(directory)),
		uintptr(1),
	)
	if result <= shellExecuteSuccessThreshold {
		if callErr != syscall.Errno(0) {
			return fmt.Errorf("yonetici yetkili process baslatilamadi: %w", callErr)
		}
		return fmt.Errorf("yonetici yetkili process baslatilamadi: ShellExecute kodu %d", result)
	}
	return nil
}

func joinWindowsCommandLine(args []string) string {
	quoted := make([]string, 0, len(args))
	for _, arg := range args {
		if arg == "" || strings.ContainsAny(arg, " \t\"") {
			escaped := strings.ReplaceAll(arg, `"`, `\"`)
			quoted = append(quoted, `"`+escaped+`"`)
		} else {
			quoted = append(quoted, arg)
		}
	}
	return strings.Join(quoted, " ")
}
