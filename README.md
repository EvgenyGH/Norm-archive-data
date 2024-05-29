# Special Standard Fuel Consumption (SSFC) archive

Запуск:

1. Поднять базу данных (рекомендовано PostgreSQL);
2. Выложить сертификат в формате р12 (norm.p12) в папку ./keystore;
3. Установить переменные среды;
4. Скомпилировать и запустить.

Переменные середы:

- DB_URL - URL базы данных в формате jdbc:postgresql:URL:PORT/DATABASE_NAME;
- DB_PASSWORD - пароль пользователя базы данных;
- DB_USER - имя пользователя базы данных;
- KEYSTORE_ALIAS - псевдоним сертификата;
- KEYSTORE_PASSWORD - пароль для хранилища р12.