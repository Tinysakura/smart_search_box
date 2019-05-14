package com.tinysakura.smartsearchbox.endpoint;

import com.tinysakura.smartsearchbox.common.Pagination;
import com.tinysakura.smartsearchbox.common.PaginationResponseView;
import com.tinysakura.smartsearchbox.common.ResponseView;
import com.tinysakura.smartsearchbox.common.entity.DocumentScore;
import com.tinysakura.smartsearchbox.config.prop.IndexProp;
import com.tinysakura.smartsearchbox.config.prop.SearchPromptProp;
import com.tinysakura.smartsearchbox.constant.enums.ResponseCodeEnum;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import com.tinysakura.smartsearchbox.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/14
 */
@RestController
public class SearchBox {

    @Autowired
    private SearchPromptProp searchPromptProp;

    @Autowired
    private RedisClientService redisClientService;

    @Autowired
    private AnalyzerService analyzerService;

    @Autowired
    private Launch launch;

    @Autowired
    private ElkClientService elkClientService;

    @Autowired
    private IndexProp indexProp;

    /**
     * 使用webSocket与前端通信，根据用户输入的关键词进行搜索提示
     * @param indexName
     * @param keyword
     * @param index
     * @param size
     * @return
     */
    @MessageMapping("/search_box/search_prompt")
    @SendTo("/search_prompt/prompt")
    public PaginationResponseView searchPromptWithWs(@RequestParam("index") String indexName,
                                                 @RequestParam("keyword") String keyword,
                                                 @RequestParam("index") Integer index,
                                                 @RequestParam("size") Integer size) {
        Set<String> result = searchPromptResult(indexName, keyword, index, size);
        zSetScoreIncr(keyword, indexName);
        return wrap(index, size, result);
    }

    /**
     * 使用webSocket与前端通信，根据用户输入的关键词进行搜索提示
     * @param indexName
     * @param keyword
     * @param index
     * @param size
     * @return
     */
    @RequestMapping("/search_box/{index}/search_prompt")
    @SendTo("/search_prompt/prompt")
    public PaginationResponseView searchPrompt(@PathVariable("index") String indexName,
                                                     @RequestParam("keyword") String keyword,
                                                     @RequestParam("index") Integer index,
                                                     @RequestParam("size") Integer size) {
        Set<String> result = searchPromptResult(indexName, keyword, index, size);
        zSetScoreIncr(keyword, indexName);
        return wrap(index, size, result);
    }

    /**
     * 索引文档查询
     * @param indexName
     * @param documentType
     * @param keyword
     * @param index
     * @param size
     * @param fields
     * @return
     */
    @GetMapping("/search_box/{index}/search_document")
    public PaginationResponseView searchDocument(@PathVariable("index") String indexName,
                                                 @RequestParam("documentType") Integer documentType,
                                                 @RequestParam("keyword") String keyword,
                                                 @RequestParam("index") Integer index,
                                                 @RequestParam("size") Integer size, String[] fields) {
        String className = indexProp.getClasses()[documentType];
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<DocumentScore> documentList = elkClientService.multiMatchQuery(indexName, null, fields, keyword, indexProp.getDefaultAnalyzer(), index, size, clazz, indexProp.getHighlightPreTags(), indexProp.getHighlightPostTags());

        return wrap(index, size, documentList);
    }

    /**
     * 整理文档对应的zset的端点，配合crontab定时请求端点达到定时整理的需求
     * @return
     */
    @GetMapping("/search_box/timing/clean_up/document_zset")
    public ResponseView timingCleanUp() {
        launch.startDocumentZsetCleanUpTimingTask();

        ResponseView responseView = new ResponseView();
        responseView.setCode(ResponseCodeEnum.OK.getCode());
        responseView.setMessage(ResponseCodeEnum.OK.getValue());

        return responseView;
    }

    private Set<String> searchPromptResult(String indexName, String keyword, Integer index, Integer size) {
        Integer from = -1 - (index - 1) * size * 2;
        String documentZsetKey = StringUtil.documentZSetKey(indexName, keyword);
        String behaviorZsetKey = StringUtil.behaviorZSetKey(indexName, keyword);
        Integer dTo = from - 2 * new Long(Math.round(searchPromptProp.getDocumentRatio() * size)).intValue();
        Integer bTo = from - 2 * new Long(Math.round(searchPromptProp.getBehaviorRatio() * size)).intValue();

        Set<String> documentPrompt = redisClientService.zRange(documentZsetKey, from, dTo);
        Set<String> behaviorPrompt = redisClientService.zRange(behaviorZsetKey, from, bTo);
        documentPrompt.addAll(behaviorPrompt);
        String[] tmp = documentPrompt.toArray(new String[]{});
        // help gc
        documentPrompt =  null;
        behaviorPrompt = null;
        Set<String> result = new HashSet<>();

        for (int i = 0; i < tmp.length; i += 2) {
            result.add(tmp[i]);
        }

        return result;
    }

    private PaginationResponseView wrap(Integer index, Integer size, Object result) {
        Pagination pagination = new Pagination();
        pagination.setIndex(index);
        pagination.setSize(size);

        PaginationResponseView<Object> responseView = new PaginationResponseView<>();
        responseView.setCode(ResponseCodeEnum.OK.getCode());
        responseView.setMessage(ResponseCodeEnum.OK.getValue());
        responseView.setPagination(pagination);
        responseView.setResults(result);

        return responseView;
    }

    /**
     * 根据用户行为更新用户行为相关zset中keyword元素的score
     * @param keyword
     */
    private void zSetScoreIncr(String keyword, String index) {
        String behaviorZsetKey = StringUtil.behaviorZSetKey(index, keyword);
        String[] analyzer = analyzerService.analyzer(keyword);

        for (String str : analyzer) {
            redisClientService.zincrby(behaviorZsetKey, 1d, str);
        }
    }
}