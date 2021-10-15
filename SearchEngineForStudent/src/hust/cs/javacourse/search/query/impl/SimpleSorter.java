package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.query.AbstractHit;

import hust.cs.javacourse.search.query.Sort;
import java.util.Collections;
import java.util.List;

/**
 * 实现了Sort接口
 * 功能:  对命中结果基于文档得分排序
 */
public class SimpleSorter implements Sort{
    /**
     * 对命中结果集合根据文档得分排序
     *
     * @param hits ：命中结果集合
     */
    @Override
    public void sort(List<AbstractHit> hits) {
        Collections.sort(hits);
    }

    /**
     * <pre>
     * 计算命中文档的得分, 作为命中结果排序的依据.
     *      排序策略为根据文档命中频率作为得分
     * @param hit ：命中文档
     * @return ：命中文档的得分的相反数
     * </pre>
     */
    @Override
    public double score(AbstractHit hit) {
        double score = 0;
        for(AbstractPosting posting: hit.getTermPostingMapping().values()){
            score = score + posting.getFreq();
        }
        return -score;  //因为API中的sort函数默认从小到大排序，所以取反
    }
}
