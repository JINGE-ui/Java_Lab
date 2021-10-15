package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象类AbstractTermTupleScanner的子类
 * 需要实现next方法获得文本文件里的三元组
 */
public class TermTupleScanner extends AbstractTermTupleScanner {
    private int curpos=0;       //单词在文档中所处的位置
    private List<TermTuple> tuples = new ArrayList<>();     //一篇文档的所有三元组的集合

    /**
     * 获取文档的三元组集合
     * @return 文档的所有三元组列表
     */
    public List<TermTuple> getTerms() {
        return tuples;
    }

    /**
     * 无参构造函数，可以缺省
     */
    public TermTupleScanner(){
    }

    /**
     * 构造函数
     * @param input：缓冲输入
     */
    public TermTupleScanner(BufferedReader input){
        super(input);
        try {
            String str = input.readLine();
            while (str != null) {        //循环直至文档被读取完毕
                StringSplitter splitter = new StringSplitter();
                splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);  //分割
                List<String> parts = splitter.splitByRegex(str);
                int i=0;
                for (i = 0; i < parts.size(); i++) {
                    if (!parts.get(i).equals("")){      //如果单词parts[i]不为空
                        //默认大小写不敏感
                        this.tuples.add(new TermTuple(new Term(parts.get(i).toLowerCase()), this.curpos++));
                    }
                }

                str = input.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractTermTuple next() {
        if (this.tuples.size()!=0){
            return tuples.remove(0);
        } else {
            return null;
        }
    }
}
