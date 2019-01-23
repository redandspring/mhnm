# mhnm
site parser  

Создать директорию  
c:/_PROJECT/temp/

Добавить файл  
c:/_PROJECT/temp/config.txt  
кодировка UTF-8
```
site.posts = ht__://w__.___%s  
site.posts.start.id = 800100

site.posts.timeout.fast.is = false

site.posts.timeout.fast.row = 2
site.posts.timeout.fast.batch.short = 5
site.posts.timeout.fast.batch.medium = 15
site.posts.timeout.fast.batch.big = 30

site.posts.timeout.slow.row = 2
site.posts.timeout.slow.batch.short = 20
site.posts.timeout.slow.batch.medium = 100
site.posts.timeout.slow.batch.big = 250 

site.posts.words.include=aaaaa;ccccc
site.posts.words.exclude=bbbbb;eeeee
```

bat файл запуска
```
cd c:\_PROJECT\temp\
java -jar mhnm-1.0.jar
pause
```
