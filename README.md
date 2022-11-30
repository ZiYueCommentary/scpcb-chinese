# SCP - 收容失效 汉化计划

游戏基于 [SCP基金会](http://scp-wiki-cn.wikidot.com/) 背景

汉化版基于 [SCP - Containment Breach TSS](https://github.com/ZiYueCommentary/scpcb-tss) 制作

游戏遵循[知识共享许可协议BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/deed.zh)（即必须署名、可商用、可二创、可二创商用）

汉化版本由 [子悦汉化组](https://ziyuesinicization.site/) 制作

## 需要软件:

- [Blitz3D TSS (ZiYueCommentary)](https://github.com/ZiYueCommentary/Blitz3D)

此处Blitz3D TSS指[子悦制作的分支版本](https://github.com/ZiYueCommentary/Blitz3D)，而不是Third Subdivision Studios官方制作的[Blitz3D TSS](https://github.com/Saalvage/Blitz3D)。

**注意** - 源代码可能比游戏本身更加可怕！

## 如何构建
1. 安装[Blitz3D TSS (ZiYueCommentary)](https://github.com/ZiYueCommentary/Blitz3D)

    您可下载Blitz3D TSS的简体中文版本，这会让引擎的报错弹窗显示简体中文。
2. 将目录中所有`.decls`文件放入`Blitz3D TSS\userlibs\`中
3. 使用Blitz3D TSS打开`Main.bb`
4. 编译并运行！

## 注意

### 字体

以下字体需**付费商用**：

|字体名称|作用贴图|
|:--:|---|
|风铃悠悠|`docL1.jpg`、`docL2.jpg`、`docL3.jpg`、`docL4.jpg`、`docL5.jpg`、`docL6.jpg`、`leaflet.jpg`|
|字体管家德古拉|`leaflet.jpg`|

#### “收容失效”

游戏中使用的主要字体为[**收容失效Containment Breach**](https://github.com/ZiYueCommentary/font-containment-breach)，专为汉化计划制作。

字体*收容失效*由[思源宋体](https://source.typekit.com/source-han-serif/cn/)和[Courier New](https://docs.microsoft.com/en-us/typography/font-list/courier-new)合并制成，二者均为**开源字体**，支持**免费商用**。

### IDEal

**IDEal**是一个经典的开发Blitz3D程序的集成开发环境软件。

请不要使用IDEal来进行开发，因为汉化计划项目的编码是UTF-8，而IDEal的编码为ANSI。

### 无大地址概念

**无大地址概念Not Address Large Aware**是Blitz3D TSS的一个编译选项，允许程序最多使用4GB内存，而不是2GB。

该选项对于64位系统有些许帮助，也可解决[排错模式](#排错模式)卡顿严重的问题，推荐始终开启。

### 排错模式

**排错模式Debug Mode**是Blitz3D的一个编译选项，其可以捕获异常，并显示抛出异常的代码行。

请注意，排错模式会极大影响程序运行性能，您可开启[无大地址概念](#无大地址概念)选项来缓解此问题。尽管如此，我们仍建议您在编译可执行文件前关闭排错模式。

<br>

<p align="center"><a href="https://www.scpcbgame.com/">收容失效英文官网</a> | <a href="https://www.scpcbgame.cn/">收容失效中文网站</a> | <a href="https://scpcbgame.cn/help.html">常见问题解决方案</a></p>