## Процедура запуска автотестов

### 1. Подготовка к запуску тестов
Для реализации автотестов необходимо установить программы IntelliJ Idea, Docker.
Проект открываем в IntelliJ Idea.

### 2. Запуск контейнеров c MySQL, с PostgreSQL,с эмулятором банковских сервисов
В файлах  `docker-compose.yml`,`Dockerfile` описаны настройки для сборки образа и запуска контейнеров.
Сборка и запуск контейнеров осуществляется командой в терминале: `docker-compose up --build`.

### 3. Запуск сервиса

Запуск сервиса с поддержкой БД осуществляется одним из следующих способов:

#### 3.1 Запуск сервиса с MySQL

В файле `application.properties` должна быть указана следующая настройка:

`spring.datasource.url=jdbc:mysql://185.119.57.172:3306/app`.

В файле `build.gradle`  указываем настройки:

`test {
systemProperty 'db.url', System.getProperty('db.url','jdbc:mysql://185.119.57.172:3306/app')
}`

В командной строке для запуска сервиса вводим в терминале команду:

`java -jar ./artifacts/aqa-shop.jar -Dspring.datasource.url=jdbc:mysql://185.119.57.172:3306/app`.

#### 3.2 Запуск сервиса с PostgreSQL

В файле `application.properties` должна быть указана следующая настройка:

`spring.datasource.url=jdbc:postgresql://185.119.57.172:5432/app`.

В файле `build.gradle`  указываем настройки:

`test {
systemProperty 'db.url', System.getProperty('db.url','jdbc:postgresql://185.119.57.172:5432/app')
}`

В командной строке для запуска сервиса вводим в терминале команду:

`java -jar ./artifacts/aqa-shop.jar -Dspring.datasource.url=jdbc:postgresql://185.119.57.172:5432/app`.


### 4 Запуск автотестов

Автотесты для сервиса с подключенной БД MySQL запускаются в терминале командной строкой:

`./gradlew clean test -D:db.url=jdbc:mysql://185.119.57.172:3306/app`.

Автотесты для сервиса с подключенной БД PostgreSQL запускаются в терминале командной строкой:

`./gradlew clean test -D:db.url=jdbc:postgresql://185.119.57.172:5432/app`.
