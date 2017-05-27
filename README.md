# gifloadbyndk
study with ndk to load gif

1. please setting NDK for Android studio

2. you can use with this:
放一个gif文件在测试机器上，然后调用即可
GifLoader.with(this).load(path).into(imageView);

3.all 'c' code in src/main/cpp, something about giflib and gifHelp.c
利用C加载文件夹中的gif文件,效率很快，这里是个简单版本供给大家学习和探讨。其实很多大型的图片加载库都采用了giflib库加载gif。
主要的加载过程：
a.首先加载路径打开这个文件，得到文件的句柄
b.然后根据句柄得到gif图片的宽和高
c.创建bitmap
d.将gif一帧的数据读取出来 ，放入bitmap的数据地址中（二维数组的像素数据）
e.循环每一帧的数据即可

