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

> 4. dodana Composable funkcija za date pickig i attaching files
> u `NewRequestScreen`

> 5. dodan Repository `LeaveRepository`
>   - prepravljen `InboxRequestViewModel`
>   - dodana veza na Firebase Firestore

> 6. dodana funkcionalnost dodavanja fileova u request
>   - potrebna prepravka u sljedecoj fazi (ovo je minimalna implementacija da button radi sta je predvidjeno)

> 7. izmjena login screena i new request screena

> 8. dodana ogranicenja na tip i datume kod zahtjeva
>   - dodana animacija za error

> 9. bugfixes

> 10. dodana `enum class RequestType(val displayName : String, val maxDays : Int)` za tipove mogucih zahtjeva za odustvo, placenih i neplacenih. `maxDays` se odnosi na zakonski predviden broj dana za placeno odsustvo. 

---

### Alejna Hodzic:
> 1. organizacija projekta:
>   - paketa
>   - fajlova - razdvajanje UI Statea od ViewModela
>   - odredjenih funkcija

> 2. navigacija projekta
>   - dodan sealed interface Screen
>   - implementirana AppNavigation funkcija (wrappan NavHost)
>   - onemogucen povratak na login screen nakon prijave
>   - izmjena potpisa funkcija za prikazivanje ekrana i prosljiedjena lambda za navigaciju

> 3. projekt dodan na Google CLoud \ Firebase
>   - dodan config file u projekt za google servis

> 4. implementacija Google autentikacije

> 5. dodan UserManager i UserProfile

> 6. ispravljena potpuna funkcionalnost LoginScreen-a
>   - ViewModelu dodana funkcija za `GoogleSingIn` 
>   - prepravljene Compose funkcije

> 7. dodan logo i izmjenjen homescreen izgled

> 8. bugfixes

> 9. prepravljena data klasa `LeaveRepository` i fileovi koji su ju koristili

> 10. pripremila Retfofit, okHttpClient, interface i
> repozitorij za poziv ka apiju - turns out nije nam 
> ni trebalo :/

> 11. dodana offline database support koristeci 
> firebase 
