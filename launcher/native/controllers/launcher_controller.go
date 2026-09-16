// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/controllers/launcher_controller.go
// 📌 Amac: Native launcher komut satiri girisini alir ve servisi cagirir.
// 📌 Modul - Go
// Version: 2.2.0
// Aciklama: Turkuaz-PhoneBook launcher akisinda kullaniciya gosterilen controller hatalarini Language katmanindan yerellestirir.
// Bagimli Oldugu Katman: Controller | Service | Tool | View | Language

package controllers

import (
	"fmt"
	"os"

	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/language"
	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/services"
	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/tools"
	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/views"
)

type LauncherController struct{}

func (LauncherController) Run(args []string) (exitCode int) {
	defer func() {
		if recovered := recover(); recovered != nil {
			views.ShowError(language.ErrorTitle, fmt.Sprintf(language.LauncherErrorFormat, recovered))
			exitCode = 1
		}
	}()

	if len(args) >= 5 && args[1] == "--apply-launcher" {
		root := args[4]
		service := services.NewLauncherService(root, views.NewSilentSplash())
		if err := service.ApplyLauncherUpdate(args[2], args[3], root); err != nil {
			views.ShowError(language.ErrorTitle, err.Error())
			return 1
		}
		return 0
	}

	root, err := tools.ExecutableRoot()
	if err != nil {
		views.ShowError(language.ErrorTitle, err.Error())
		return 1
	}
	currentExe, err := os.Executable()
	if err != nil {
		views.ShowError(language.ErrorTitle, err.Error())
		return 1
	}
	pending := services.PendingLauncher(root)
	if !tools.IsInstalledDistribution(root) {
		if _, err := os.Stat(pending); err == nil {
			if err := services.PrepareLauncherUpdateHelper(root, currentExe, pending); err != nil {
				views.ShowError(language.ErrorTitle, fmt.Sprintf(language.LauncherUpdateError, err))
				return 1
			}
			return 0
		}
	}

	splash := views.NewSplash()
	service := services.NewLauncherService(root, splash)
	if err := service.Run(); err != nil {
		splash.Close()
		views.ShowError(language.ErrorTitle, err.Error())
		return 1
	}
	splash.Close()
	return 0
}
