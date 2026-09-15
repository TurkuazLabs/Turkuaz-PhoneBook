// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/views/splash_windows.go
// 📌 Amac: Windows native Turkuaz splash ekranini cizer ve hata dialogunu gosterir.
// 📌 Modul - Go
// Version: 2.1.0
// Aciklama: Konsolsuz Win32 acilis ekraninda dil bagimsiz Turkuaz marka adini ve MessageBox view katmanini kullanir.
// Bagimli Oldugu Katman: View | Language

//go:build windows

package views

import (
	"os"
	"path/filepath"
	"runtime"
	"sync"
	"syscall"
	"unsafe"
)

const (
	wmDestroy      = 0x0002
	wmPaint        = 0x000F
	wmClose        = 0x0010
	wsPopup        = 0x80000000
	wsExToolWindow = 0x00000080
	wsExTopmost    = 0x00000008
	swShow         = 5
	dtCenter       = 0x00000001
	dtVCenter      = 0x00000004
	dtSingleLine   = 0x00000020
	transparent    = 1
	mbIconError    = 0x00000010
	imageIcon      = 1
	lrLoadFromFile = 0x00000010
	diNormal       = 0x0003
)

var (
	user32               = syscall.NewLazyDLL("user32.dll")
	gdi32                = syscall.NewLazyDLL("gdi32.dll")
	kernel32             = syscall.NewLazyDLL("kernel32.dll")
	procRegisterClassEx  = user32.NewProc("RegisterClassExW")
	procCreateWindowEx   = user32.NewProc("CreateWindowExW")
	procDefWindowProc    = user32.NewProc("DefWindowProcW")
	procShowWindow       = user32.NewProc("ShowWindow")
	procUpdateWindow     = user32.NewProc("UpdateWindow")
	procGetMessage       = user32.NewProc("GetMessageW")
	procTranslateMsg     = user32.NewProc("TranslateMessage")
	procDispatchMessage  = user32.NewProc("DispatchMessageW")
	procPostQuitMessage  = user32.NewProc("PostQuitMessage")
	procBeginPaint       = user32.NewProc("BeginPaint")
	procEndPaint         = user32.NewProc("EndPaint")
	procFillRect         = user32.NewProc("FillRect")
	procDrawText         = user32.NewProc("DrawTextW")
	procInvalidateRect   = user32.NewProc("InvalidateRect")
	procGetSystemMetrics = user32.NewProc("GetSystemMetrics")
	procMessageBox       = user32.NewProc("MessageBoxW")
	procLoadImage        = user32.NewProc("LoadImageW")
	procDrawIconEx       = user32.NewProc("DrawIconEx")
	procDestroyIcon      = user32.NewProc("DestroyIcon")
	procCreateSolidBrush = gdi32.NewProc("CreateSolidBrush")
	procDeleteObject     = gdi32.NewProc("DeleteObject")
	procCreateFont       = gdi32.NewProc("CreateFontW")
	procSelectObject     = gdi32.NewProc("SelectObject")
	procSetTextColor     = gdi32.NewProc("SetTextColor")
	procSetBkMode        = gdi32.NewProc("SetBkMode")
	procGetModuleHandle  = kernel32.NewProc("GetModuleHandleW")
)

type point struct{ X, Y int32 }
type rect struct{ Left, Top, Right, Bottom int32 }
type paintStruct struct {
	Hdc         uintptr
	Erase       int32
	Paint       rect
	Restore     int32
	IncUpdate   int32
	RgbReserved [32]byte
}
type msg struct {
	Hwnd    uintptr
	Message uint32
	WParam  uintptr
	LParam  uintptr
	Time    uint32
	Pt      point
}
type wndClassEx struct {
	Size, Style                        uint32
	WndProc                            uintptr
	ClsExtra, WndExtra                 int32
	Instance, Icon, Cursor, Background uintptr
	MenuName, ClassName                *uint16
	IconSm                             uintptr
}

type windowsSplash struct {
	mu      sync.RWMutex
	hwnd    uintptr
	status  string
	percent int
	ready   chan struct{}
}

