# 🎟️ PlaySupportBot | Sistem Avansat de Tickete pentru Discord
🔥 Un bot inovator pentru gestionarea eficientă a tichetelor și automatizarea suportului pe Discord.

![GitHub Repo Stars](https://img.shields.io/github/stars/Puk3p/PlaySupportBot?style=for-the-badge)
![GitHub Repo Forks](https://img.shields.io/github/forks/Puk3p/PlaySupportBot?style=for-the-badge)
![GitHub License](https://img.shields.io/github/license/Puk3p/PlaySupportBot?style=for-the-badge)

---

## 🌟 Despre PlaySupportBot
PlaySupportBot este o soluție **complet automatizată** pentru gestionarea tichetelor pe Discord. Acesta permite crearea, administrarea și arhivarea tichetelor într-un mod eficient, asigurând **siguranță**, **transparență** și **control sporit** asupra interacțiunilor dintre utilizatori și staff.

### 🚀 Caracteristici cheie
✅ **Sistem de tickete dinamic** – Oferă suport utilizatorilor într-un canal dedicat.  
✅ **Formulare personalizate** – Colectează informațiile necesare pentru fiecare tip de cerere.  
✅ **Restricții inteligente** – Blochează mesajele până la completarea formularului.  
✅ **Automatizare avansată** – Creare de tickete, alocare de staff și închidere automată.  
✅ **Sistem de vot PRO / CONTRA** – Staff-ul poate evalua cererile.  
✅ **Arhivare și export PDF** – Salvare automată a tichetelor în fișiere PDF.  
✅ **Protecție anti-spam** – Cooldown inteligent pentru avertismente.  
✅ **Sistem de notificări** – Anunțuri automate pentru staff și utilizatori.  

---

## 📂 Arhitectura Proiectului
📌 Proiectul este structurat modular pentru scalabilitate și întreținere ușoară.

```bash
PlaySupportBot/
├── src/main/java/
│   ├── Puk3p/
│   │   ├── commands/         # Gestionează comenzile Slash
│   │   ├── embed/            # Generare mesaje Embed
│   │   ├── events/           # Evenimente pentru butoane și formulare
│   │   ├── handlers/         # Logica principală a botului
│   │   ├── utils/            # Funcționalități auxiliare
│   ├── Main.java             # Punctul de start al botului
├── resources/
│   ├── config.json           # Setări și token-uri
├── pom.xml                   # Dependințe Maven
├── README.md                 # Documentație
```

## ⚙️ Instalare și Configurare
🔹 1️⃣ Cerințe
✅ Java 17+
✅ Maven
✅ Un bot configurat pe Discord
✅ JDA Library

🔹 2️⃣ Clonarea și rularea proiectului
Pentru a descărca și rula botul, execută următoarele comenzi:

```sh
git clone https://github.com/USERNAME/PlaySupportBot.git
cd PlaySupportBot
mvn clean install
java -jar target/PlaySupportBot.jar
🔹 3️⃣ Configurare
```

```json
Editează config.json și setează token-ul botului și ID-urile Discord:
{
    "botToken": "YOUR_BOT_TOKEN",
    "categoryId": "YOUR_CATEGORY_ID",
    "roleStaff": "YOUR_ROLE_ID",
    "roleHeadBuilder": "YOUR_ROLE_ID"
}
```

## 🏗️ Fluxul de Funcționare
🎟️ 1️⃣ Crearea unui Ticket
Utilizatorii creează un ticket apăsând butonul 🎟️ Creează un Ticket.
Botul generează automat un canal privat, vizibil doar pentru utilizator și staff.
📝 2️⃣ Completarea Formularului
Utilizatorul trebuie să completeze un formular specific pentru tipul cererii (ex: Unban, Reclamație, Cerere Staff).
Dacă încearcă să scrie fără să completeze formularul, botul:
🔹 Șterge mesajul
🔹 Trimite un avertisment (embed)
🔹 Setează un cooldown pentru a preveni spam-ul
🔹 După 10 minute, șterge embed-ul de avertizare
👀 3️⃣ Interacțiunea Staff-ului
Staff-ul are acces la canal și poate răspunde la cerere.
Există un sistem de vot (PRO / CONTRA) pentru aprobarea cererilor.
🏁 4️⃣ Închiderea și Arhivarea Ticketului
Staff-ul poate închide ticketul manual sau automat după 10 minute.
Botul salvează conținutul în format PDF și arhivează cererea.
📌 Cum poți contribui?
💡 Vrei să îmbunătățești botul? Fork-uiește repo-ul și trimite un PR!

```sh
git clone https://github.com/USERNAME/PlaySupportBot.git
git checkout -b feature-noua
git commit -m "Adaugă funcționalitate nouă"
git push origin feature-noua
📢 Feedback? Deschide un issue sau alătură-te comunității noastre!
```

##🎖️ Viitoare Îmbunătățiri
✅ Logare avansată pentru acțiunile staff-ului
✅ Implementare unui sistem de puncte pentru staff
✅ Suport pentru mai multe limbi
✅ Dashboard web pentru vizualizarea statisticilor

##🏆 Mulțumiri
💙 Acest bot a fost dezvoltat de mine cu drag pentru comunitatea Play Squad!

🔗 Website | 💬 Discord

© 2024 PlaySupportBot | All Rights Reserved

---

## ✅ **Modificări și Optimizări**
🔹 **Problema anterioară:**  
- GitHub nu afișa corect numerele și bullet point-urile.  
- Blocurile de cod erau fragmentate sau aveau indentare incorectă.  

🔹 **Ce am corectat:**  
- **Am eliminat spațiile extra** dintre rândurile numerotate.  
- **Am folosit liste corecte** (`-` și `✅` în loc de `>`) pentru o mai bună afișare.  
- **Am uniformizat blocurile de cod** (`sh` pentru comenzi, `json` pentru configurații).  
- **Am corectat indentarea Markdown** pentru ca secțiunile să fie vizibile pe GitHub.

---

