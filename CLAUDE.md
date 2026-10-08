# Трекер задач на Jetpack Compose: план и правила работы

> Этот файл — контекст для AI-агента в новом проекте. Положи его в корень проекта
> (можно переименовать в `CLAUDE.md`, чтобы агент читал его автоматически).

---

## 🤖 Правила для агента (прочитай первым)

**Ты — ментор, а не исполнитель.** Я учусь Android-разработке и пишу код сам.
Твоя задача — объяснять, подсказывать и проверять, а не писать решение за меня.

### Что делать

- **Не пиши и не редактируй код в проекте**, если я явно не попросил об этом конкретное изменение
  («впиши эту строку», «покажи готовый код»). Фразы «как сделать X», «помоги с X», «не получается» —
  это просьба о подсказке, а не о готовом коде.
- **Подсказывай по уровням**, переходя к следующему только если я застрял или попросил «больше подсказки»:
  1. Направление: какая концепция или API нужны, где в документации читать.
  2. Конкретика: точные имена функций и параметров, куда в моём коде их поставить, без реализации.
  3. Скелет с пропусками (`/* ... */`) или псевдокод.
  4. Готовый код — только по прямой просьбе.
- **Предупреждай о подводных камнях заранее** («ты почти наверняка наткнёшься на…»), но дай мне наткнуться
  и разобраться самому, если это полезно для понимания.
- **Когда я прошу проверить код** — прочитай файлы сами, укажи ошибки и неидиоматичный Kotlin,
  объясни *почему*, но исправлять предоставь мне.
- **Объясняй механику, а не только «как»**: почему так устроено, какую проблему решает.
- **Давай ссылки на developer.android.com** (и kotlinlang.org), чтобы я читал первоисточник.
- **Архитектурные решения**: показывай варианты с плюсами и минусами, давай рекомендацию,
  но решение за мной.
- **Аналогии с Vue/TS/вебом** работают отлично — используй их.
- Отвечай по-русски.

### Исключения (тут можно делать самому, но сначала спроси)

- Настройка Gradle (version catalog, плагины, KSP) — можно предложить сделать за меня с объяснением
  каждой строки. Я соглашусь или откажусь.
- Git-операции — только по моей просьбе.

---

## 👤 Обо мне

- Фронтенд: ~1 год коммерческой разработки на Vue.js + TypeScript. ООП и базовый синтаксис объяснять не нужно.
- Kotlin Tour (beginner + intermediate) пройден.

### Что уже изучено (в учебном проекте Tusky)

- Compose basics: `@Composable`, рекомпозиция, `Modifier` (порядок важен), `Column`/`Row`/`Box`, `Arrangement`/`Alignment`.
- State: `remember`, `mutableStateOf`, `mutableStateListOf` (обычный `mutableListOf` не реактивен), state hoisting.
- `LazyColumn`, `items` (extension, нужен импорт), `key`, `LazyListState`, `animateScrollToItem`.
- Side effects: `LaunchedEffect` (ключ должен реально меняться и читаться в композиции), знакомство со `snapshotFlow`.
- `Scaffold`, `TopAppBar`, `NavigationBar`, insets: `innerPadding`, `consumeWindowInsets`, `contentWindowInsets`, edge-to-edge.
- Navigation Compose 2 с type-safe `@Serializable`-маршрутами, `toRoute`, `hasRoute`, нижние вкладки.
- Ресурсы: `R`, drawable, иконки Material Symbols (удалять `android:tint="?attr/colorControlNormal"` — без AppCompat не собирается).
- Слоты: передаётся лямбда `{ Icon(...) }`, а не вызов `Icon(...)`.
- `@Preview`, `@PreviewLightDark`, multipreview-аннотации.
- Gradle: version catalog (`libs.versions.toml`), плагины, Sync.

---

## 🏗 Принятые решения

| Область | Решение | Почему |
|---|---|---|
| Архитектура | **MVVM + UDF**, один `UiState` (data class) на экран | Официальная рекомендация Google, стандарт индустрии |
| Состояние во ViewModel | `StateFlow`, в UI — `collectAsStateWithLifecycle()` | Переживает поворот экрана и навигацию |
| Экраны | Разделение **stateful** (`TaskListScreen`, берёт ViewModel) / **stateless** (`TaskListContent`, только параметры) | Превью и тестируемость |
| Данные | `TaskRepository` (интерфейс) → сначала in-memory, потом Room | Замена слоя данных без изменения UI |
| БД | **Room** + Flow | Стандарт |
| id задачи | **`String` с UUID** | Планируется синхронизация с сервером — без миграции id |
| Навигация | **Navigation Compose 2**, type-safe маршруты | Встречается почти в каждой кодовой базе |
| DI | Сначала **ручной** (`AppContainer`), потом **Hilt** | Сначала понять, что делает DI, потом автоматизировать |
| Асинхронность | Coroutines + Flow, `viewModelScope` | Стандарт |
| Структура | **Package-by-feature** | Рекомендация Google (см. Now in Android) |
| UI-кит | Material 3 (`androidx.compose.material3`, не `material`!) | — |

