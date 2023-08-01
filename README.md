# SkBriggy

SkBriggy is a [**Skript**](https://github.com/SkriptLang/Skript) addon that allows users to create commands using Minecraft's Brigadier command system.
This enables you to use command suggestions in the style that Minecraft does.

While this is not intended to be a drop in replacement for Skript commands, it does allow for some more intuitive command building.

## Visual Differences:
### Skript (with SkBee for tab completions):
Code:
```hs
command /skriptban <player> <number> <text> <text>:
	trigger:
		# do stuff

on tab complete of "/skriptban":
	set tab completions for position 2 to "<time>"
	set tab completions for position 3 to "minutes", "hours" and "days"
	set tab completions for position 4 to "<reason>"
```
Visual:
![](https://i.imgur.com/aTw6P3g.png)
![](https://i.imgur.com/dgbjI4t.png)
![](https://i.imgur.com/MB1cItQ.png)
![](https://i.imgur.com/ZzHyLT9.png)
![](https://i.imgur.com/hJRGLXc.png)

### SkBriggy
Code:
```hs
brig command /brigban <player> <time:int> <span:string> <reason:text>:
	arguments:
		set suggestions of "span" arg to "minutes", "hours" and "days"
	trigger:
		# do stuff
```
Visual:
![](https://i.imgur.com/XgmywMq.png)
![](https://i.imgur.com/56Ty53s.png)
![](https://i.imgur.com/UciQAGM.png)
![](https://i.imgur.com/SGkzV76.png)
![](https://i.imgur.com/GptXjKE.png)
