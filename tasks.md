## Evidencija funkcionalnosti projekta

---

### Ajla Bulic:
> 1. kreirala inicijalne ekrane:
>   - `ClientHomeScreen`
>   - `LeaveRequestViewModel`
>   - `FakeLeaveRepository`

> 2. dodala `DropDownMenu` za filtriranje zahtjeva na HomeScreen
>    - funkcija za fltriranje u `LeaveRequestViewModel`

> 3. dodala `NewRequestScreen`
>   - dodana nova polja u data `class LeaveRequest`
>   - dodane nove funkcionalnosti u `LeaceRequestViewModel` za handling polja za opis, datume i tip zahtjeva
>   - dodano polje `currentRequest` u `LeaveUiState`

> 4. dodana Composable funkcija za date pickig i attacking files
> u `NewRequestScreen`

> 5. dodan Repository `LeaveRepository`
>   - prepravljen `InboxRequestViewModel`
>   - dodana veza na Firebase Firestore

> 6. dodana funkcionalnost dodavanja fileova u request
>   - potrebna prepravka u sljedecoj fazi (ovo je minimalna implementacija da button radi sta je predvidjeno)

---

### Alejna Hodzic:
> 1. organizacija projekta:
>   - paketa
>   - fajlova - razdvajanje UI Statea od ViewModela
>   - odredjenih funkcija

> 2. navigacija projekta
>   - dodan sealed interface Screen
>   - implementirana AppNavigation funkcija (wrappan NavHost)
>   - izmjena potpisa funkcija za prikazivanje ekrana i prosljiedjena lambda za navigaciju

> 3. projekt dodan na Google CLoud \ Firebase
>   - dodan config file u projekt za google servis

> 4. implementacija Google autentikacije

> 5. dodan UserManager i UserProfile

> 6. ispravljena potpuna funkcionalnost LoginScreen-a
>   - ViewModelu dodana funkcija za `GoogleSingIn` 
>   - prepravljene Compose funkcije
