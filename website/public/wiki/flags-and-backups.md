## 🚩 Flags and Backups

### 🔧 What Does Flags Mean?

Flags in Instagram are like an on/off switch controlled by Meta’s servers. Instead of sending new code every time, Instagram turns features on or off remotely using these flags, without an app update.

Instafel makes these flags inside the app accessible by users and lets you change them yourself. In this way, you can try out features even if Meta hasn’t officially released them to everyone. However, some features are completely server based and you cannot enable some experimental things without Meta's roll outs.

---

### 📚 Flag & Backup Libraries

To make using feature flags easier, Instafel provides two main libraries:

- **Flag Library:**  
  A searchable collection of known flags found in Alpha versions. Each flag includes a description, which parts of the app it affects, and the versions where it appears or starts to work.

- **Backup Library:**  
  Saved flag configurations (.JSON or .IBACKUP files) that can be imported directly. These backups activate pre-tested combinations of flags known to work well together or unlock specific features.

Users can:

- Save their current configuration as a backup
- Share backups with others
- Restore from a backup to revert to a known-good state

This enables community-based experimentation and shared discoveries.

---

### 🔍 How to Access the Flag Library and Backup Library?

- The Flag Library is being established on the website (still WIP) at [Flag Library](https://instafel.mamii.dev/library/flag)
- The most used flags can also be found via the [Telegram Community](https://t.me/instafel/34335)
- The Backup Library is accessible here: [Backup Library](https://instafel.mamii.dev/library/backup)
- You can apply any backup files via the Instafel Menu.

---

## 🛠️ Accessing and Using the Instafel Menu

### 🔑 What Is the Instafel Menu?

The Instafel Menu is a special settings panel added to the regular Instagram Alpha app. It reveals powerful tools, lets you import, export, or apply backups, and control updates—giving you full access to Instagram Alpha’s unique experience.

---

### 🚪 How to Access the Instafel Menu

1. **Open Instafel App:** Launch Instafel on your device.
2. **Go to Your Profile:** Tap your profile icon at the bottom right.
3. **Open the Side Navigation Drawer:** **Press and hold** the hamburger (☰) icon in the top-right corner to open the Instafel menu.
   ![Hamburger Button](/hamburger-button.jpg)
4. **Access Instafel Features:** The advanced Instafel menu will appear, giving you access to backups, libraries, updater controls, and more.

---

### 🔍 Key Features on Instafel Menu

- **Flag Library:**  
  Explore a hand-picked database of known feature flags found by the community, with descriptions and notes on how stable they are.

- **Backup Library:**  
  Import or export backup files containing saved flag configurations. Quickly switch between setups or share your configurations.

- **Updater:**  
  Manage Over-The-Air (OTA) updates directly inside the app. Enable auto-updates or adjust checking frequency.

- **Crash Reports:**  
  Capture crash logs and send them to developers to help improve stability over time.

---

### ⚠️ Warnings and Tips

- Some flags may **cause crashes or UI glitches**; enable with caution.
- Always **backup your current flag setup** before experimenting.
- Instafel app and Instafel Backups may receive updates; enable auto updates both for app and backup files for the best experience.

---

### 🚨 Risks and Limitations

Not all flags are safe to enable. Because these features are under active development:

- Some flags may **crash the app** immediately.
- Others may **break navigation or layouts**.
- A few flags are **server-dependent** and won’t activate locally without server response.

Instafel does **not fake** server responses; it only allows local control over flags already built into the APK.

---
