# Repozitorij za projekt iz predmeta Razvoj mobilnih aplikacija i servisa

---

## HR Aplikacija

Studenti:  
`Ajla Bulic`  
`Alejna Hodzic`

---

### 📱 Opis aplikacije

Aplikacija je namijenjena za digitalizaciju i upravljanje zahtjevima zaposlenika za godišnji odmor ili plaćeno odsustvo. Sistem podržava tri tipa korisnika sa različitim pravima pristupa (Role-Based Access Control):

1.  **Zaposlenik (Employee):**
    * Podnosi zahtjeve za odsustvo.
    * Ima uvid u broj preostalih dana odmora.
    * Prati status svojih zahtjeva (Na čekanju / Odobreno / Odbijeno).
2.  **Sekretar (Secretary):**
    * Prva linija obrade zahtjeva.
    * Pregleda pristigle zahtjeve svih zaposlenika.
    * Filtrira zahtjeve i prosljeđuje validne zahtjeve Dekanu.
3.  **Dekan (Dean):**
    * Ima ovlasti za konačno odobrenje ili odbijanje zahtjeva.
    * Vidi samo zahtjeve koji su prošli provjeru sekretara.

---

### Opis implementiranih funkcionalnosti:

#### Alejna:

> **Navigation**
> 
> Najprije je dodan sealed interface (u kombinaciji sa Serialization-om)
> kako bi se type-safe
> moglo navigirati kroz ekrane s minimanlim mogucnostima za
> pravljenje greske. 
> 
> Sealed interface sadrzi (zasad) samo singletone za
> Home, Login i Create Request screen-ove. Po prosirenju
> aplikacije biti ce dodano jos singletona.
> 
> Funkcija `AppNavigation` je esencijalno wrapper za 
> `NavHost` koja nam omogucava jednostavniji i 
> modularniji pristup navigaciji kako se NavHost ne bi 
> morao implementirati u MainActivity.kt. 
> 
> Navigacija krece od Login screen-a i za njega je
> definisana samo jedna ruta i to prema Home screen-u.
> 
> Navigacija kroz ekrane je implementrirana tako da se
> pozivu compose funkcije za screen proslijedi lambda
> u kojoj je definisana navigacija putem funkcije .navigate().
> 
> Ta lambda propagira u Compose funkciji do UI elementa
> koji na event treba da istu trigeruje i izvrsi.
> 
> Isti postupak je odradjen i za ostale Screenove. 
> 

> **Google Authentication**
> 
> Dodana funkcija za genersanje nasumicnog nonce-a, koji 
> je zapravo enkodirani string i sluzi kao sigurnosni mehanizam.
> 
> Dalje u procesu Google autentikacije slijedi konfiguracija
> zahtjeva prema Google serveru koristeci `Credential Manager`.
> 
> `setFilterByAuthorizedAccounts` je postavljen na false
> kako bi se omogucio prikaz svih accounta usera.
> 
> `setServerClientId` funkciji prosljedjujemo WEB client id
> koji se dobije preko Google Cloud Console i sluzi za
> dobijanje tokena i verifikaciju da zahtjev dolazi od
> registrovane aplikacije. Smjesten je u resursima kako ne
> bi bio hard-coded u aplikaciji, a da ipak nema potrebe za
> dodatnom distribucijom podataka kako bi se aplikacija
> mogla samo pokrenuti.
> 
> Potom s enapravi zahtjev i dohvate kredencijali 
> pri cemu se koristi i app Context zbog cega isti mora
> propagirati kroz login view model do ove funkcije (kako
> bi bio koristen isti kontekst).
> 
> Dodane su klase UserProfile i UserManager. UserProfile
> sluzi kako bismo povratne podatke iz kredencijala 
> spremili, a UserManager nam sluzi kao singleton koji
> cuva trenutnog korisnika prijavljenog na aplikaciju.
> Zasad na taj nacin imamo korisnika u aplikaciji obzirom
> da za ovu fazu nije bilo zahtjevano pisanje u bazu.
> U narednim fazama jednom kada se dobiju kredencijali
> isti ce biti spremljeni na Firebase Firestore.
> 

> **Offline Firebase Firestore**
> U init bloku se inicijaliziraju postavke za Firestore offline bazu podataka
> . Postavi se cache na 100MB i te portavke se apliciraju na 
> vec inicijaliziranu bazu podataka.
> 
> Dalje je samo bilo potrebno na ekranu prikazivati sve 
> ono sto se nalazi u cache-u kada snapshot listener ne
> dobija podatke od baze. Poziva se `snapshot.metadata.isFromCache`.

