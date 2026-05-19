# Homework 5: Fractal Flame Generator

Проект генерирует изображение фрактального пламени по идее Chaos Game.

## Что реализовано

- Однопоточный и многопоточный режим (`--threads`).
- Цветной алгоритм (RGB + накопление плотности + log-density tone mapping).
- 6 трансформаций из статьи: `linear`, `sinusoidal`, `spherical`, `swirl`, `horseshoe`, `polar`.
- Симметрия (`-s`, `--symmetry-level`, N >= 1).
- Ввод параметров: CLI, JSON, default.
- Приоритет параметров: `CLI > JSON > default`.
- Валидация параметров с ошибками в консоль.
- Логи прогресса и ошибок.
- Результат: PNG, RGB, 8 бит на канал.
- Unit + integration тесты, Jacoco coverage check (>= 80%).

## Структура

- Main: `src/main/java/hw5/Main.java`
- Конфиг: `src/main/java/hw5/config`
- CLI парсер: `src/main/java/hw5/cli`
- Трансформации: `src/main/java/hw5/transform`
- Рендер: `src/main/java/hw5/render`
- Сохранение PNG: `src/main/java/hw5/io`
- Бенчмарк: `src/main/java/hw5/benchmark/BenchmarkRunner.java`

## Подготовка окружения (PowerShell)

Если Maven запускается из IntelliJ:

```powershell
$env:JAVA_HOME="$env:USERPROFILE\.jdks\openjdk-25.0.2"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
```

Ожидается версия Java 17+.

## Сборка

```powershell
& "D:\Programms\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd" -f "C:\Users\mrrom\IdeaProjects\homework1\homework5\pom.xml" clean package
```

После успешной сборки создается:

- `target\homework5-1.0.0.jar`

## Запуск

### 1. Запуск с дефолтами

```powershell
java -jar .\target\homework5-1.0.0.jar
```

### 2. Запуск с JSON-конфигом

```powershell
java -jar .\target\homework5-1.0.0.jar --config .\config.example.json
```

### 3. Запуск с CLI-параметрами

```powershell
java -jar .\target\homework5-1.0.0.jar -w 1920 -h 1080 -i 500000 -t 4 -s 3 -f swirl:1.0,horseshoe:0.8,spherical:0.7 -ap 0.45,-0.25,0.2,0.2,0.45,-0.1/0.5,0.2,-0.3,-0.2,0.5,0.3 -o result.png
```

## Тесты и покрытие

```powershell
& "D:\Programms\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd" -f "C:\Users\mrrom\IdeaProjects\homework1\homework5\pom.xml" test
```

Отчёт Jacoco:

- `target\site\jacoco\index.html`

## Бенчмарк (1, 2, 4, 8 потоков)

```powershell
java -cp .\target\homework5-1.0.0.jar hw5.benchmark.BenchmarkRunner
```