# SearchEngineForStudent
## 开发环境: Window10(64bit)+IntelliJ IDEA Ultimate 2020.3

【注意事项】：自动测试目录Experiment1Test位于本机的路径上不能有中文，否则测试有错误

## 使用方法：
1. 自动测试
Experiment1Test是自动测试目录，
程序编译后的.class文件已经复制到Experiment1Test\betest目录下，
点击运行Experiment1Test\test.bat；
2. IDEA运行
使用IntelliJ IDEA Ultimate 2020.3打开工程目录SeachEngineForStudent，
可以运行SeachEngineForStudent\src\hust.cs.javacourse.search.run包下的TestBuildIndex.java和TestSearchIndex.java，
TestBuildIndex.java程序：测试序列化和反序列化功能；
TestSearchIndex.java程序：测试搜索功能；
3. 控制台运行
在SearchEngineForStudent\bin\artifacts\SearchEngineForStudent_jar目录下打开控制台；
输入命令：java -jar SearchEngineForStudent.jar，
将会运行TestSearchIndex.java程序，执行搜索功能。