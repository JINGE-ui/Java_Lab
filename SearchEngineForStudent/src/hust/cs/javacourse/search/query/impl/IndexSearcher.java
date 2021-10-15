package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.*;

/**
 * 抽象类AbstractIndexSearcher的子类
 * 实现功能：检索具体实现
 */
public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        index.load(new File(indexFile));
        index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        AbstractPostingList plist = this.index.search(queryTerm);
        if (plist != null) {
            Map<AbstractTerm, AbstractPosting> termPostingMapping = new HashMap<>();
            AbstractHit[] hits = new AbstractHit[plist.size()];     //plist.size()是存在单词queryTerm的文档数目
            for (int i = 0; i < plist.size(); i++) {
                AbstractPosting posting = plist.get(i);
                termPostingMapping.put(queryTerm, posting);
                hits[i] = new Hit(posting.getDocId(), this.index.getDocName(posting.getDocId()), termPostingMapping);       // 这里传文件名即可，不然会找不到文件，详情参见fileUtil中的read方法
                hits[i].setScore(sorter.score(hits[i]));
                termPostingMapping.clear();
            }
            sorter.sort(Arrays.asList(hits));   //根据得分从高到低排序
            return hits;
        } else {
            return null;
        }

    }



    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        AbstractPostingList plist1 = this.index.search(queryTerm1);
        AbstractPostingList plist2 = this.index.search(queryTerm2);
        Map<AbstractTerm, AbstractPosting> termPostingMapping = new HashMap<>();
        ArrayList<AbstractHit> hits = new ArrayList<>();
        switch (combine) {
            case OR:
                if (plist1 != null) {       //将单词queryTerm1的命中信息加入hits
                    for (int i = 0; i < plist1.size(); i++) {
                        AbstractPosting posting = plist1.get(i);
                        termPostingMapping.put(queryTerm1, posting);
                        hits.add(new Hit(posting.getDocId(), this.index.getDocName(posting.getDocId()), termPostingMapping));     // 这里传文件名即可，不然会找不到文件，详情参见fileUtil中的read方法
                        hits.get(i).setScore(sorter.score(hits.get(i)));
                        termPostingMapping.clear();
                    }
                }
                if (plist2 != null) {
                    for (int i = 0; i < plist2.size(); i++) {
                        int flag = 0;
                        AbstractPosting posting = plist2.get(i);
                        for (int j = 0; j < hits.size(); j++) {
                            AbstractHit item = hits.get(j);
                            if (item.getDocId() == posting.getDocId()) {        //单词1和单词2所在的文档相同
                                item.getTermPostingMapping().put(queryTerm2, posting);
                                item.setScore(sorter.score(item));
                                flag = 1;
                            }
                        }
                        if (flag == 0) {
                            termPostingMapping.put(queryTerm2, posting);
                            hits.add(new Hit(posting.getDocId(), this.index.getDocName(posting.getDocId()), termPostingMapping));       //单词1和单词2所在文档不相同，则加入hits
                            hits.get(hits.size()-1).setScore(sorter.score(hits.get(hits.size()-1)));
                            termPostingMapping.clear();
                        }
                    }
                }
                break;
            case AND:
                if (plist1 != null && plist2 != null) {
                    for (int i = 0; i < plist1.size(); i++) {
                        AbstractPosting posting = plist1.get(i);
                        for (int j = 0; j < plist2.size(); j++) {
                            AbstractPosting posting1 = plist2.get(j);
                            if (posting.getDocId() == posting1.getDocId()) {    //两个单词所在文档相同，才加入hits
                                termPostingMapping.put(queryTerm1, posting);
                                termPostingMapping.put(queryTerm2, posting1);
                                hits.add(new Hit(posting.getDocId(), this.index.getDocName(posting.getDocId()), termPostingMapping));
                                hits.get(i).setScore(sorter.score(hits.get(i)));
                                termPostingMapping.clear();
                            }
                        }
                    }
                }
                break;
        }

        sorter.sort(hits);
        return hits.toArray(new AbstractHit[hits.size()]);
    }




    /**
     * 查询两个在文中相邻出现的单词（进阶功能）
     * @param queryTerm1 ：第一个单词
     * @param queryTerm2 ：第二个单词
     * @param sorter ：排序器
     * @return ：查询结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter) {
        AbstractPostingList postList1 = index.search(queryTerm1);
        AbstractPostingList postList2 = index.search(queryTerm2);
        if(postList1 == null || postList2 == null) return null;
        List<AbstractHit> hitArray = new ArrayList<AbstractHit>();
        int i=0, j=0;
        while(i < postList1.size() && j < postList2.size()) {
            AbstractPosting post1 = postList1.get(i);
            AbstractPosting post2 = postList2.get(j);
            // 这里默认索引中的数据都是按文档ID从小到大排序了
            if (post1.getDocId() == post2.getDocId()) {
                List<Integer> pos1 = post1.getPositions();
                List<Integer> pos2 = post2.getPositions();
                int a = 0, b = 0;
                List<Integer> positions = new ArrayList<Integer>();     // 存放连续两个单词出现的位置
                while(a < pos1.size() && b < pos2.size()){
                    int p1 = pos1.get(a);
                    int p2 = pos2.get(b);
                    if(p1 == p2-1){
                        positions.add(p1);
                        a++;    b++;
                    } else if(p1 < p2-1){
                        a++;
                    } else {
                        b++;
                    }
                }
                if(positions.size() > 0) {      // 否则会出现score = 0.0的情况
                    String path = index.getDocName(post1.getDocId());
                    Map<AbstractTerm, AbstractPosting> mp =
                            new HashMap<AbstractTerm, AbstractPosting>();
                    // 把两个单词合并放入映射表中，中间用一个空格隔开，位置则使用两个单词中第一个单词所在的位置
                    // 这样在高亮显示的时候就可以了（要注意把空格替换成\\s，以避免原文中相邻单词直接有多个空格的情况）
                    mp.put(new Term(queryTerm1.getContent() + " " + queryTerm2.getContent()),
                            new Posting(post1.getDocId(), positions.size(), positions));
                    AbstractHit h = new Hit(post1.getDocId(), path, mp);
                    h.setScore(sorter.score(h));        // 先设置分数
                    hitArray.add(h);
                }
                i++;    j++;
            } else if (post1.getDocId() < post2.getDocId()) {
                i++;
            } else {        // post1 > post2
                j++;
            }
        }
        if(hitArray.size() < 1) return null;
        new SimpleSorter().sort(hitArray);
        return (AbstractHit[]) hitArray.toArray(new Hit[0]);
    }
}
