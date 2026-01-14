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

(Ovdje dodati opis funkcionalnosti)

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
