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

#### Ajla:
> **Repository**
> 
>  Inicijalno je implementiran `FakeLeaveRepository` koji predstavlja fake tj. dummy podatke koji se koriste ako
> nema nista na Firebase serveru ili lokalno ( lokalna baza jos nije implementirana ). Ovi dummy podaci su super
>  sluzili da se prati tok dolaska podataka sa servera ( ako se poslao request, automatski se vise dummy podaci
> ne prikazuju nego se prikazuju podaci sa servera ).
> 
> Nakon toga, implementiran je `LeaveRepository` koji sluzi kao Single Source of Truth, kojeg view model koristi da
>  dobije podatke koji mu trebaju. LeaveRepository drzi instancu na Firebase Firestore koja mu sluzi za dohvatanje
> podataka sa Firestore-a. Posto se koristi Flow podataka, podaci se azuriraju In Real Time.
> 
>
> **Firebase Firestore**
>
> Za dohvatanje podataka u firebase kolekciji `leave_requests` koristi se i relevantan `userEmail` sa kojim je asociran
> trenutni ulogovani korisnik aplikacije. To omogucava da korisnik dobije samo svoje requeste sa Firestore-a. S druge
> strane, dekan koji bi ih odobravao bi imao pristup svim zahtjevima na Firestore-u.
> 
> Kao sto sam i navela, ako je ne moguce pristupiti podacima na serveru, ili ce se prikazati oni koji su sacuvani
> lokalno (nakon implementacije Room-a) ili aktivna offline Firestore podrska.
>
> **Dodavanje file-ova u request**
>
> Da bi se omogucila funkcionalnost dodavanja file-ova u Request dodata su nova polja u LeaveRequest data class,
>  a ticu se imena file-a, tipa file-a i uri-ja.
>
> Fajl se dohvata pomocu filePickerLaunchera pozivom funkcije launch koja otvara sistemski file picker za biranje
> fileova. Nema ogranicenja na tip file-a koji mozemo izabrati ( za sad ), a to se postiglo pozivom launch("*/*") gdje
> je "*/*" argument koji znaci "dozvoli bilo koju vrstu fajla".
>
> `getfileName` funkcija sluzi da na osnovu Uri objekta kojeg vrati sistemski file picker, pokusa dobiti metapodatke
> fajla. Ako je `uri.scheme == "content"` u tom slucaju se koristi ContentResolver da se procita ime fajla, a to se
> vrsi preko cursora.
>
> Ako se ime fajla ne moze dobiti preko ContentProvidera funkcija prelazi na rezervni nacin. U tom slucaju, uzima se
> putanja iz URI-ja.
>
> **Napomena**
>
>  Posto na Firebase saljemo lokalni uri a ne javni url implementacija je nepotpuna. Da bi korisnik na
> drugoj strani imao pristup fajlu, fajl ce se morati uploadati na Firebase Storage i onda na Firebase poslati javni
> url fajla. Implementacija je uradena radi kompletiranja funkcionalnosti slanja zahtjeva i dodavanja file-ova.
> 
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