> **Organizacija podataka u bazi**
> Napravila sam da je moguce odabrati vise rangeova 
> datuma (data klasa uzima listu Datuma), ali to jos nije implementirano
> na UI, samo u pozadini. Dodala sam novi tip za spremanje podataka vezanih
> za fajlove koji se mogu ucitati kao dokaz na zahtjev za odsustvo.

> **Support za API pozive**
> Dodan OkHttpKlijent, RetrofitInstance, GoogleAPIInterface,
> repozitorij za google podatke i pomocna funkcija za OkHttpClient.
> Slicno zadaci3, na kraju nije iskoristeno ni za sta, jer smo podatak
> vec imale kroz kredencijale (meni je slika bila null jer je postavljena
> na private pa sam debuggirala to dug vremenski period, jer nisam znala sta
> je bilo u pitanju).
> 
> Kod ostaje za potencijalne upotrebe kasnije. Ako se ne bude koristio biti ce 
> izbrisan.
> 


---
#### Ajla:

> **Implementacija Rola (Dekan & Sekretar)**
> Implementirana je logika koja na osnovu ulogovanog korisnika (emaila) renderuje drugačiji UI:
> * **Sekretar Dashboard:** Implementiran ekran koji povlači *sve* zahtjeve iz kolekcije `requests` koji su u statusu `PENDING`. Dodana logika za "Provjeru" koja mijenja status zahtjeva u `PENDING DEAN`.
> * **Dekan Dashboard:** Ekran koji prikazuje samo zahtjeve sa statusom `PENDING DEAN`. Implementirane akcije `Approve` (finalno odobrenje, umanjuje dane odmora) i `Reject`.

> **Upravljanje statusima zahtjeva**
> Kreirana logika za tranziciju stanja zahtjeva kroz sistem:
> `CREATED` -> `PENDING` (kod Sekretara) -> `PENDING DEAN` (kod Dekana) -> `APPROVED` / `REJECTED`.
> Svaka promjena statusa se atomično zapisuje u Firestore kako bi se osigurao integritet podataka.

> **UI/UX za liste zahtjeva**
> Dizajnirane kartice za prikaz zahtjeva (Request Cards) koje se  prilagođavaju ovisno o tome ko ih gleda (npr. Dekan vidi dugmad "Odobri/Odbij", dok Zaposlenik vidi samo status "Na čekanju").

> **Validacija i Poslovna logika**
> Dodane provjere prilikom kreiranja zahtjeva (npr. korisnik ne može tražiti više dana nego što ima na raspolaganju). Implementirano oduzimanje dana od ukupnog fonda sati nakon što Dekan odobri zahtjev.

> **Firebase Storage & Upravljanje Dokumentima**
> Implementirana kompletna integracija sa **Firebase Storage** servisom za upload dokaza (npr. doznake za bolovanje):
> * Prilikom slanja zahtjeva, fajl se automatski uploaduje na Storage.
> * Sistem generiše **javni URL** (download link) koji se povezuje sa zahtjevom u bazi.
> * Omogućen pristup ovim dokumentima isključivo **Dekanu i Sekretaru**, koji direktno iz aplikacije mogu otvoriti link i pregledati priloženi dokaz prije odobravanja.

> **PDF Export Funkcionalnost**
> Implementirana administrativna funkcionalnost za **Dekana**:
> * Mogućnost generisanja službenog **PDF dokumenta** (Lista zaposlenih) na osnovu odobrenih podataka.
> * PDF fajl se generiše lokalno i spreman je za dijeljenje ili arhiviranje.

> **Background Processing & Notifications (WorkManager)**
> Implementiran sistem za automatsko obavještavanje korisnika putem **Android WorkManager-a**:
> * Kreiran `LeaveReminderWorker` (nasljeđuje `CoroutineWorker`) koji radi u pozadini neovisno o životnom ciklusu aplikacije.
> * Koristi se `PeriodicWorkRequest` sa pametnim proračunom `initialDelay`-a kako bi se notifikacija zakazala tačno 24 sata prije isteka odsustva.
> * Implementirani **Notification Channels** za podršku na Android O+ verzijama, sa visokim prioritetom prikaza kako bi korisnik sigurno vidio podsjetnik za povratak na posao.
---

### Opis arhitekture aplikacije

1. Aplikacija je razvijena koristeći **Kotlin** programski jezik i **Jetpack Compose** toolkit za izgradnju korisničkog sučelja. Arhitektura prati moderne preporuke Google-a za razvoj Android aplikacija, implementirajući **MVVM (Model-View-ViewModel).** Cilj ove arhitekture je osigurati skalabilnost, lakše testiranje i jasno razdvajanje odgovornosti (Separation of Concerns).

