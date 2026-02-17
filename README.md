# Repozitorij za projekt iz predmeta Razvoj mobilnih aplikacija i servisa

---

## HR Aplikacija

Studenti:  
`Ajla Bulic`  
`Alejna Hodzic`

---

### Opis aplikacije

Aplikacija je namjenjena za upravljanje zahtjevima zaposlenika za godisnji odmor ili placeno odsustvo. 

Zaposlenik pored podnosenja zahtjeva moze vidjeti i koliko dana raspolozivih za odsustvo mu je ostalo.

HR sluzbenik moze obradjivati zahtjeve i voditi evidenciju, a dekan daje konacno odobrenje za zahtjeve!

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


> **Implementacija lokalne Room baze podataka**  
> Dodani entiteti i izmjenjena prethodna baza podataka zbog perzistencije podataka na uredjaju.
> Definirani su entiteti koji odgovaraju strukturi potrebnoj za rad aplikacije bez veze sa internetom.

> **Ponovna implementacija repozitorija**   
> Spremanje (pisanje) u lokalnu i bazu podataka na firestore. Ostvareni pristup je offline-first.
> Repozitorij je veza izmedju lokalne baze i Firestore-a. 
> Podaci se najprije upisuju u lokalnu bazu, te se asinhrono sinhronizuju sa serverom.
> Omoguceno je real time azuriranje podataka u aplikaciji (citanje izmjena na firestore) pomocu snapshot listenera.

> **Implementacija profile menija**   
> Omogucen je logout za korisnika (sigunro brisanje kredencijala iz user managera), te izmjena stanja status labele koja prikazuje dostupnost klijenta/profesora.
> Na meniju se vide i ostali dostupni podaci poput slike profila (ako je javna), ime, prezime te mail!

> **Prikay podataka procitanih iz baze**
> Dosta koda je bilo hardkodirano, nakon implementacije lokalne baze taj kod je izmjenjen sa dnamickim podacima iz baze koji se azuriraju u real time!

> **Prikaz preview kartice zahtjeva**
> Omogucen prikaz podataka o zahtjevu! Podaci se dobiju iz baze podataka te se prikazuju shodno statusu zahtjeva.

> **Implementacija funkcionalnosti validacije slanja zahtjeva**
> Vikendi se ne broje pri proracunu dana odsustva!  
> Nije moguce birati dane iz proslosti!
> Short notice najave godisnjeg odmora obavjestavaju korisnika da zahtjev mozda nece biti prihvacen.

> **Promjena uloge Dekan <-> Profesor, Sekretar <-> Zaposlenik (Dinamicka uloga)** 
> Obzirom da postoji poreba da i dekan i sekretar mogu poslati zahtjeve za odsustvo, omoguceno im je biranje uloge u kojoj zele da koriste aplikaciju, te prelaz izmedju uloga!
> Opcije se prikazuju klikom na profilnu sliku!



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
│   └── navigation/           # NavGraph.kt, Screen.kt  
├── data/                     # Sloj podataka 
│   ├── api/                  # AuthInterceptor.kt, GoogleApiInterface.kt, ...
│   ├── auth/                 # GoogleAuth.kt, UserManager.kt, UserProfile.kt 
│   ├── repository/           # LeaveRepository, GoogleProfileRepository 
│   └── model/                # FakeLeaveRepository.kt, LeaveRepository.kt
├── ui/theme/                 ...
└── MainActivity.kt
