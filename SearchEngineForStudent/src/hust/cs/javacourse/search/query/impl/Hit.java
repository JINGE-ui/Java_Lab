package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.util.Config;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抽象类AbstractHit的子类，
 * 存储搜索命中结果
 */
public class Hit extends AbstractHit {
    /**
     *无参构造函数
     */
    public Hit(){
    }

    /**
     * 构造函数
     * @param docId     : 文档id
     * @param docPath   : 文档绝对路径
     */
    public Hit(int docId,String docPath){
        super(docId,docPath);
    }

    /**
     * 构造函数
     * @param docId              ：文档id
     * @param docPath            ：文档绝对路径
     * @param termPostingMapping ：命中的三元组列表
     */
    public Hit(int docId, String docPath, Map<AbstractTerm, AbstractPosting> termPostingMapping) {
        super(docId, docPath, termPostingMapping);
    }

    /**
     * 获得文档id
     *
     * @return ： 文档id
     */
    @Override
    public int getDocId() {
        return docId;
    }

    /**
     * 获得文档绝对路径
     *
     * @return ：文档绝对路径
     */
    @Override
    public String getDocPath() {
        return docPath;
    }

    /**
     * 获得文档内容
     *
     * @return ： 文档内容
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * 获得文档得分
     *
     * @return ： 文档得分
     */
    @Override
    public double getScore() {
        return score;
    }

    /**
     * 设置文档内容
     *
     * @param content ：文档内容
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 设置文档得分
     *
     * @param score ：文档得分
     */
    @Override
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * 获得命中的单词和对应的Posting键值对
     *
     * @return ：命中的单词和对应的Posting键值对
     */
    @Override
    public Map<AbstractTerm, AbstractPosting> getTermPostingMapping() {
        return termPostingMapping;
    }


    /**
     * 获得命中结果的字符串表示, 用于显示搜索结果.
     * 高亮显示搜索关键词
     * @return : 命中结果的字符串表示
     */
    @Override
    public String toString() {
        String content_o = content;
        String tmp = content.toLowerCase();
        for(AbstractTerm t : this.termPostingMapping.keySet()){
            String regex = t.getContent();      // 高亮
            if(tmp.indexOf(regex)!=-1) {
                String regex1 = content.substring(tmp.indexOf(regex), tmp.indexOf(regex) + regex.length());
                content_o = content_o.replaceAll(regex1.replaceAll("\\s", "\\\\s"),
                        "\033[32m" + regex1 + "\033[0m");
            }
        }



        //content_o = content_o.replaceAll(regex.replaceAll("\\s", "\\\\s"),
        //        "\033[32m" + regex + "\033[0m");
        return "Hit{\n" +
                "docId=" + docId + "\n"+
                "docPath='" + docPath + "\n" +
                "content='" + content_o + "\n" +
                "score=" + -score +"\n"+
                "termPostingMapping:\n" + termPostingMapping +"\n"+
                '}';
    }

    /**
     * 比较二个命中结果的大小，根据score比较
     *
     * @param o ：要比较的名字结果
     * @return ：二个命中结果得分的差值
     */
    @Override
    public int compareTo(AbstractHit o) {
        return (int)Math.round(score-o.getScore());
    }
}
