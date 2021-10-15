package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * AbstractIndexBuilder是索引构造器的抽象父类
 *      需要实例化一个具体子类对象完成索引构造的工作
 */
public class IndexBuilder extends AbstractIndexBuilder {
    /**
     * 无参构造函数
     * @param docBuilder：Document对象
     */
    public IndexBuilder(AbstractDocumentBuilder docBuilder){
        super(docBuilder);
    }

    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引.
     *      需要遍历和解析目录下的每个文本文件, 得到对应的Document对象，再依次加入到索引，并将索引保存到文件.
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    @Override
    public AbstractIndex buildIndex(String rootDirectory) {
        AbstractIndex index = new Index();
        //遍历目录中的文件
        for(String path: FileUtil.list(rootDirectory)){
            AbstractDocument document = docBuilder.build(docId++,path,new File(path));
            index.addDocument(document);
        }
        //序列化信息写入 index.dat
        File file = new File(Config.INDEX_DIR + "index.dat");
        if(!file.exists()){
            try {
                if(!file.createNewFile()) throw new IOException();
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        index.save(file);
        return index;
    }
}
