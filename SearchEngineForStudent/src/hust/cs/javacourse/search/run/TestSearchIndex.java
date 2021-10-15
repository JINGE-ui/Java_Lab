package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.DocumentBuilder;
import hust.cs.javacourse.search.index.impl.IndexBuilder;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StopWords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.System.exit;

/**
 * 测试搜索
 * 默认是不区分大小写的
 */
public class TestSearchIndex {
    /**
     * 搜索程序入口
     * @param args 命令行参数
     * @throws IOException 可能存在的输入输出异常
     */
    public static void main(String[] args) throws IOException {
        IndexSearcher searcher = new IndexSearcher();
        SimpleSorter freqSorter = new SimpleSorter();
        // 查询一个单词
        String req;
        System.out.println("选择数据集，将索引对象序列化到文件：");
        System.out.println("1. 功能测试数据集");
        System.out.println("2. 真实测试数据集");
        System.out.println("3. 输入exit退出");
        System.out.print("请输入你的选择: ");
        Scanner scan = new Scanner(System.in);
        String in = "";
        if(scan.hasNext()){
            in = scan.next();
            while(!in.equals("1") && !in.equals("2") && !in.equals("exit")){
                System.out.print("格式错误，请重新输入你的选择: ");
                System.out.println("1. 功能测试数据集");
                System.out.println("2. 真实测试数据集");
                System.out.println("3. 输入exit退出");
                in = scan.next();
            }
        }
        AbstractIndexBuilder indexBuilder = new IndexBuilder(new DocumentBuilder());
        String addPath = "";
        if(in.equals("1")){
            addPath = "function/";
        }else{
            addPath = "real/";
        }
        AbstractIndex index = indexBuilder.buildIndex(Config.DOC_DIR+addPath);    //已经保存到序列化文件
        if (index.getDictionary().isEmpty()){
            System.out.println("Warning: 索引表为空！");
            exit(1);
        }
        searcher.open(Config.INDEX_DIR + "index.dat");
        System.out.println("-----------------索引对象已写入序列化文件-------------------");

        System.out.println("从序列化文件中读取倒排索引，进行查询，输入格式：");
        System.out.println("1. 单个搜索关键词");
        System.out.println("2. 两个关键词的与查询 ( 格式：Word1 & Word2 )");
        System.out.println("3. 两个关键词的或查询 ( 格式：Word1 | Word2 )");
        System.out.println("4. 两个单词的短语检索 ( 格式：Word1 Word2 )");
        System.out.println("5. 输入exit退出查询");
        System.out.print("请输入需要查询的单词: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while((req = br.readLine()) != null && !req.equals("exit")){
            String[] reqs = req.split("[\\s]+");   // 用空白符切分输入的这行
            List<String> stopWords = new ArrayList<String>(Arrays.asList(StopWords.STOP_WORDS));
            for(String s : reqs){
                if(stopWords.contains(s))
                    System.out.println("\033[31mWarning: 停用词: " + s + "\033[0m");
            }
            if(reqs.length < 1){
                System.out.println("请至少输入一个单词");
            } else if(reqs.length == 1){
                String queryWord = req.toLowerCase();
                AbstractHit[] hits = searcher.search(new Term(queryWord), freqSorter);
                if(hits == null) System.out.println("未搜索到任何结果");
                else for(AbstractHit h: hits) {
                    System.out.println("--------------------------------------------------------------");
                    System.out.println(h.toString());
                }
                System.out.println("--------------------------------------------------------------");
                System.out.print("请输入需要查询的单词: ");

            } else if(reqs.length == 2){        // 查询两个在文中相邻的单词
                String queryWord1 = "",queryWord2 = "";
                queryWord1 = reqs[0].toLowerCase();
                queryWord2 = reqs[1].toLowerCase();
                AbstractHit[] hits = searcher.search(new Term(queryWord1), new Term(queryWord2), freqSorter);
                if(hits == null) System.out.println("未搜索到任何结果");
                else for(AbstractHit h: hits) {
                    System.out.println("--------------------------------------------------------------");
                    System.out.println(h.toString());
                }
                System.out.println("--------------------------------------------------------------");
                System.out.print("请输入需要查询的单词: ");

            } else if(reqs.length == 3){        // 查询两个单词，中间用&表示“与”，|表示或

                if(reqs[1].equals("&")){     // 与
                    String queryWord1 = "",queryWord2 = "";
                    queryWord1 = reqs[0].toLowerCase();
                    queryWord2 = reqs[2].toLowerCase();
                    AbstractHit[] hits = searcher.search(new Term(queryWord1), new Term(queryWord2),
                            freqSorter, AbstractIndexSearcher.LogicalCombination.AND);
                    if(hits == null || hits.length < 1) System.out.println("未搜索到任何结果");
                    else for(AbstractHit h: hits) {
                        System.out.println("--------------------------------------------------------------");
                        System.out.println(h.toString());
                    }
                    System.out.println("--------------------------------------------------------------");

                } else if(reqs[1].equals("|") ){  // 或
                    String queryWord1 = "",queryWord2 = "";
                    queryWord1 = reqs[0].toLowerCase();
                    queryWord2 = reqs[2].toLowerCase();
                    AbstractHit[] hits = searcher.search(new Term(queryWord1), new Term(queryWord2),
                            freqSorter, AbstractIndexSearcher.LogicalCombination.OR);
                    if(hits == null || hits.length < 1) System.out.println("未搜索到任何结果");
                    else for(AbstractHit h: hits) {
                        System.out.println("--------------------------------------------------------------");
                        System.out.println(h.toString());
                    }
                    System.out.println("--------------------------------------------------------------");
                } else {
                    System.out.println("\033[31m逻辑关系解析失败\033[0m");
                }
                System.out.print("请输入需要查询的单词: ");

            } else {
                System.out.println("输入单词数过多");
                System.out.print("请输入需要查询的单词: ");
            }
        }
    }
}

