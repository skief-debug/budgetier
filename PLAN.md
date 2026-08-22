# BudgeTier — Offener Plan & TODO

Stand: 22.08.2026 | Pause läuft, hier geht's weiter wenn zurück.

---

## ✅ Erledigt heute

- [x] Logo-Bug gefixt (Alligator wurde größer beim App-Schließen)
- [x] Alle Mipmap-Icons korrekt für jede Dichte generiert (mdpi → xxxhdpi)
- [x] Auto-Updater gebaut (UpdateManager + UpdateDialog + GitHub Actions Workflow)
- [x] AndroidManifest: INTERNET + FileProvider Permissions
- [x] App kompiliert erfolgreich

---

## 🔧 Als erstes nach der Pause: GitHub Setup (5 Min)

Das muss noch gemacht werden damit der Auto-Updater funktioniert.

### Schritt 1 — GitHub CLI einloggen
```powershell
gh auth login
```
→ Browser öffnet sich, GitHub-Account verbinden

### Schritt 2 — Repo erstellen & Code pushen
```powershell
cd "C:\Users\ansga\Desktop\budget app"
gh repo create budgetier --private --push --source .
```

### Schritt 3 — GitHub-Username eintragen
Datei öffnen:
`app\src\main\java\com\budgettracker\app\utils\UpdateManager.kt`

Zeile 15 anpassen:
```kotlin
const val GITHUB_REPO = "DEIN_GITHUB_USERNAME/budgetier"  // ← hier Username eintragen
```

### Schritt 4 — Neu bauen & auf Handy installieren
```powershell
.\gradlew assembleDebug
```
Danach APK auf Handy installieren.

### Schritt 5 — Ersten Release testen
```powershell
git add .
git commit -m "fix: GitHub username konfiguriert"
git tag v1
git push && git push --tags
```
→ GitHub Actions baut APK (~5 Min) → App öffnen → Update-Dialog testen

---

## 🎨 UI/UX Verbesserungen (geplant)

Die App läuft gut, aber folgende Dinge können noch verbessert werden:

### Hohe Priorität

- [ ] **Splash Screen** — statt schwarzem Flackern beim Start einen sauberen
      Splash mit dem Alligator-Logo (Android 12+ SplashScreen API)

- [ ] **Haptisches Feedback** — bei Buttons, Swipe-Aktionen etc.
      `HapticFeedback` in Compose

- [ ] **Leerer Zustand (Empty States)** — wenn noch keine Transaktionen da sind,
      eine schöne Illustration + Hinweistext anzeigen statt leere Liste

- [ ] **Kategorie-Icons** — statt nur Emojis echte anpassbare Icons pro Kategorie

### Mittlere Priorität

- [ ] **Animations** — sanfte Übergänge zwischen Dashboard ↔ Statistiken Tab
      (Compose AnimatedContent / SharedTransitionLayout)

- [ ] **Pull-to-Refresh** — Swipe-Down im Dashboard zum manuellen Update-Check

- [ ] **Swipe to Delete** — Transaktionen per Wischen löschen
      (SwipeToDismiss in Compose)

- [ ] **Kategorien-Reihenfolge** — Drag & Drop zum Umsortieren der Kategorien

- [ ] **Budgetwarnung** — Push-Benachrichtigung wenn eine Kategorie 80% erreicht
      (WorkManager + NotificationManager)

### Nice to Have

- [ ] **Widget** — Kleines Home-Screen Widget mit aktuellem Gesamtbudget
      (Glance API für Android Widgets)

- [ ] **Dark/Light Mode** — Toggle in Settings (aktuell nur Dark Mode fest)

- [ ] **Backup/Export** — Transaktionen als CSV oder JSON exportieren
      (aktuell nur Import via Smart-Input)

- [ ] **Wiederkehrende Buchungen** — z.B. Netflix monatlich automatisch buchen

---

## 🚀 Update-Workflow (nach GitHub Setup)

Sobald eine Änderung fertig ist:

```powershell
git add .
git commit -m "feat: Beschreibung der Änderung"
git tag v2          # ← Versionsnummer immer hochzählen!
git push && git push --tags
```

GitHub Actions baut die APK automatisch.
App beim nächsten Start: 🚀 Update-Dialog erscheint.

---

## 📁 Wichtige Dateien

| Datei | Zweck |
|---|---|
| `app\src\main\java\...\utils\UpdateManager.kt` | GitHub API + Download Logik |
| `app\src\main\java\...\ui\components\UpdateDialog.kt` | Update-Dialog UI |
| `.github\workflows\release.yml` | GitHub Actions Build-Pipeline |
| `app\build.gradle.kts` | versionCode / versionName |
| `app\src\main\AndroidManifest.xml` | Permissions + FileProvider |

