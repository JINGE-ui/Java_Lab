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
import hust.cs.javacourse.search.util.FileUtil;

import static java.lang.System.exit;

public class WriteIndexFile {
    public static void main(String[] args){

        AbstractIndexBuilder indexBuilder = new IndexBuilder(new DocumentBuilder());
        /*
        System.out.println("将索引写到文本文件:");

        //功能测试文件
        AbstractIndex index = indexBuilder.buildIndex(Config.DOC_DIR+"function/");
        if (index.getDictionary().isEmpty()){
            System.out.println("Warning: 索引表为空！");
        }
        System.out.println("倒排索引内容：");
        System.out.println(index.toString());
        System.out.println("正在写入..............................");
        String WritePath = Config.PROJECT_HOME_DIR+"/bin/output/function/index.txt/";
        FileUtil.write(index.toString(),WritePath);


        //真实测试文件
        AbstractIndexBuilder indexBuilder2 = new IndexBuilder(new DocumentBuilder());
        AbstractIndex index2 = indexBuilder2.buildIndex(Config.DOC_DIR+"real/");    //已经保存到序列化文件
        if (index2.getDictionary().isEmpty()){
            System.out.println("Warning: 索引表为空！");
        }
        System.out.println("倒排索引内容：");
        System.out.println(index2.toString());
        System.out.println("正在写入..............................");
        String WritePath2 = Config.PROJECT_HOME_DIR+"/bin/output/real/index.txt/";
        FileUtil.write(index2.toString(),WritePath2);

         */


        //修改DocumentBuilder类中的Build方法，取消单词过滤，并将上述的写入文件名改为indexWithoutFilter，即可得到未经过滤的索引内容
        AbstractIndex index3 = indexBuilder.buildIndex(Config.DOC_DIR+"real/");
        String WritePath3 = Config.PROJECT_HOME_DIR+"/bin/output/SearchResult.txt/";
        if (index3.getDictionary().isEmpty()){
            System.out.println("Warning: 索引表为空！");
            exit(1);
        }
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(Config.INDEX_DIR + "index.dat");
        String[] Words={"coronavirus",
                "wales",
                "circumstances",
                "government",
                "google",
                "health",
                "luxembourg",
                "recognition",
                "wales",
                "all-star",
                "kobe"};
        SimpleSorter freqSorter = new SimpleSorter();
        StringBuffer buff = new StringBuffer();
        buff.append("**************************************************************************************\n");
        for(String queryWord:Words){
            AbstractHit[] hits = searcher.search(new Term(queryWord), freqSorter);
            buff.append(queryWord+": \n");
            if(hits == null){
                buff.append("未搜索到任何结果\n");
                buff.append("**************************************************************************************\n");
            }
            else for(AbstractHit h: hits) {
                buff.append("--------------------------------------------------------------\n");
                buff.append(h.toString());
            }
            buff.append("\n**************************************************************************************\n");
        }
        FileUtil.write(buff.toString(),WritePath3);

    }
}
