package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;

/**
 * AbstractIndex的具体实现类
 * AbstractIndex是内存中的倒排索引对象的抽象父类.
 *      一个倒排索引对象包含了一个文档集合的倒排索引.
 *      内存中的倒排索引结构为HashMap，key为Term对象，value为对应的PostingList对象.
 *      另外在AbstractIndex里还定义了从docId和docPath之间的映射关系.
 *      必须实现下面接口:
 *          FileSerializable：可序列化到文件或从文件反序列化.
 */
public class Index extends AbstractIndex {
    /**
     * 无参构造函数
     */
    public Index(){
    }

    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuffer str = new StringBuffer("docIdToDocPath:\n");
        Iterator<Map.Entry<Integer,String>>it = docIdToDocPathMapping.entrySet().iterator();
        while(it.hasNext()){   //遍历第一个map
            Map.Entry<Integer,String> entry = it.next();
            str.append("docId: "+entry.getKey()+", path = "+entry.getValue()).append("\n");
        }
        str.append("termToPosting:\n");

        Iterator<Map.Entry<AbstractTerm, AbstractPostingList>> it1 = termToPostingListMapping.entrySet().iterator();
        while (it1.hasNext()){  //遍历第二个map
            Map.Entry<AbstractTerm, AbstractPostingList> entry1 = it1.next();
            str.append(entry1.getKey()+"= "+entry1.getValue()+",").append("\n");
        }
        return str.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        docIdToDocPathMapping.put(document.getDocId(),document.getDocPath());
        //map给出单词出现的位置
        Map<AbstractTerm, List<Integer>> map = new HashMap<>();
        for(AbstractTermTuple termTuple:document.getTuples()){
            if(map.get(termTuple.term) == null){ //该单词还未被加入map
                map.put(termTuple.term, new ArrayList<Integer>());
            }
            map.get(termTuple.term).add(termTuple.curPos);
        }
        //更新倒排索引
        for(AbstractTerm term:map.keySet()){
            if(termToPostingListMapping.get(term)==null){
                termToPostingListMapping.put(term,new PostingList());
            }
            termToPostingListMapping.get(term).add(
                    new Posting(document.getDocId(),map.get(term).size(),map.get(term))
            );
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
            readObject(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            writeObject(output);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return new HashSet<AbstractTerm>(termToPostingListMapping.keySet());
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for(AbstractPostingList list:termToPostingListMapping.values()){
            list.sort();        //PostingList排序
            for(int i=0;i<list.size();i++){
                list.get(i).sort();     //position排序
            }
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.termToPostingListMapping);
            out.writeObject(this.docIdToDocPathMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
            this.termToPostingListMapping = (Map<AbstractTerm, AbstractPostingList>)in.readObject();
            this.docIdToDocPathMapping = (Map<Integer, String>)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