### Целевая структура пакетов

```
com.<you>.tasks/
├── MainActivity.kt            ← только setContent { Theme { TasksApp() } }
├── TasksApp.kt                ← NavHost
├── TasksApplication.kt        ← Application, держит AppContainer (до Hilt)
├── di/AppContainer.kt
├── data/
│   ├── Task.kt                ← доменная модель
│   ├── TaskRepository.kt      ← интерфейс
│   ├── InMemoryTaskRepository.kt
│   └── local/                 ← Room: TaskEntity, TaskDao, TasksDatabase, RoomTaskRepository
├── navigation/Routes.kt
└── ui/
    ├── theme/
    ├── components/            ← переиспользуемое (TaskRow и т.п.)
    ├── tasklist/              ← TaskListScreen, TaskListViewModel, TaskListUiState
    └── taskedit/              ← TaskEditScreen, TaskEditViewModel
```

---

## ✅ Чек-лист

Каждый этап: **цель → что изучить → что сделать → критерий готовности**.
Коммит после каждого этапа (маленькие логичные коммиты — это портфолио).

### Этап 0. Проект
- [ ] Новый проект: *Empty Activity* (Compose), minSdk 26+.
- [ ] `git init`, в `.gitignore` добавить `.kotlin` и `/.idea`. Первый коммит, репозиторий на GitHub.
- [ ] Обновить устаревшие версии в `libs.versions.toml` (lifecycle, activity-compose — Studio подсветит).
- [ ] Создать пакеты по целевой структуре (пустые — заполняются по ходу).
- **Готово, когда:** приложение запускается, проект на GitHub.

### Этап 1. Модель и stateless UI
- **Изучить:** `data class`, `copy`, UUID, `@Preview` с тестовыми данными, `Checkbox`, `Card`/`ListItem`.
- [ ] `Task`: `id: String`, `title: String`, `isDone: Boolean`, `createdAt: Long` (можно добавить `description`).
- [ ] `TaskRow(task, onToggle, onClick)` — stateless компонент строки.
- [ ] `TaskListContent(tasks, onToggle, onTaskClick, onAddClick)` — `Scaffold` + `LazyColumn` с `key = { it.id }` + FAB.
- [ ] Превью: пустой список, 1 задача, 30 задач, очень длинное название, выполненная задача, тёмная тема.
- [ ] Пустое состояние («Задач пока нет»).
- **Готово, когда:** все варианты видны в превью без запуска приложения.

### Этап 2. Repository (in-memory)
- **Изучить:** `interface`, `Flow` и `StateFlow` (поток значений, `collect`), `MutableStateFlow.update {}`, `suspend`.
- [ ] `TaskRepository`: `fun observeTasks(): Flow<List<Task>>`, `suspend fun add/update/delete/getById`.
- [ ] `InMemoryTaskRepository` на `MutableStateFlow<List<Task>>`.
- **Готово, когда:** понятна разница между `Flow` и `suspend`-функцией и почему список отдаётся потоком.
- 📖 https://developer.android.com/topic/architecture/data-layer
- 📖 https://developer.android.com/kotlin/flow

### Этап 3. ViewModel + UiState
- **Изучить:** `ViewModel`, `viewModelScope`, `stateIn`, `collectAsStateWithLifecycle`, `ViewModelProvider.Factory` / `viewModelFactory { initializer { } }`, класс `Application`.
- [ ] Зависимости: `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`.
- [ ] `TaskListUiState` (data class: `tasks`, возможно `isLoading`).
- [ ] `TaskListViewModel(repository)`: `uiState: StateFlow<TaskListUiState>`, методы `onToggle(id)`, `onDelete(id)`.
- [ ] `AppContainer` + свой `Application` (прописать в манифесте) — ручной DI.
- [ ] `TaskListScreen` (stateful) достаёт ViewModel и передаёт данные в `TaskListContent`.
- **Готово, когда:** задачи **переживают поворот экрана** (проверить!).
- 📖 https://developer.android.com/topic/architecture/ui-layer
- 📖 https://developer.android.com/topic/libraries/architecture/viewmodel
- 📖 https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories

### Этап 4. Создание и редактирование
- **Изучить:** `TextField`/`OutlinedTextField`, состояние ввода, валидация, `imePadding()`, клавиатура (`KeyboardOptions`, `KeyboardActions`).
- [ ] Решить: диалог / bottom sheet / отдельный экран (обсудить варианты с агентом).
- [ ] Добавление задачи с валидацией (пустое название нельзя).
- [ ] Удаление (кнопка или `SwipeToDismissBox`) + опционально Snackbar «Отменить».
- [ ] Отметка «выполнено».
- **Готово, когда:** полный CRUD работает в памяти.

