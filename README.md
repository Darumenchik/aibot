# Chiper - Secure Messenger

Современный мессенджер с красивым glassmorphism дизайном, полной приватностью и плавными анимациями.

## 🚀 Особенности

### 🎨 Дизайн
- **Glassmorphism UI** - полупрозрачные карточки с размытием фона
- **6 тем** - Океан (по умолчанию), Полуночная, Закат, Лес, Космос, Неон, Аврора
- **Анимированные переходы** между экранами
- **Material You** поддержка (динамические цвета)
- **RTL поддержка** для арабского/иврита
- **Высокий контраст** режим
- **Масштабирование шрифтов** (0.8x - 1.4x)

### 🔐 Безопасность
- **Код доступа** (4-6 цифр) с биометрией (Face ID / Touch ID)
- **Двухфакторная аутентификация** (TOTP - Google Authenticator)
- **Управление устройствами** - просмотр и отзыв сессий
- **Автоблокировка** с настраиваемым таймаутом
- **Локальное хранение** настроек (DataStore)

### 💬 Чат
- **Swipe-to-reply** - свайп вправо для ответа
- **Реакции на сообщения** (долгое нажатие → выбор эмодзи)
- **Голосовые сообщения** с визуализацией волны
- **Видео-сообщения** (круглые как в Telegram)
- **Markdown** форматирование (жирный, курсив, код, ссылки)
- **Ответы с превью** исходного сообщения
- **Закрепленные сообщения**
- **Исчезающие сообщения** (таймер автоуничтожения)

### 👥 Группы и каналы
- **Группы до 200 000 участников**
- **Каналы** для вещания неограниченной аудитории
- **Администраторы/модераторы** с правами
- **Пригласительные ссылки** с QR-кодами
- **Опросы** (анонимные, multiple choice, quiz)
- **Закрепленные сообщения** (несколько)
- **Slow mode** для чатов

### 📞 Звонки
- **Голосовые звонки** (WebRTC)
- **Видеозвонки** с Picture-in-Picture
- **Групповые звонки** (до 30+ человек)
- **Запись звонков** (с согласия)
- **Screen share**

### 🔔 Уведомления
- **FCM Push** уведомления
- **Кастомные звуки** на чат/группу
- **Умная группировка** уведомлений
- **Badge count** на иконке приложения
- **Quick reply** из уведомления
- **Do Not Disturb** расписание

### 🎭 Анимации
- **Splash screen** с пружинистой анимацией
- **Onboarding** с плавающими частицами
- **Staggered list** вход элементов
- **Pull-to-refresh** с кастомным спиннером
- **Reaction burst** - взрыв эмодзи при двойном тапе
- **Skeleton loaders** с shimmer эффектом

## 🛠 Технологии

- **Kotlin Multiplatform** (Android + iOS ready)
- **Compose Multiplatform** 1.11+
- **Voyager** навигация
- **Koin** DI
- **DataStore** настройки
- **Coroutines + Flow** реактивность
- **Kotlinx Serialization** JSON
- **Ktor** клиент (готово для API)

## 📦 Установка

```bash
# Клонирование
git clone https://github.com/yourusername/chiper.git
cd chiper

# Сборка debug APK
./gradlew :androidApp:assembleDebug

# Запуск на устройстве
./gradlew :androidApp:installDebug
```

## 🔧 Настройка Firebase (для продвинутой версии)

1. Создайте проект в [Firebase Console](https://console.firebase.google.com)
2. Добавьте Android приложение с package name `com.chiper.kz`
3. Скачайте `google-services.json` в `androidApp/`
4. Включите Authentication (Email/Password, Google)
5. Включите Firestore Database
6. Включите Cloud Messaging

## 📁 Структура проекта

```
chiper/
├── androidApp/           # Android приложение
├── shared/
│   ├── src/commonMain/
│   │   ├── kotlin/com/chiper/kz/
│   │   │   ├── components/      # Glass UI компоненты
│   │   │   ├── components/chat/ # Чат компоненты
│   │   │   ├── components/glass/ # Glass UI система
│   │   │   ├── data/            # Репозитории
│   │   │   ├── di/              # Koin модули
│   │   │   ├── model/           # Data классы
│   │   │   ├── navigation/      # Voyager экраны
│   │   │   ├── screens/         # Экраны приложения
│   │   │   │   ├── auth/        # Авторизация
│   │   │   │   ├── chat/        # Чат
│   │   │   │   ├── chatlist/    # Список чатов
│   │   │   │   ├── channels/    # Каналы
│   │   │   │   ├── groups/      # Группы
│   │   │   │   ├── media/       # Медиа пикер
│   │   │   │   ├── onboarding/  # Онбординг
│   │   │   │   ├── profile/     # Профиль
│   │   │   │   ├── security/    # Безопасность
│   │   │   │   ├── settings/    # Настройки
│   │   │   │   ├── calls/       # Звонки
│   │   │   │   └── notifications/ # Уведомления
│   │   │   ├── security/        # Security репозиторий
│   │   │   ├── theme/           # Темы и Glass UI
│   │   │   ├── ui/              # UI утилиты
│   │   │   └── utils/           # Утилиты
│   │   └── composeResources/    # Ресурсы Compose
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml       # Версии зависимостей
├── build.gradle.kts
└── settings.gradle.kts
```

## 🎯 Roadmap

- [ ] iOS приложение (Compose Multiplatform)
- [ ] Desktop версия (Windows/macOS/Linux)
- [ ] Web версия (WASM)
- [ ] E2E шифрование (Signal Protocol)
- [ ] Стикеры (Lottie)
- [ ] GIF поиск (Giphy/Tenor)
- [ ] Виджеты Android
- [ ] Wear OS поддержка
- [ ] Android Auto

## 📄 Лицензия

MIT License - см. [LICENSE](LICENSE)

---

**Chiper** - Быстрый. Безопасный. Твой. 🚀