2. Aplikacija je strukturirana u tri glavna sloja koja komuniciraju putem **jednosmjernog toka podataka (Unidirectional Data Flow)**:

    a. **UI Sloj (View)**
    * **Tehnologija:** Jetpack Compose.
    * **Odgovornost:** Prikaz podataka korisniku i hvatanje korisničkih akcija (klikovi, unosi). 
    * **Implementacija:** Ekrani su definirani kao Composable funkcije (npr. `LoginScreen`, `CreateRequestScreen`). 
    * UI je **reaktivan**: sluša promjene stanja (`StateFlow`) iz ViewModela i automatski se osvježava (recomposition) kada se podaci promijene. 
    * Koristi se **Single Activity** pristup (`MainActivity` je ulazna tačka).
    
    b. **ViewModel**
    * **Tehnologija:** Android ViewModel, Kotlin Coroutines, StateFlow.
    * **Odgovornost:** * Čuva stanje UI-a (UI State) koje preživljava promjene konfiguracije (rotacija ekrana). 
        * Sadrži poslovnu logiku (npr. validacija datuma u `InboxRequestViewModel`). 
        * Komunicira sa repozitorijumom radi dohvatanja podataka ili njihovog slanja. 
    * **Ključno:** Korištenje **Shared ViewModel** pristupa. `InboxRequestViewModel` se inicijalizira u navigacijskom grafu i dijeli između `ClientHomeScreen` i `CreateRequestScreen`, što omogućuje dijeljenje podataka.

3. Ključne komponente i Tehnologije 
    * **Navigacija:** Koristi se **Jetpack Navigation Compose**. Umjesto hard-kodiranih stringova, rute su definisane kao serijalizabilni objekti/klase (npr. `@Serializable object Login : Screen`). 
    * **Upravljanje Stanjem:** Koristi se `StateFlow` za reaktivno upravljanje stanjem. 
    * **Mreža i API:** * **Retrofit & OkHttp:** Korišteni su za komunikaciju s REST API-jima (Google API). 
        * **Firebase SDK:** Korišteni za autentifikaciju i bazu podataka u stvarnom vremenu. 
        * **Coroutines:** Svi mrežni pozivi se izvode asinhrono na IO threadovima. 

4. Struktura Projekta 

```text 
com.example.projekatfaza23
├── UI/                       # Ekrani i ViewModeli 
│   ├── login/                # LoginScreen.kt, LoginViewModel.kt i LoginUIState.kt
│   ├── home/                 # ClientHomeScreen.kt, ClilentHomeUIState.kt i util.kt
│   ├── request/              # CreateRequestScreen.kt, InboxRequestViewModel.kt
|   ├── dean/                 # Svi ekrani za dekana...
|   ├── secretary/            # Svi ekrani za sekretara... 
|   ├── profile/              # ProfileMenu.kt
│   └── navigation/           # NavGraph.kt, Screen.kt  
├── data/                     # Sloj podataka 
│   ├── api/                  # AuthInterceptor.kt, GoogleApiInterface.kt, ...
│   ├── auth/                 # GoogleAuth.kt, UserManager.kt, UserProfile.kt 
|   ├── db/                   # LeaveDao...
│   ├── repository/           # LeaveRepository, GoogleProfileRepository 
│   └── model/                # FakeLeaveRepository.kt, LeaveRepository.kt
├──  worker/                  # LeaveReminderWorker.kt 
├── ui/theme/                 ...
└── MainActivity.kt

```
---

## Upute za pokretanje

Aplikacija je razvijena u **Android Studio** okruženju koristeći **Kotlin** i **Jetpack Compose**. Za pokretanje je potrebna aktivna internet konekcija (zbog Firebase servisa).

### 📋 Preduslovi

Aplikacija je razvijena u najnovijem okruženju. Za uspješno kompajliranje i pokretanje, potrebne su sljedeće specifikacije:

* **Android Studio:** Narwhal | 2025.1.1 (ili novija).
* **Jezik:** Kotlin 2.0.21.
* **Java Verzija:** Java 11 (Projekt je konfigurisan sa `jvmTarget = "11"`).
* **Min SDK:** API Level 24 (Android 7.0 Nougat).
* **Target SDK:** API Level 36.

### ⚙️ Instalacija
1.  **Klonirajte repozitorij:**
    ```bash
    git clone https://github.com/ajla03/RMAS2025_AjlaiAlejna-.git
    ```
2.  **Otvorite projekt:** Pokrenite Android Studio i odaberite `Open Project`, te navigirajte do kloniranog foldera.
3.  **Firebase Konfiguracija:**
    * Osigurajte da se fajl `google-services.json` nalazi u `app/` direktoriju projekta.
    * *Napomena: Ako testirate na emulatoru, osigurajte da emulator ima instalirane Google Play Servise.*
4.  **Build:** Pustite da Gradle završi sinhronizaciju (Sync) i preuzimanje zavisnosti.
5.  **Run:** Pokrenite aplikaciju.

---
