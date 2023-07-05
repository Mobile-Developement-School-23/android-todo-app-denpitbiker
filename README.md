# YANDEX ToDoApp

### Made by [@vdenk](https://t.me/vdenk)

## Project description

- Swiping both left & right
    - Swiping **left** to delete. Item will be removed from Recycler View. Trash icon will be drawn
      at the red background while swiping.
    - Swiping **right** to check. Item **isDone** param will be inverted & item will be recreated by
      the RecyclerView. Check box icon will be drawn at the green background while swiping.
- Both **Day** and **Night** Themes supported
- **Responsive UI**
    - Text becomes strikethrough when the task is completed and vice versa
    - Fragment navigation implemented && animated
    - Collapsing toolbar with swipe-to-refresh gesture
- Yandex.passport authentication
- Dagger DI
- Room Database
- Retrofit
- Coroutines with Flows

## Homework [1] ✅

1. Дизайн как на Figma.
2. Навигация не переживает поворот экрана:), поэтому поворот экрана запрещен в манифесте.
3. TodoItemsRepository генерирует данные случайным образом, для демонстрации работы интерфейса

## Homework [3] ✅

Сделано всё по ТЗ, но есть косяки по архитектуре:

1. Забыл ограничить оповещения о работе с сервером в некоторых ситуациях
2. Алгоритм синхронизации выглядит страшненько
3. Синглтоны...
4. Утечка памяти у SwipeCallback(Исправлено)
5. Как оказалось, при рефакторе кода я поломал поход в сеть...

## Homework [4] ✅