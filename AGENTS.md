<!--
Dosya Yolu: REPO_ROOT/AGENTS.md
Amac: Turkuaz-PhoneBook icin BURAK DEV STANDARD v3.0.0 proje kurallari
Modul - Documentation Markdown
Version: 3.0.0
Aciklama: Private kaynaktan pinli yerel ve public katkici uyumlu sozlesme
Bagimli Oldugu Katman: Documentation
-->

# Turkuaz-PhoneBook - Project Agent Contract

**BURAK DEV STANDARD:** v3.0.0, active, commit `72f6f642128fa29ec9a68b18881e9aa225739c9d`.
**Merkezi kaynak:** https://github.com/TurkuazSoft/DevStandards/tree/72f6f642128fa29ec9a68b18881e9aa225739c9d
**Proje profili:** `standards/security.md` (contact app).
**Roadmap:** Mevcut README, docs, CONTRIBUTING, issue/PR ve kod kontratlari.

Merkezi repo private oldugu icin dis katkicilar ona erisemeyebilir. Bu yerel ozet tek basina temel kurallari aktarir; erisilemeyen merkezi kaynak okundu diye iddia edilmez.

## Temel gelistirme ve guvenlik ilkeleri

- Once ilgili repo AGENTS, git durumu, acik PR/issue, mevcut kaynak ve CI sonucunu incele.
- "Devam et" son dogrulanmis durumdan ilerle demektir; mevcut davranisi ve kullanici roadmap'ini koru.
- Controller request/response, Service is kurallari, Repository/Model veri, Tool/Adapter harici entegrasyon, View arayuz, Language lokalizasyondan sorumludur. Projenin native klasor mimarisini koru.
- Teknik dosyada format izin veriyorsa Dosya Yolu, Amac, Modul - FileType, Version, Aciklama ve Bagimli Oldugu Katman metaverisini kullan; JSON/binary/manifest gibi formatlari bozma.
- Gercek dosya yolu bilinmiyorsa REPO_ROOT/goreli-yol; magic string, hardcoded secret ve dogrulanmamis uyumluluk iddiasi yok.
- Secret ve kullanici verilerini loglama. Test, build, smoke, E2E ve release dogrulamasini ayri tut; test edilmemis sonucu PASS sayma.
- Guvenilmeyen dis/fork PR kodunu guvenilir kisisel self-hosted runner'da calistirma.
- CI artifakti production release degildir. Onaysiz migration, degisiklik, release ve baska repo yazimi yapma.

## contact app

Contacts, backup, mobile and address book PII must be protected; import/export regression and permissions tested.

Sonraki merkezi standart versiyonlarini bu projeye ayri kontrollu PR ile tasi.