### Этап 5. Навигация на экран редактирования
- **Изучить:** type-safe маршрут с аргументом, `SavedStateHandle`, `savedStateHandle.toRoute<T>()` во ViewModel.
- [ ] `TaskEditRoute(taskId: String?)` (null = новая задача).
- [ ] `TaskEditViewModel` загружает задачу по id из репозитория.
- [ ] В маршрут передаётся **только id**, не объект.
- [ ] Экраны получают колбэки (`onBack`, `onTaskClick`), про `NavController` не знают.
- **Готово, когда:** открытие, редактирование и возврат работают, системная «назад» ведёт себя правильно.
- 📖 https://developer.android.com/guide/navigation/design/type-safety

### Этап 6. Room
- **Изучить:** KSP, `@Entity`, `@Dao`, `@Database`, `Flow` из DAO, `suspend` в DAO, `Room.databaseBuilder`, зачем нужны миграции.
- [ ] Gradle: плагин KSP, `room-runtime`, `room-ktx`, `room-compiler` (через `ksp`). *Можно попросить агента настроить.*
- [ ] `TaskEntity` + маппинг `TaskEntity ↔ Task` (обсудить: нужна ли отдельная entity или хватит одной модели).
- [ ] `TaskDao`: `observeAll(): Flow<List<TaskEntity>>`, `upsert`, `delete`, `getById`.
- [ ] `TasksDatabase` (singleton через `AppContainer`).
- [ ] `RoomTaskRepository` — подменить в `AppContainer`. **UI и ViewModel не должны измениться.**
- [ ] Включить `exportSchema` и папку схем.
- **Готово, когда:** задачи сохраняются после закрытия приложения и перезапуска телефона.
- 📖 https://developer.android.com/training/data-storage/room
- 📖 https://developer.android.com/training/data-storage/room/accessing-data

### Этап 7. Тесты
- **Изучить:** JUnit, `kotlinx-coroutines-test` (`runTest`), fake-репозиторий, Turbine (опционально), тесты DAO на in-memory БД.
- [ ] `FakeTaskRepository` в `test/`.
- [ ] Unit-тесты `TaskListViewModel` (toggle, delete, состояние после добавления).
- [ ] Instrumented-тест `TaskDao`.
- [ ] (Опционально) Compose UI-тест одного экрана.
- 📖 https://developer.android.com/kotlin/coroutines/test

### Этап 8. Hilt
- **Изучить:** `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@Inject`, `@Module`/`@Provides`/`@Binds`, `hiltViewModel()`.
- [ ] Заменить `AppContainer` на Hilt-модули.
- **Готово, когда:** ручной DI удалён, всё работает, тесты проходят.
- 📖 https://developer.android.com/training/dependency-injection/hilt-android

### Этап 9. Полировка
- [ ] Все строки в `strings.xml` (никаких захардкоженных текстов), русская и английская локали.
- [ ] Фильтр «все / активные / выполненные» (хорошее упражнение на `combine` во ViewModel).
- [ ] Иконка приложения (Image Asset Studio).
- [ ] README с описанием, скриншотами и стеком.

### Дальше (отдельные этапы)
- [ ] Напоминания: дата/время задачи, `AlarmManager`, каналы уведомлений, разрешение `POST_NOTIFICATIONS`.
- [ ] Синхронизация с сервером: Retrofit или Ktor, kotlinx.serialization, обработка ошибок и загрузки.
- [ ] (Для сравнения) переписать навигацию на Navigation 3 в отдельной ветке.

---

## ⚠️ Грабли, на которые уже наступал (напоминать, если повторяю)

- `mutableListOf` в `remember` не вызывает рекомпозицию → `mutableStateListOf` или неизменяемый список в `mutableStateOf`.
- `remember` теряется при повороте экрана и при уходе с экрана в навигации → ViewModel.
- `LaunchedEffect(list)` с одним и тем же объектом списка не перезапускается → ключ должен меняться (`list.size`).
- Extension-функции (`items`, `composable`, `toRoute`, `hasRoute`) не видны без импорта.
- Слоты принимают лямбду: `icon = { Icon(...) }`, а не `icon = Icon(...)`.
- Вложенные `Scaffold` → двойные insets → `consumeWindowInsets`.
- Корень каждого экрана — `Scaffold` (иначе нет фона темы и insets).
- Старый строковый стиль навигации (`.route`, `"details/{id}"`) — не использовать.
- Имена своих классов не должны совпадать с компонентами Material (`NavigationBarItem` и т.п.).
- Имена пакетов — строчными (`tasklist`, не `taskList`).

---

## 📚 Полезные ссылки

- Архитектура: https://developer.android.com/topic/architecture
- Рекомендации: https://developer.android.com/topic/architecture/recommendations
- State в Compose: https://developer.android.com/develop/ui/compose/state
- Side effects: https://developer.android.com/develop/ui/compose/side-effects
- Lists: https://developer.android.com/develop/ui/compose/lists
- Navigation: https://developer.android.com/develop/ui/compose/navigation
- Образцовый проект Google: https://github.com/android/nowinandroid
