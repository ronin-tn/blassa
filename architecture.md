Core principle

Single-direction data flow. UI is a pure function of state. All business decisions live outside composables.

1. Mandatory architecture

Clean Architecture + MVVM

No deviations.

app (UI)
 └── presentation
       ├── screens
       ├── components
       ├── navigation
       └── ViewModels
domain
 ├── models
 ├── usecases
 └── repositories (interfaces)
data
 ├── remote (API)
 ├── local (optional)
 ├── dto
 ├── mappers
 └── repositories (impl)

2. UI layer (Jetpack Compose)
Rules

Composables are stateless

No network, no repositories, no use cases

No coroutine scopes inside composables (except LaunchedEffect)

Structure
Screen()
 ├── ScreenContent(state, events)
 └── ReusableComponents()

State model
sealed interface UiState {
    object Loading
    data class Success(...)
    data class Error(message: String)
}


UI renders strictly based on UiState.

3. ViewModel layer
Responsibilities

Holds screen state

Calls use cases

Maps domain results → UI state

Rules

Uses StateFlow

Exposes immutable state only

One ViewModel per screen

class ProfileViewModel(
    private val getProfile: GetProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(Loading)
    val state: StateFlow<UiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = Loading
            _state.value = getProfile()
        }
    }
}


No logic leaks upward or downward.

4. Domain layer
Purpose

Pure business logic. Zero Android imports.

Components

Models (business entities)

UseCases (one action per class)

Repository interfaces

class GetProfileUseCase(
    private val repo: UserRepository
) {
    suspend operator fun invoke(): Profile
}


Use cases do not know about Retrofit, Room, or Compose.

5. Data layer
Networking

Retrofit

DTOs only

Explicit mapping to domain models

ApiDTO → Mapper → DomainModel


No DTO reaches UI.

Repositories

Implement domain interfaces.

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository

6. State management
Rules

One state object per screen

No multiple flows per screen

No mutable state in composables

Preferred tools:

StateFlow

rememberSaveable (UI-only state)

Avoid:

LiveData

Global mutable state

Two-way binding

7. Navigation
Tool

Navigation Compose

Rules

Routes are constants

No ViewModel creation inside composables

Pass IDs, not objects

"profile/{userId}"


ViewModel loads data using ID.

8. Dependency Injection
Tool

Hilt

Rules

ViewModel injected

UseCases injected

Repositories bound in data module

No manual factories.

9. Error handling
Strategy

Domain returns failures explicitly

ViewModel converts failures to UI state

UI displays error, never interprets it

No try/catch in composables.

10. Concurrency model

Coroutines everywhere

Dispatchers.IO only in data layer

Structured concurrency enforced

No async nesting. No GlobalScope.

11. Testing boundaries
What to test

UseCases (pure unit tests)

ViewModels (state transitions)

Minimal Compose UI tests

Do not snapshot-test business logic.

12. Common anti-patterns (avoid)

Logic in composables

Shared ViewModel across unrelated screens

DTO reuse in UI

Exposing mutable state

Remembering state in ViewModel and Composable simultaneously

13. Build order

Domain models + use cases

Repository interfaces

Data implementations

ViewModels

Compose UI

This order is enforced to prevent coupling.

This setup is stable, scalable, and aligned with Compose’s declarative model.