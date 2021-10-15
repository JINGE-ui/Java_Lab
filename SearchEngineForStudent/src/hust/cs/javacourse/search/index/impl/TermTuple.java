package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.AbstractTermTuple;
/**
 * <pre>
 * AbstractTermTuple是所有TermTuple对象的抽象父类.
 *      一个TermTuple对象为三元组(单词，出现频率，出现的当前位置).
 *      当解析一个文档时，每解析到一个单词，应该产生一个三元组，其中freq始终为1(因为单词出现了一次).
 * </pre>
 *
 *
 */
public class TermTuple extends AbstractTermTuple {
    /**
     * 无参构造函数
     */
    public TermTuple(){
    }

    /**
     * 使用term和curPos来构造TermTuple
     * @param term ：单词
     * @param curPos ：位置
     */
    public TermTuple(AbstractTerm term,int curPos){
        this.term = term;
        this.curPos = curPos;
    }

    /**
     * 使用String类型代替Term，更便于使用
     * @param content ：单词内容
     * @param curPos ：位置
     */
    public TermTuple(String content,int curPos){
        this.term = new Term(content);
        this.curPos = curPos;
    }

    /**
     * 判断二个三元组内容是否相同
     * @param obj ：要比较的另外一个三元组
     * @return 如果内容相等（三个属性内容都相等）返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TermTuple){
            return term.equals(((TermTuple) obj).term) && freq==((TermTuple) obj).freq &&
                    curPos==((TermTuple) obj).curPos;
        }else{
            return false;
        }
    }
    /**
     * 获得三元组的字符串表示
     * @return ： 三元组的字符串表示
     */
    @Override
    public String toString() {
        return "term="+term+",freq="+freq+",curPos="+curPos;
    }
}