var activeSplash *windowsSplash

func rgb(r, g, b byte) uintptr      { return uintptr(r) | uintptr(g)<<8 | uintptr(b)<<16 }
func utf16ptr(value string) *uint16 { p, _ := syscall.UTF16PtrFromString(value); return p }

func NewSplash() Splash {
	s := &windowsSplash{status: "Baslatiliyor...", percent: 5, ready: make(chan struct{})}
	activeSplash = s
	go s.run()
	<-s.ready
	return s
}

func (s *windowsSplash) run() {
	runtime.LockOSThread()
	defer runtime.UnlockOSThread()
	instance, _, _ := procGetModuleHandle.Call(0)
	className := utf16ptr("TurkuazTelefonRehberiSplash")
	wc := wndClassEx{Size: uint32(unsafe.Sizeof(wndClassEx{})), WndProc: syscall.NewCallback(windowProc), Instance: instance, ClassName: className}
	procRegisterClassEx.Call(uintptr(unsafe.Pointer(&wc)))

	const width, height = 560, 330
	sw, _, _ := procGetSystemMetrics.Call(0)
	sh, _, _ := procGetSystemMetrics.Call(1)
	x := int32((int(sw) - width) / 2)
	y := int32((int(sh) - height) / 2)
	hwnd, _, _ := procCreateWindowEx.Call(
		wsExToolWindow|wsExTopmost,
		uintptr(unsafe.Pointer(className)), uintptr(unsafe.Pointer(utf16ptr("Turkuaz"))),
		wsPopup, uintptr(x), uintptr(y), width, height,
		0, 0, instance, 0,
	)
	s.mu.Lock()
	s.hwnd = hwnd
	s.mu.Unlock()
	close(s.ready)
	if hwnd == 0 {
		return
	}
	procShowWindow.Call(hwnd, swShow)
	procUpdateWindow.Call(hwnd)
	var m msg
	for {
		ret, _, _ := procGetMessage.Call(uintptr(unsafe.Pointer(&m)), 0, 0, 0)
		if int32(ret) <= 0 {
			break
		}
		procTranslateMsg.Call(uintptr(unsafe.Pointer(&m)))
		procDispatchMessage.Call(uintptr(unsafe.Pointer(&m)))
	}
}

func (s *windowsSplash) Update(status string, percent int) {
	if percent < 0 {
		percent = 0
	}
	if percent > 100 {
		percent = 100
	}
	s.mu.Lock()
	s.status = status
	s.percent = percent
	hwnd := s.hwnd
	s.mu.Unlock()
	if hwnd != 0 {
		procInvalidateRect.Call(hwnd, 0, 1)
	}
}

func (s *windowsSplash) Close() {
	s.mu.RLock()
	hwnd := s.hwnd
	s.mu.RUnlock()
	if hwnd != 0 {
		user32.NewProc("PostMessageW").Call(hwnd, wmClose, 0, 0)
	}
}

func windowProc(hwnd uintptr, message uint32, wparam, lparam uintptr) uintptr {
	switch message {
	case wmPaint:
		paintSplash(hwnd)
		return 0
	case wmClose:
		user32.NewProc("DestroyWindow").Call(hwnd)
		return 0
	case wmDestroy:
		procPostQuitMessage.Call(0)
		return 0
	default:
		ret, _, _ := procDefWindowProc.Call(hwnd, uintptr(message), wparam, lparam)
		return ret
	}
}

func paintSplash(hwnd uintptr) {
	var ps paintStruct
	hdc, _, _ := procBeginPaint.Call(hwnd, uintptr(unsafe.Pointer(&ps)))
	if hdc == 0 {
		return
	}
	defer procEndPaint.Call(hwnd, uintptr(unsafe.Pointer(&ps)))

	bg, _, _ := procCreateSolidBrush.Call(rgb(10, 31, 37))
	full := rect{0, 0, 560, 330}
	procFillRect.Call(hdc, uintptr(unsafe.Pointer(&full)), bg)
	procDeleteObject.Call(bg)

	accent, _, _ := procCreateSolidBrush.Call(rgb(25, 194, 178))
	bar := rect{0, 0, 560, 8}
	procFillRect.Call(hdc, uintptr(unsafe.Pointer(&bar)), accent)

	if executable, err := os.Executable(); err == nil {
		iconPath := filepath.Join(filepath.Dir(executable), "assets", "branding", "app-icon.ico")
		if _, err := os.Stat(iconPath); err == nil {
			icon, _, _ := procLoadImage.Call(0, uintptr(unsafe.Pointer(utf16ptr(iconPath))), imageIcon, 76, 76, lrLoadFromFile)
			if icon != 0 {
				procDrawIconEx.Call(hdc, 242, 38, icon, 76, 76, 0, 0, diNormal)
				procDestroyIcon.Call(icon)
			}
		}
	}

	procSetBkMode.Call(hdc, transparent)
	procSetTextColor.Call(hdc, rgb(236, 253, 250))
	fontTitle, _, _ := procCreateFont.Call(^uintptr(31)+1, 0, 0, 0, 700, 0, 0, 0, 1, 0, 0, 5, 0, uintptr(unsafe.Pointer(utf16ptr("Segoe UI"))))
	old, _, _ := procSelectObject.Call(hdc, fontTitle)
	titleRect := rect{54, 122, 506, 165}
	procDrawText.Call(hdc, uintptr(unsafe.Pointer(utf16ptr("Turkuaz"))), ^uintptr(0), uintptr(unsafe.Pointer(&titleRect)), dtCenter|dtVCenter|dtSingleLine)
	procSelectObject.Call(hdc, old)
	procDeleteObject.Call(fontTitle)

	procSetTextColor.Call(hdc, rgb(150, 178, 181))
	fontSmall, _, _ := procCreateFont.Call(^uintptr(16)+1, 0, 0, 0, 400, 0, 0, 0, 1, 0, 0, 5, 0, uintptr(unsafe.Pointer(utf16ptr("Segoe UI"))))
	old, _, _ = procSelectObject.Call(hdc, fontSmall)
	subRect := rect{45, 166, 515, 194}
	procDrawText.Call(hdc, uintptr(unsafe.Pointer(utf16ptr("Kisileriniz guvende, her zaman yaninizda"))), ^uintptr(0), uintptr(unsafe.Pointer(&subRect)), dtCenter|dtVCenter|dtSingleLine)

	status := "Baslatiliyor..."
	percent := 5
	if activeSplash != nil {
		activeSplash.mu.RLock()
		status = activeSplash.status
		percent = activeSplash.percent
		activeSplash.mu.RUnlock()
	}
	procSetTextColor.Call(hdc, rgb(211, 232, 230))
	statusRect := rect{58, 224, 502, 252}
	procDrawText.Call(hdc, uintptr(unsafe.Pointer(utf16ptr(status))), ^uintptr(0), uintptr(unsafe.Pointer(&statusRect)), dtCenter|dtVCenter|dtSingleLine)
	procSelectObject.Call(hdc, old)
	procDeleteObject.Call(fontSmall)

	track, _, _ := procCreateSolidBrush.Call(rgb(31, 62, 67))
	trackRect := rect{76, 278, 484, 288}
	procFillRect.Call(hdc, uintptr(unsafe.Pointer(&trackRect)), track)
	procDeleteObject.Call(track)
	fill, _, _ := procCreateSolidBrush.Call(rgb(25, 194, 178))
	fillWidth := int32((408 * percent) / 100)
	fillRect := rect{76, 278, 76 + fillWidth, 288}
	procFillRect.Call(hdc, uintptr(unsafe.Pointer(&fillRect)), fill)
	procDeleteObject.Call(fill)
}

func ShowError(title, message string) {
	procMessageBox.Call(0, uintptr(unsafe.Pointer(utf16ptr(message))), uintptr(unsafe.Pointer(utf16ptr(title))), mbIconError)
}
